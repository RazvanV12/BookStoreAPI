package com.personal.bookstoreapi.service;

import com.personal.bookstoreapi.domain.entity.BookItem;
import com.personal.bookstoreapi.domain.entity.DigitalBookItem;
import com.personal.bookstoreapi.domain.entity.PhysicalBookItem;
import com.personal.bookstoreapi.domain.entity.Rental;
import com.personal.bookstoreapi.domain.entity.User;
import com.personal.bookstoreapi.domain.enums.RentalStatus;
import com.personal.bookstoreapi.dto.request.CreateRentalRequestDTO;
import com.personal.bookstoreapi.dto.response.RentalDetailsResponseDTO;
import com.personal.bookstoreapi.dto.response.RentalResponseDTO;
import com.personal.bookstoreapi.repository.BookItemRepository;
import com.personal.bookstoreapi.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BookItemRepository bookItemRepository;
    private final UserService userService;

    @Transactional
    public RentalResponseDTO createRental(CreateRentalRequestDTO request) {
        User user = userService.getCurrentUser();

        BookItem bookItem = bookItemRepository.findById(request.bookItemId())
                                              .orElseThrow(() -> new IllegalArgumentException(
                                                      "BookItem not found: " + request.bookItemId()));

        // Business validations
        if (!bookItem.isAvailableForRent()) {
            throw new IllegalStateException("This book item is not available for rent");
        }

        if (bookItem.getRentPrice() == null) {
            throw new IllegalStateException("Rent price is not set for this book item");
        }

        // Physical stock rule: 1 rental = 1 exemplar
        if (bookItem instanceof PhysicalBookItem physical) {
            if (physical.getStockQuantity() == null || physical.getStockQuantity() < 1) {
                throw new IllegalStateException("No stock available for renting this physical book item");
            }
            physical.setStockQuantity(physical.getStockQuantity() - 1);
        }

        Instant startAt = Instant.now();
        Instant endAt = startAt.plus(Duration.ofDays(request.days()));

        BigDecimal totalAmount = bookItem.getRentPrice()
                                         .multiply(BigDecimal.valueOf(request.days()));

        Rental rental = Rental.builder()
                              .user(user)
                              .bookItem(bookItem)
                              .startAt(startAt)
                              .endAt(endAt)
                              .status(RentalStatus.ACTIVE)
                              .totalAmount(totalAmount)
                              .build();

        Rental saved = rentalRepository.save(rental);

        return new RentalResponseDTO(
                saved.getId(),
                bookItem.getId(),
                bookItem.getBook()
                        .getTitle(),
                saved.getStartAt(),
                saved.getEndAt(),
                saved.getTotalAmount()
        );
    }

    @Transactional(readOnly = true)
    public List<RentalResponseDTO> getMyRentals() {
        User user = userService.getCurrentUser();

        return rentalRepository.findAllByUserId(user.getId())
                               .stream()
                               .map(r -> new RentalResponseDTO(
                                       r.getId(),
                                       r.getBookItem()
                                        .getId(),
                                       r.getBookItem()
                                        .getBook()
                                        .getTitle(),
                                       r.getStartAt(),
                                       r.getEndAt(),
                                       r.getTotalAmount()
                               ))
                               .toList();
    }

    @Transactional(readOnly = true)
    public RentalDetailsResponseDTO getRentalDetails(Long rentalId) {
        User user = userService.getCurrentUser();

        Rental rental = rentalRepository.findByIdWithBook(rentalId)
                                        .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (!rental.getUser()
                   .getId()
                   .equals(user.getId())) {
            throw new SecurityException("You are not allowed to view this rental");
        }

        BookItem bi = rental.getBookItem();

        String type = (bi instanceof DigitalBookItem) ? "DIGITAL" : "PHYSICAL";

        return new RentalDetailsResponseDTO(
                rental.getId(),
                bi.getId(),
                bi.getBook()
                  .getTitle(),
                type,
                rental.getStartAt(),
                rental.getEndAt(),
                bi.getRentPrice(),
                rental.getTotalAmount()
        );
    }
}
