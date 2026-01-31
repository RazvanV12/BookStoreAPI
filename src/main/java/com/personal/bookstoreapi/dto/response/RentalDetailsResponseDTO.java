package com.personal.bookstoreapi.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record RentalDetailsResponseDTO(
        Long rentalId,
        Long bookItemId,
        String bookTitle,
        String type,
        Instant startAt,
        Instant endAt,
        BigDecimal rentPricePerDay,
        BigDecimal totalAmount
) {
}
