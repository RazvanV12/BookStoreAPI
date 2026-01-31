package com.personal.bookstoreapi.repository;

import com.personal.bookstoreapi.domain.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findAllByUserId(Long userId);

    @Query("""
                select r
                from Rental r
                join fetch r.bookItem bi
                join fetch bi.book b
                where r.id = :rentalId
            """)
    Optional<Rental> findByIdWithBook(@Param("rentalId") Long rentalId);
}
