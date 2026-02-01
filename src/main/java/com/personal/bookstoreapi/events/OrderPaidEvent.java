package com.personal.bookstoreapi.events;

import lombok.Getter;

@Getter
public class OrderPaidEvent {
    private final Long orderId;

    public OrderPaidEvent(Long orderId) {
        this.orderId = orderId;
    }
}