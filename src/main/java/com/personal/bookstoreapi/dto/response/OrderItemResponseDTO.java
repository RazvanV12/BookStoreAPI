package com.personal.bookstoreapi.dto.response;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long bookItemId,
        String bookTitle,
        Integer quantity,
        BigDecimal unitPrice
) {
}
