package com.personal.bookstoreapi.dto.response;

public record RegisterResponseDTO(

        String email,
        String fullName,
        String accessToken
) {
}
