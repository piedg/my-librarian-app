package com.pie.demo.services;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pie.demo.dtos.loan.LoanResponse;
import com.pie.demo.entities.LoanEntity;
import com.pie.demo.enums.LoanState;
import com.pie.demo.exceptions.*;
import com.pie.demo.repositories.BookRepository;
import com.pie.demo.repositories.LoanRepository;
import com.pie.demo.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanService {

    private static final int MAX_ACTIVE_LOANS = 5;
    private static final Duration RESERVATION_DURATION = Duration.ofDays(3);
    private static final Duration LOAN_DURATION = Duration.ofDays(30);

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // ---------- Prenotazione (anche di più libri) ----------
    @Transactional
    public List<LoanResponse> reserve(Long userId, Set<Long> bookIds) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("id", userId);
        }

        long activeLoans = loanRepository.countByUserIdAndStateIn(userId, LoanState.ACTIVE);
        if (activeLoans + bookIds.size() > MAX_ACTIVE_LOANS) {
            throw new LoanLimitExceededException(MAX_ACTIVE_LOANS, activeLoans, bookIds.size());
        }

        List<LoanResponse> result = new ArrayList<>();
        for (Long bookId : new TreeSet<>(bookIds)) {
            result.add(reserveOne(userId, bookId));
        }

        log.info("Prenotazione: utente = {}, libri = {}", userId, bookIds);
        return result;
    }

    private LoanResponse reserveOne(Long userId, Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("id", bookId);
        }

        if (loanRepository.existsByUserIdAndBookIdAndStateIn(userId, bookId, LoanState.ACTIVE)) {
            throw new DuplicateActiveLoanException(userId, bookId);
        }

        // 0 righe modificate = nessuna copia libera.
        if (bookRepository.decrementAvailableCopies(bookId) == 0) {
            throw new NoCopiesAvailableException(bookId);
        }

        LoanEntity loan = new LoanEntity();
        loan.setUser(userRepository.getReferenceById(userId));
        loan.setBook(bookRepository.getReferenceById(bookId));
        loan.setState(LoanState.RESERVED);
        loan.setDueDate(Instant.now().plus(RESERVATION_DURATION));

        return LoanResponse.from(loanRepository.save(loan));
    }

    // ---------- Ritiro: RESERVED -> BORROWED ----------

    @Transactional
    public LoanResponse pickUp(Long loanId) {
        LoanEntity loan = getOrThrow(loanId);

        if (loan.getState() == LoanState.RESERVED && loan.getDueDate().isBefore(Instant.now())) {
            throw new InvalidLoanStateException("La prenotazione " + loanId + " è scaduta");
        }

        changeState(loan, LoanState.BORROWED);
        loan.setDueDate(Instant.now().plus(LOAN_DURATION));

        log.info("Ritiro: loan = {}", loanId);
        return LoanResponse.from(loan);
    }

    // ---------- Riconsegna: BORROWED -> RETURNED ----------

    @Transactional
    public LoanResponse returnLoan(Long loanId) {
        LoanEntity loan = getOrThrow(loanId);

        changeState(loan, LoanState.RETURNED);
        loan.setReturnDate(Instant.now());
        releaseCopy(loan);

        log.info("Riconsegna: loan = {}", loanId);
        return LoanResponse.from(loan);
    }

    // ---------- Annullamento: RESERVED -> CANCELLED ----------

    @Transactional
    public LoanResponse cancel(Long loanId) {
        LoanEntity loan = getOrThrow(loanId);

        changeState(loan, LoanState.CANCELLED);
        releaseCopy(loan);

        log.info("Prenotazione annullata: loan = {}", loanId);
        return LoanResponse.from(loan);
    }

    // ---------- Scadenza prenotazioni non ritirate (la chiamerà un job) ----------

    @Transactional(readOnly = true)
    public List<Long> findExpiredReservationIds() {
        return loanRepository.findIdsByStateAndDueDateBefore(LoanState.RESERVED, Instant.now());
    }

    /**
     * Fa scadere UNA prenotazione, in una transazione propria.
     * 
     * @return true se è scaduta, false se nel frattempo non era più da
     *         far scadere
     */
    @Transactional
    public boolean expireOne(Long loanId) {
        LoanEntity loan = loanRepository.findById(loanId).orElse(null);

        // ri-controllo: nel frattempo potrebbe essere stata ritirata o annullata
        if (loan == null
                || loan.getState() != LoanState.RESERVED
                || !loan.getDueDate().isBefore(Instant.now())) {
            return false;
        }

        changeState(loan, LoanState.EXPIRED);
        releaseCopy(loan);
        log.info("Prenotazione scaduta: loan = {}", loanId);
        return true;
    }

    // ---------- Letture ----------

    @Transactional(readOnly = true)
    public LoanResponse findById(Long id) {
        return LoanResponse.from(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> findAll() {
        return loanRepository.findAll().stream().map(LoanResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> findByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("id", userId);
        }
        return loanRepository.findByUserId(userId).stream().map(LoanResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> findByState(LoanState state) {
        return loanRepository.findByState(state).stream().map(LoanResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> findByUserAndState(Long userId, LoanState state) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("id", userId);
        }
        return loanRepository.findByUserIdAndStateIn(userId, Set.of(state)).stream()
                .map(LoanResponse::from)
                .toList();
    }

    // ---------- Helper ----------

    /** Unico punto in cui cambia lo stato: applica la macchina a stati. */
    private void changeState(LoanEntity loan, LoanState next) {
        if (!loan.getState().canTransitionTo(next)) {
            throw new InvalidLoanStateException(String.format(
                    "Il prestito %d non può passare da %s a %s", loan.getId(), loan.getState(), next));
        }
        loan.setState(next);
    }

    private void releaseCopy(LoanEntity loan) {
        Long bookId = loan.getBook().getId();
        if (bookRepository.incrementAvailableCopies(bookId) == 0) {
            log.warn("Copie disponibili già al massimo per il libro {}: dati incoerenti?", bookId);
        }
    }

    private LoanEntity getOrThrow(Long id) {
        return loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException("id", id));
    }
}