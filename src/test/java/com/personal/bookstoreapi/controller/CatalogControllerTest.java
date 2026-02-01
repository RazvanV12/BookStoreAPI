package com.personal.bookstoreapi.controller;

import com.personal.bookstoreapi.dto.response.BookDetailsDTO;
import com.personal.bookstoreapi.dto.response.BookItemDTO;
import com.personal.bookstoreapi.dto.response.BookListDTO;
import com.personal.bookstoreapi.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogControllerTest {

    @Mock
    CatalogService catalogService;

    @InjectMocks
    CatalogController controller;

    @Test
    void getAllBooks_delegates() {
        when(catalogService.getAllBooks()).thenReturn(List.of(new BookListDTO(1L,"T",null,null,null,null,null)));
        var res = controller.getAllBooks();
        assertThat(res).hasSize(1);
    }

    @Test
    void getBookById_delegates() {
        when(catalogService.getBookById(2L)).thenReturn(new BookDetailsDTO(2L,"T2",null,null,null,null,null,null));
        var res = controller.getBookById(2L);
        assertThat(res.id()).isEqualTo(2L);
    }

    @Test
    void getBookItems_delegates() {
        when(catalogService.getBookItems(3L)).thenReturn(List.of(new BookItemDTO(1L,"PHYSICAL",null,null,false,null,1)));
        var res = controller.getBookItems(3L);
        assertThat(res).hasSize(1);
    }
}
