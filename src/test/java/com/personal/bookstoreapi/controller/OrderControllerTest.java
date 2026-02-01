package com.personal.bookstoreapi.controller;

import com.personal.bookstoreapi.dto.request.CreateOrderRequestDTO;
import com.personal.bookstoreapi.dto.response.OrderDetailsResponseDTO;
import com.personal.bookstoreapi.dto.response.OrderResponseDTO;
import com.personal.bookstoreapi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    OrderService orderService;

    @InjectMocks
    OrderController controller;

    @Test
    void createOrder_delegates() {
        var req = new CreateOrderRequestDTO(List.of());
        when(orderService.createOrder(req)).thenReturn(new OrderResponseDTO(5L, "PAID", new BigDecimal("1.00"), null));
        var out = controller.createOrder(req);
        assertThat(out.orderId()).isEqualTo(5L);
    }

    @Test
    void getMyOrders_delegates() {
        when(orderService.getMyOrders()).thenReturn(List.of(new OrderResponseDTO(6L,"PAID",new BigDecimal("2.00"),null)));
        var out = controller.getMyOrders();
        assertThat(out).hasSize(1);
    }

    @Test
    void getOrderDetails_delegates() {
        when(orderService.getOrderDetails(7L)).thenReturn(new OrderDetailsResponseDTO(7L,"PAID",new BigDecimal("3.00"),List.of()));
        var out = controller.getOrderDetails(7L);
        assertThat(out.orderId()).isEqualTo(7L);
    }
}
