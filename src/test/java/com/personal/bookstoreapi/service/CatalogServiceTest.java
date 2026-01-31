package com.personal.bookstoreapi.service;

import com.personal.bookstoreapi.domain.entity.Book;
import com.personal.bookstoreapi.dto.response.BookDetailsDTO;
import com.personal.bookstoreapi.dto.response.BookItemDTO;
import com.personal.bookstoreapi.dto.response.BookListDTO;
import com.personal.bookstoreapi.exception.NotFoundException;
import com.personal.bookstoreapi.mapper.CatalogMapper;
import com.personal.bookstoreapi.repository.BookItemRepository;
import com.personal.bookstoreapi.repository.BookRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    BookItemRepository bookItemRepository;

    @Mock
    CatalogMapper mapper;

    @InjectMocks
    CatalogService catalogService;

    @Nested
    class GetAllBooks {
        @Test
        void happy_returnsListOfBookListDTOs() {
            Book b1 = new Book(); b1.setId(1L); b1.setTitle("A");
            Book b2 = new Book(); b2.setId(2L); b2.setTitle("B");

            when(bookRepository.findAll()).thenReturn(List.of(b1,b2));
            when(mapper.toBookListDTO(b1)).thenReturn(new BookListDTO(1L,"A",null,null,null,null,null));
            when(mapper.toBookListDTO(b2)).thenReturn(new BookListDTO(2L,"B",null,null,null,null,null));

            List<BookListDTO> res = catalogService.getAllBooks();
            assertThat(res).hasSize(2);
            assertThat(res.get(0).id()).isEqualTo(1L);
        }

        @Test
        void happy_emptyList_returnsEmpty() {
            when(bookRepository.findAll()).thenReturn(List.of());
            List<BookListDTO> res = catalogService.getAllBooks();
            assertThat(res).isEmpty();
        }
    }

    @Nested
    class GetBookById {
        @Test
        void happy_existingId_returnsBookDetailsDTO() {
            Book book = new Book(); book.setId(5L); book.setTitle("Title");
            when(bookRepository.findById(5L)).thenReturn(Optional.of(book));
            when(mapper.toBookDetailsDTO(book)).thenReturn(new BookDetailsDTO(5L,"Title",null,null,null,null,null,null));

            BookDetailsDTO dto = catalogService.getBookById(5L);
            assertThat(dto.id()).isEqualTo(5L);
            assertThat(dto.title()).isEqualTo("Title");
        }

        @Test
        void unhappy_nonExistingId_throwsNotFoundException() {
            when(bookRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> catalogService.getBookById(99L)).isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    class GetBookItems {
        @Test
        void happy_bookExists_returnsBookItemDTOs() {
            when(bookRepository.existsById(7L)).thenReturn(true);
            var bi = mock(com.personal.bookstoreapi.domain.entity.BookItem.class);
            when(bookItemRepository.findByBookId(7L)).thenReturn(List.of(bi));
            when(mapper.toBookItemDTO(bi)).thenReturn(new BookItemDTO(1L, "PHYSICAL", null, null, false, null, null));

            var res = catalogService.getBookItems(7L);
            assertThat(res).hasSize(1);
        }

        @Test
        void happy_bookExists_butNoItems_returnsEmptyList() {
            when(bookRepository.existsById(8L)).thenReturn(true);
            when(bookItemRepository.findByBookId(8L)).thenReturn(List.of());
            var res = catalogService.getBookItems(8L);
            assertThat(res).isEmpty();
        }

        @Test
        void unhappy_bookNotFound_throwsNotFoundException() {
            when(bookRepository.existsById(11L)).thenReturn(false);
            assertThatThrownBy(() -> catalogService.getBookItems(11L)).isInstanceOf(NotFoundException.class);
        }
    }
}
