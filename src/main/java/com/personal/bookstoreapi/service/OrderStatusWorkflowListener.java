package com.personal.bookstoreapi.service;

import com.personal.bookstoreapi.domain.entity.Order;
import com.personal.bookstoreapi.domain.enums.OrderStatus;
import com.personal.bookstoreapi.events.OrderPaidEvent;
import com.personal.bookstoreapi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class OrderStatusWorkflowListener {

    private final OrderRepository orderRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPaid(OrderPaidEvent event) throws InterruptedException {
        Long orderId = event.getOrderId();

        Thread.sleep(30_000);
        move(orderId, OrderStatus.PAID, OrderStatus.SHIPPING);

        Thread.sleep(30_000);
        move(orderId, OrderStatus.SHIPPING, OrderStatus.DELIVERED);
    }

    @Transactional
    public void move(Long orderId, OrderStatus expected, OrderStatus next) {
        Order order = orderRepository.findById(orderId)
                                     .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() != expected) return;

        order.setStatus(next);
        orderRepository.save(order);
    }
}