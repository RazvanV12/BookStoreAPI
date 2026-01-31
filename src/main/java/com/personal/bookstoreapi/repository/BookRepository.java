package com.personal.bookstoreapi.repository;

import com.personal.bookstoreapi.domain.entity.Book;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByTitleIgnoreCase(String title);

    Optional<Book> findById(@NonNull Long id);

    List<Book> findAll();
}