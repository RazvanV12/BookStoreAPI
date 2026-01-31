package com.personal.bookstoreapi.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record OrderDetailsResponseDTO(

        Long orderId,
        String status,
        BigDecimal totalAmount,
        List<OrderItemResponseDTO> items
) {
}
