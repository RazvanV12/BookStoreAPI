package com.personal.bookstoreapi.repository;

import com.personal.bookstoreapi.domain.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByTitleIgnoreCase(String title);
}