package com.personal.bookstoreapi.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record RentalResponseDTO(
        Long rentalId,
        Long bookItemId,
        String bookTitle,
        Instant startAt,
        Instant endAt,
        BigDecimal totalAmount
) {
}