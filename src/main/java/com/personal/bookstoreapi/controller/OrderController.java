package com.personal.bookstoreapi.controller;

import com.personal.bookstoreapi.dto.request.CreateOrderRequestDTO;
import com.personal.bookstoreapi.dto.response.OrderDetailsResponseDTO;
import com.personal.bookstoreapi.dto.response.OrderResponseDTO;
import com.personal.bookstoreapi.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO createOrder(
            @RequestBody @Valid CreateOrderRequestDTO request
    ) {
        return orderService.createOrder(request);
    }

    @GetMapping("/me")
    public List<OrderResponseDTO> getMyOrders() {
        return orderService.getMyOrders();
    }

    @GetMapping("/{orderId}")
    public OrderDetailsResponseDTO getOrderDetails(
            @PathVariable Long orderId
    ) {
        return orderService.getOrderDetails(orderId);
    }
}