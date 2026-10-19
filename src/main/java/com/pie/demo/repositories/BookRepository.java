package com.pie.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pie.demo.entities.BookEntity;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    public boolean existsByIsbn(String isbn);

    public boolean existsByIsbnAndIdNot(String isbn, Long id);

    public List<BookEntity> findByTitleContainingIgnoreCase(String title);

    public List<BookEntity> findByAvailableCopiesGreaterThan(int copies);

    public List<BookEntity> findByTitleContainingIgnoreCaseAndAvailableCopiesGreaterThan(String title, int copies);

    public List<BookEntity> findByCategoryId(Long categoryId);

    public List<BookEntity> findByAuthorsId(Long authorId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE BookEntity b SET b.availableCopies = b.availableCopies - 1 "
            + "WHERE b.id = :id AND b.availableCopies > 0")
    int decrementAvailableCopies(@Param("id") Long id);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE BookEntity b SET b.availableCopies = b.availableCopies + 1 "
            + "WHERE b.id = :id AND b.availableCopies < b.totalCopies")
    int incrementAvailableCopies(@Param("id") Long id);
}
