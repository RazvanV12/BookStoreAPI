package com.personal.bookstoreapi.mapper;

import com.personal.bookstoreapi.domain.entity.Author;
import com.personal.bookstoreapi.domain.entity.Book;
import com.personal.bookstoreapi.domain.entity.BookItem;
import com.personal.bookstoreapi.domain.entity.DigitalBookItem;
import com.personal.bookstoreapi.domain.entity.PhysicalBookItem;
import com.personal.bookstoreapi.dto.response.AuthorResponseDTO;
import com.personal.bookstoreapi.dto.response.BookDetailsDTO;
import com.personal.bookstoreapi.dto.response.BookItemDTO;
import com.personal.bookstoreapi.dto.response.BookListDTO;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CatalogMapper {

    public BookListDTO toBookListDTO(Book book) {
        return new BookListDTO(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getLanguage(),
                book.getPublicationYear(),
                book.getCoverUrl(),
                toAuthorDTOs(book.getAuthors())
        );
    }

    public BookDetailsDTO toBookDetailsDTO(Book book) {
        return new BookDetailsDTO(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getIsbn(),
                book.getLanguage(),
                book.getPublicationYear(),
                book.getCoverUrl(),
                toAuthorDTOs(book.getAuthors())
        );
    }

    public BookItemDTO toBookItemDTO(BookItem item) {

        String fileFormat = null;
        Integer stockQuantity = null;

        String type;
        if (item instanceof DigitalBookItem digital) {
            type = "DIGITAL";
            fileFormat = digital.getFileFormat();
        } else if (item instanceof PhysicalBookItem physical) {
            type = "PHYSICAL";
            stockQuantity = physical.getStockQuantity();
        } else {
            type = item.getClass()
                       .getSimpleName()
                       .toUpperCase();
        }

        return new BookItemDTO(
                item.getId(),
                type,
                item.getPrice(),
                item.getRentPrice(),
                item.isAvailableForRent(),
                fileFormat,
                stockQuantity
        );
    }

    private Set<AuthorResponseDTO> toAuthorDTOs(Set<Author> authors) {
        if (authors == null) return Set.of();
        return authors.stream()
                      .map(a -> new AuthorResponseDTO(a.getId(), a.getName()))
                      .collect(Collectors.toSet());
    }
}
