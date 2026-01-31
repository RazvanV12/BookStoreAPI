package com.personal.bookstoreapi.repository;

import com.personal.bookstoreapi.domain.entity.BookItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookItemRepository extends JpaRepository<BookItem, Long> {
}