package com.personal.bookstoreapi.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequestDTO(
        List<@NotEmpty CreateOrderItemDTO> items
) {
}
