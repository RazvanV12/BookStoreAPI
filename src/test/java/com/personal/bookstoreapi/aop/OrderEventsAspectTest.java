package com.personal.bookstoreapi.aop;

import com.personal.bookstoreapi.dto.response.OrderResponseDTO;
import com.personal.bookstoreapi.events.OrderPaidEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class OrderEventsAspectTest {

    ApplicationEventPublisher publisher;
    OrderEventsAspect aspect;

    @BeforeEach
    void setup() {
        publisher = mock(ApplicationEventPublisher.class);
        aspect = new OrderEventsAspect(publisher);
    }

    @Test
    void afterCreateOrder_nonDto_doesNotPublish() {
        aspect.afterCreateOrder("not a dto");
        verifyNoInteractions(publisher);
    }

    @Test
    void afterCreateOrder_orderNotPaid_doesNotPublish() {
        OrderResponseDTO dto = new OrderResponseDTO(1L, "CREATED", new BigDecimal("0.00"), null);
        aspect.afterCreateOrder(dto);
        verifyNoInteractions(publisher);
    }

    @Test
    void afterCreateOrder_orderPaid_publishesOrderPaidEvent() {
        OrderResponseDTO dto = new OrderResponseDTO(7L, "PAID", new BigDecimal("10.00"), null);
        aspect.afterCreateOrder(dto);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publishEvent(captor.capture());
        Object published = captor.getValue();
        assertThat(published).isInstanceOf(OrderPaidEvent.class);
        OrderPaidEvent ev = (OrderPaidEvent) published;
        assertThat(ev.getOrderId()).isEqualTo(7L);
    }
}
