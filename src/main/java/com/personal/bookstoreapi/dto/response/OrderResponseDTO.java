package com.personal.bookstoreapi.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponseDTO(
        Long orderId,
        String status,
        BigDecimal totalAmount,
        Instant createdAt
) {
}
