package com.pie.demo.entities;

import java.time.Instant;

import com.pie.demo.enums.LoanState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "loans", indexes = {
        @Index(name = "idx_loans_user_state", columnList = "user_id, state"),
        @Index(name = "idx_loans_book", columnList = "book_id"),
        @Index(name = "idx_loans_state_due", columnList = "state, due_date"),

})
@NoArgsConstructor

public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Column(name = "request_date", nullable = false, updatable = false)
    private Instant requestDate;

    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Column(name = "return_date")
    private Instant returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private LoanState state;

    @PrePersist
    void onCreate() {
        if (requestDate == null) {
            requestDate = Instant.now();
        }
    }
}
