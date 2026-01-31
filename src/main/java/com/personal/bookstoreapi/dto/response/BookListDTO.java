package com.personal.bookstoreapi.dto.response;

import java.util.Set;

public record BookListDTO(
        Long id,
        String title,
        String isbn,
        String language,
        Integer publicationYear,
        String coverUrl,
        Set<AuthorResponseDTO> authors
) {
}
