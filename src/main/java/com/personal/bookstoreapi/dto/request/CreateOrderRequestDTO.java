package com.personal.bookstoreapi.dto.request;

import java.util.List;

public record CreateOrderRequestDTO(
        List<CreateOrderItemDTO> items
) {
}
