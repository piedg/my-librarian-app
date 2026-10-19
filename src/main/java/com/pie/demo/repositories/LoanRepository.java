package com.pie.demo.repositories;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pie.demo.entities.LoanEntity;
import com.pie.demo.enums.LoanState;

public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

    @Override
    @EntityGraph(attributePaths = { "book", "user" })
    public List<LoanEntity> findAll();

    @EntityGraph(attributePaths = { "book" })
    public List<LoanEntity> findByUserId(Long userId);

    @EntityGraph(attributePaths = { "book" })
    public List<LoanEntity> findByUserIdAndStateIn(Long userId, Collection<LoanState> state);

    @EntityGraph(attributePaths = { "book", "user" })
    public List<LoanEntity> findByState(LoanState state);

    public long countByUserIdAndStateIn(Long userId, Collection<LoanState> states);

    public boolean existsByUserIdAndBookIdAndStateIn(Long userId, Long bookId, Collection<LoanState> states);

    @Query("SELECT l.id FROM LoanEntity l WHERE l.state = :state AND l.dueDate < :now")
    public List<Long> findIdsByStateAndDueDateBefore(@Param("state") LoanState state, @Param("now") Instant now);

    public boolean existsByBookId(Long bookId);
}
