package com.personal.bookstoreapi.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateRentalRequestDTO(
        @NotNull(message = "bookItemId is required")
        Long bookItemId,

        @NotNull(message = "days is required")
        @Min(value = 1, message = "days must be at least 1")
        @Max(value = 90, message = "days must be at most 90")
        Integer days
) {
}
