package com.personal.bookstoreapi.controller;

import com.personal.bookstoreapi.dto.response.BookDetailsDTO;
import com.personal.bookstoreapi.dto.response.BookItemDTO;
import com.personal.bookstoreapi.dto.response.BookListDTO;
import com.personal.bookstoreapi.service.CatalogService;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Validated
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public List<BookListDTO> getAllBooks() {
        return catalogService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookDetailsDTO getBookById(@PathVariable @Positive(message = "id must be positive") Long id) {
        return catalogService.getBookById(id);
    }

    @GetMapping("/{id}/items")
    public List<BookItemDTO> getBookItems(@PathVariable @Positive(message = "id must be positive") Long id) {
        return catalogService.getBookItems(id);
    }
}