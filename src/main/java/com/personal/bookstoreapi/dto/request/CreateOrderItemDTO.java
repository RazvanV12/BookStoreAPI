package com.personal.bookstoreapi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record CreateOrderItemDTO (

        @Positive Long bookItemId,
        @Min(1) Integer quantity
){
}
