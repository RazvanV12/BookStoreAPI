package com.personal.bookstoreapi.dto.response;

import java.math.BigDecimal;

public record BookItemDTO(
        Long id,
        String type,              // DIGITAL / PHYSICAL
        BigDecimal price,
        BigDecimal rentPrice,
        boolean availableForRent,

        // DIGITAL
        String fileFormat,

        // PHYSICAL
        Integer stockQuantity
) {
}
