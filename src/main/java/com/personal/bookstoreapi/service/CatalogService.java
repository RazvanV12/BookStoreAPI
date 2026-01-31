package com.personal.bookstoreapi.service;

import com.personal.bookstoreapi.dto.response.BookDetailsDTO;
import com.personal.bookstoreapi.dto.response.BookItemDTO;
import com.personal.bookstoreapi.dto.response.BookListDTO;
import com.personal.bookstoreapi.exception.NotFoundException;
import com.personal.bookstoreapi.mapper.CatalogMapper;
import com.personal.bookstoreapi.repository.BookItemRepository;
import com.personal.bookstoreapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;
    private final CatalogMapper mapper;

    @Transactional(readOnly = true)
    public List<BookListDTO> getAllBooks() {
        return bookRepository.findAll()
                             .stream()
                             .map(mapper::toBookListDTO)
                             .toList();
    }

    @Transactional(readOnly = true)
    public BookDetailsDTO getBookById(Long id) {
        var book = bookRepository.findById(id)
                                 .orElseThrow(() -> new NotFoundException("Book not found"));
        return mapper.toBookDetailsDTO(book);
    }

    @Transactional(readOnly = true)
    public List<BookItemDTO> getBookItems(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundException("Book not found");
        }

        return bookItemRepository.findByBookId(bookId)
                                 .stream()
                                 .map(mapper::toBookItemDTO)
                                 .toList();
    }
}