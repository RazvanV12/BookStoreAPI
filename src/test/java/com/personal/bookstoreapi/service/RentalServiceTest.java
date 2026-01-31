package com.personal.bookstoreapi.service;

import com.personal.bookstoreapi.domain.entity.Book;
import com.personal.bookstoreapi.domain.entity.BookItem;
import com.personal.bookstoreapi.domain.entity.PhysicalBookItem;
import com.personal.bookstoreapi.domain.entity.Rental;
import com.personal.bookstoreapi.domain.entity.User;
import com.personal.bookstoreapi.dto.request.CreateRentalRequestDTO;
import com.personal.bookstoreapi.dto.response.RentalDetailsResponseDTO;
import com.personal.bookstoreapi.dto.response.RentalResponseDTO;
import com.personal.bookstoreapi.repository.BookItemRepository;
import com.personal.bookstoreapi.repository.RentalRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    RentalRepository rentalRepository;

    @Mock
    BookItemRepository bookItemRepository;

    @Mock
    UserService userService;

    @InjectMocks
    RentalService rentalService;

    private static User user(long id) { return User.builder().id(id).email("u@x").fullName("U").passwordHash("p").build(); }

    private static PhysicalBookItem physical(long id, Integer stock, BigDecimal rent) {
        PhysicalBookItem p = PhysicalBookItem.builder().id(id).stockQuantity(stock).availableForRent(true).rentPrice(rent).build();
        Book b = new Book(); b.setId(1L); b.setTitle("Book"); p.setBook(b); return p;
    }

    @Nested
    class CreateRental {
        @Test
        void happy_createsRentalAndReturnsResponse() {
            when(userService.getCurrentUser()).thenReturn(user(3L));
            var req = new CreateRentalRequestDTO(2L,3);
            PhysicalBookItem p = physical(2L,5,new BigDecimal("2.50"));
            when(bookItemRepository.findById(2L)).thenReturn(Optional.of((BookItem)p));

            Rental saved = Rental.builder().id(7L).bookItem(p).user(user(3L)).startAt(Instant.now()).endAt(Instant.now().plusSeconds(3600)).totalAmount(new BigDecimal("7.50")).build();
            when(rentalRepository.save(any())).thenReturn(saved);

            RentalResponseDTO res = rentalService.createRental(req);
            assertThat(res.rentalId()).isEqualTo(7L);
            assertThat(res.totalAmount()).isEqualByComparingTo(new BigDecimal("7.50"));
            verify(bookItemRepository).findById(2L);
        }

        @Test
        void unhappy_rentPriceNull_throwsIllegalStateException() {
            when(userService.getCurrentUser()).thenReturn(user(1L));
            PhysicalBookItem p = physical(2L,5,null);
            when(bookItemRepository.findById(2L)).thenReturn(Optional.of((BookItem)p));
            var req = new CreateRentalRequestDTO(2L,1);
            assertThatThrownBy(() -> rentalService.createRental(req)).isInstanceOf(IllegalStateException.class).hasMessageContaining("Rent price is not set");
        }

        @Test
        void unhappy_physicalStockNull_throwsIllegalStateException() {
            when(userService.getCurrentUser()).thenReturn(user(1L));
            PhysicalBookItem p = PhysicalBookItem.builder().id(3L).availableForRent(true).rentPrice(new BigDecimal("1.00")).stockQuantity(null).build();
            p.setBook(new Book());
            when(bookItemRepository.findById(3L)).thenReturn(Optional.of((BookItem)p));
            var req = new CreateRentalRequestDTO(3L,1);
            assertThatThrownBy(() -> rentalService.createRental(req)).isInstanceOf(IllegalStateException.class).hasMessageContaining("No stock available");
        }

        @Test
        void happy_physical_stockDecremented_onRental() {
            when(userService.getCurrentUser()).thenReturn(user(2L));
            PhysicalBookItem p = physical(4L,2,new BigDecimal("2.00"));
            when(bookItemRepository.findById(4L)).thenReturn(Optional.of((BookItem)p));
            when(rentalRepository.save(any())).thenAnswer(i -> { Rental r = i.getArgument(0); r.setId(99L); return r; });

            var req = new CreateRentalRequestDTO(4L,1);
            RentalResponseDTO res = rentalService.createRental(req);
            assertThat(res.rentalId()).isEqualTo(99L);
            assertThat(p.getStockQuantity()).isEqualTo(1);
        }

        @Test
        void unhappy_bookItemNotFound_throwsIllegalArgumentException() {
            when(userService.getCurrentUser()).thenReturn(user(1L));
            var req = new CreateRentalRequestDTO(9L,1);
            when(bookItemRepository.findById(9L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> rentalService.createRental(req)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("BookItem not found");
        }

        @Test
        void unhappy_notAvailableForRent_throwsIllegalStateException() {
            when(userService.getCurrentUser()).thenReturn(user(1L));
            PhysicalBookItem p = physical(2L,5,new BigDecimal("1.00")); p.setAvailableForRent(false);
            when(bookItemRepository.findById(2L)).thenReturn(Optional.of((BookItem)p));
            var req = new CreateRentalRequestDTO(2L,1);
            assertThatThrownBy(() -> rentalService.createRental(req)).isInstanceOf(IllegalStateException.class).hasMessageContaining("not available for rent");
        }

        @Test
        void unhappy_physicalNoStock_throwsIllegalStateException() {
            when(userService.getCurrentUser()).thenReturn(user(1L));
            PhysicalBookItem p = physical(2L,0,new BigDecimal("1.00"));
            when(bookItemRepository.findById(2L)).thenReturn(Optional.of((BookItem)p));
            var req = new CreateRentalRequestDTO(2L,1);
            assertThatThrownBy(() -> rentalService.createRental(req)).isInstanceOf(IllegalStateException.class).hasMessageContaining("No stock available");
        }
    }

    @Nested
    class GetMyRentals {
        @Test
        void happy_returnsRentalResponseList() {
            User u = user(4L);
            when(userService.getCurrentUser()).thenReturn(u);
            Rental r = new Rental(); r.setId(2L); r.setUser(u);
            BookItem bi = new PhysicalBookItem(); bi.setId(77L); bi.setBook(new Book()); r.setBookItem(bi);
            when(rentalRepository.findAllByUserId(4L)).thenReturn(List.of(r));

            var res = rentalService.getMyRentals();
            assertThat(res).hasSize(1);
            assertThat(res.get(0).rentalId()).isEqualTo(2L);
        }
    }

    @Nested
    class GetRentalDetails {
        @Test
        void happy_returnsRentalDetailsForOwner() {
            User u = user(5L);
            when(userService.getCurrentUser()).thenReturn(u);
            PhysicalBookItem p = physical(3L,2,new BigDecimal("1.00"));
            Rental r = Rental.builder().id(12L).user(u).bookItem(p).startAt(Instant.now()).endAt(Instant.now().plusSeconds(1000)).totalAmount(new BigDecimal("3.00")).build();
            when(rentalRepository.findByIdWithBook(12L)).thenReturn(Optional.of(r));

            RentalDetailsResponseDTO dto = rentalService.getRentalDetails(12L);
            assertThat(dto.rentalId()).isEqualTo(12L);
            assertThat(dto.type()).isEqualTo("PHYSICAL");
        }

        @Test
        void unhappy_rentalNotFound_throwsIllegalArgumentException() {
            when(userService.getCurrentUser()).thenReturn(user(1L));
            when(rentalRepository.findByIdWithBook(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> rentalService.getRentalDetails(99L)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Rental not found");
        }

        @Test
        void unhappy_notOwner_throwsSecurityException() {
            User owner = user(10L);
            User other = user(11L);
            when(userService.getCurrentUser()).thenReturn(other);
            Rental r = new Rental(); r.setId(20L); r.setUser(owner);
            when(rentalRepository.findByIdWithBook(20L)).thenReturn(Optional.of(r));

            assertThatThrownBy(() -> rentalService.getRentalDetails(20L)).isInstanceOf(SecurityException.class).hasMessageContaining("not allowed");
        }
    }
}
