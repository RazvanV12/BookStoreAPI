package com.personal.bookstoreapi.mapper;

import com.personal.bookstoreapi.domain.entity.Author;
import com.personal.bookstoreapi.domain.entity.Book;
import com.personal.bookstoreapi.domain.entity.DigitalBookItem;
import com.personal.bookstoreapi.domain.entity.PhysicalBookItem;
import com.personal.bookstoreapi.dto.response.BookDetailsDTO;
import com.personal.bookstoreapi.dto.response.BookItemDTO;
import com.personal.bookstoreapi.dto.response.BookListDTO;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogMapperTest {

    CatalogMapper mapper = new CatalogMapper();

    @Test
    void toBookListDTO_happy() {
        Book b = new Book(); b.setId(1L); b.setTitle("T"); b.setIsbn("I"); b.setLanguage("EN"); b.setPublicationYear(2020); b.setCoverUrl("c");
        Author a = new Author(); a.setId(2L); a.setName("Auth"); b.setAuthors(Set.of(a));

        BookListDTO dto = mapper.toBookListDTO(b);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.authors()).hasSize(1);
    }

    @Test
    void toBookDetailsDTO_happy() {
        Book b = new Book(); b.setId(3L); b.setTitle("T2");
        BookDetailsDTO dto = mapper.toBookDetailsDTO(b);
        assertThat(dto.id()).isEqualTo(3L);
    }

    @Test
    void toBookItemDTO_digital() {
        DigitalBookItem d = DigitalBookItem.builder().id(5L).fileFormat("PDF").price(null).availableForRent(true).build();
        d.setBook(new Book());
        BookItemDTO dto = mapper.toBookItemDTO(d);
        assertThat(dto.type()).isEqualTo("DIGITAL");
        assertThat(dto.fileFormat()).isEqualTo("PDF");
    }

    @Test
    void toBookItemDTO_physical() {
        PhysicalBookItem p = PhysicalBookItem.builder().id(6L).stockQuantity(4).price(null).availableForRent(false).build();
        p.setBook(new Book());
        BookItemDTO dto = mapper.toBookItemDTO(p);
        assertThat(dto.type()).isEqualTo("PHYSICAL");
        assertThat(dto.stockQuantity()).isEqualTo(4);
    }

    @Test
    void toAuthorDTOs_nullAuthors_returnsEmptySet() {
        Book b = new Book(); b.setAuthors(null);
        BookListDTO dto = mapper.toBookListDTO(b);
        assertThat(dto.authors()).isEmpty();
    }
}
