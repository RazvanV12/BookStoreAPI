package com.personal.bookstoreapi.repository;

import com.personal.bookstoreapi.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUserId(Long userId);
}
