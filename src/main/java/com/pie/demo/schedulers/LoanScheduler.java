package com.pie.demo.schedulers;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pie.demo.services.LoanService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class LoanScheduler {

    private final LoanService loanService;

    @Scheduled(fixedDelayString = "${app.scheduler.expire-reservations.delay-ms:3600000}")
    public void expireReservations() {
        List<Long> ids = loanService.findExpiredReservationIds();

        int expired = 0;
        int failed = 0;

        for (Long id : ids) {
            try {
                if (loanService.expireOne(id)) {
                    expired++;
                }
            } catch (Exception e) {
                failed++;
                log.error("Impossibile far scadere la prenotazione {}", id, e);
            }
        }

        if (!ids.isEmpty()) {
            log.info("Scheduler: candidate = {}, scadute = {}, errori = {}", ids.size(), expired, failed);
        }
    }
}