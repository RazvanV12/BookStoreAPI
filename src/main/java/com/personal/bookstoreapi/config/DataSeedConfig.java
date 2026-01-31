package com.personal.bookstoreapi.config;

import com.personal.bookstoreapi.domain.entity.Author;
import com.personal.bookstoreapi.domain.entity.Book;
import com.personal.bookstoreapi.domain.entity.DigitalBookItem;
import com.personal.bookstoreapi.domain.entity.PhysicalBookItem;
import com.personal.bookstoreapi.repository.AuthorRepository;
import com.personal.bookstoreapi.repository.BookItemRepository;
import com.personal.bookstoreapi.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Configuration
public class DataSeedConfig {

    @Bean
    CommandLineRunner seedData(AuthorRepository authorRepo,
                               BookRepository bookRepo,
                               BookItemRepository itemRepo) {
        return args -> seed(authorRepo, bookRepo, itemRepo);
    }

    @Transactional
    void seed(AuthorRepository authorRepo,
              BookRepository bookRepo,
              BookItemRepository itemRepo) {

        if (bookRepo.count() > 0) {
            return;
        }

        // ===== AUTHORS =====
        Author orwell = authorRepo.findByNameIgnoreCase("George Orwell")
                                  .orElseGet(() -> authorRepo.save(
                                          Author.builder()
                                                .name("George Orwell")
                                                .build()
                                  ));

        Author rowling = authorRepo.findByNameIgnoreCase("J.K. Rowling")
                                   .orElseGet(() -> authorRepo.save(
                                           Author.builder()
                                                 .name("J.K. Rowling")
                                                 .build()
                                   ));

        Author dosto = authorRepo.findByNameIgnoreCase("Fyodor Dostoevsky")
                                 .orElseGet(() -> authorRepo.save(
                                         Author.builder()
                                               .name("Fyodor Dostoevsky")
                                               .build()
                                 ));

        // ===== BOOKS =====
        Book book1984 = Book.builder()
                            .title("1984")
                            .isbn("9780451524935")
                            .language("EN")
                            .publicationYear(1949)
                            .description("A dystopian novel about surveillance and totalitarianism.")
                            .authors(Set.of(orwell))
                            .build();

        Book bookHp = Book.builder()
                          .title("Harry Potter and the Philosopher's Stone")
                          .isbn("9780747532699")
                          .language("EN")
                          .publicationYear(1997)
                          .description("A young wizard discovers his destiny.")
                          .authors(Set.of(rowling))
                          .build();

        Book bookCrime = Book.builder()
                             .title("Crime and Punishment")
                             .isbn("9780140449136")
                             .language("EN")
                             .publicationYear(1866)
                             .description("A psychological novel exploring guilt and redemption.")
                             .authors(Set.of(dosto))
                             .build();

        bookRepo.saveAll(Set.of(book1984, bookHp, bookCrime));

        // ===== BOOK ITEMS =====
        PhysicalBookItem item1984Physical = PhysicalBookItem.builder()
                                                            .book(book1984)
                                                            .price(new BigDecimal("59.90"))
                                                            .availableForRent(true)
                                                            .rentPrice(new BigDecimal("9.90"))
                                                            .stockQuantity(15)
                                                            .weightGrams(320)
                                                            .build();

        DigitalBookItem item1984Digital = DigitalBookItem.builder()
                                                         .book(book1984)
                                                         .price(new BigDecimal("29.90"))
                                                         .availableForRent(true)
                                                         .rentPrice(new BigDecimal("5.90"))
                                                         .fileFormat("EPUB")
                                                         .fileUrl("https://example.com/files/1984.epub")
                                                         .build();

        PhysicalBookItem hpPhysical = PhysicalBookItem.builder()
                                                      .book(bookHp)
                                                      .price(new BigDecimal("79.90"))
                                                      .availableForRent(false)
                                                      .rentPrice(null)
                                                      .stockQuantity(30)
                                                      .weightGrams(410)
                                                      .build();

        DigitalBookItem crimeDigital = DigitalBookItem.builder()
                                                      .book(bookCrime)
                                                      .price(new BigDecimal("24.90"))
                                                      .availableForRent(true)
                                                      .rentPrice(new BigDecimal("4.90"))
                                                      .fileFormat("PDF")
                                                      .fileUrl("https://example.com/files/crime-and-punishment.pdf")
                                                      .build();

        itemRepo.saveAll(Set.of(
                item1984Physical,
                item1984Digital,
                hpPhysical,
                crimeDigital
        ));
    }
}