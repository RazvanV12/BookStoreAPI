package com.personal.bookstoreapi.dto.response;

import java.util.Set;

public record BookDetailsDTO(
        Long id,
        String title,
        String description,
        String isbn,
        String language,
        Integer publicationYear,
        String coverUrl,
        Set<AuthorResponseDTO> authors
) {
}
