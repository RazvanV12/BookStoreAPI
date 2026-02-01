package com.personal.bookstoreapi.aop;

import com.personal.bookstoreapi.dto.response.OrderResponseDTO;
import com.personal.bookstoreapi.events.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class OrderEventsAspect {

    private final ApplicationEventPublisher publisher;

    @AfterReturning(
            value = "execution(* com.personal.bookstoreapi.service.OrderService.createOrder(..))",
            returning = "result"
    )
    public void afterCreateOrder(Object result) {
        if (!(result instanceof OrderResponseDTO dto)) return;

        if ("PAID".equals(dto.status())) {
            publisher.publishEvent(new OrderPaidEvent(dto.orderId()));
        }
    }
}