package com.pie.demo.dtos.loan;

import java.time.Instant;

import com.pie.demo.entities.LoanEntity;
import com.pie.demo.enums.LoanState;

public record LoanResponse(Long id, Long userId, Long bookId, String bookTitle,
        Instant startDate, Instant dueDate, Instant returnDate, LoanState state) {

    public static LoanResponse from(LoanEntity loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getUser().getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getRequestDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getState());
    }
}