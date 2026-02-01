package com.personal.bookstoreapi.controller;

import com.personal.bookstoreapi.dto.request.CreateRentalRequestDTO;
import com.personal.bookstoreapi.dto.response.RentalDetailsResponseDTO;
import com.personal.bookstoreapi.dto.response.RentalResponseDTO;
import com.personal.bookstoreapi.service.RentalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentalControllerTest {

    @Mock
    RentalService rentalService;

    @InjectMocks
    RentalController controller;

    @Test
    void createRental_delegates() {
        var req = new CreateRentalRequestDTO(1L,2);
        when(rentalService.createRental(req)).thenReturn(new RentalResponseDTO(1L,1L,"T",null,null,new BigDecimal("2.00")));
        var out = controller.createRental(req);
        assertThat(out.rentalId()).isEqualTo(1L);
    }

    @Test
    void getMyRentals_delegates() {
        when(rentalService.getMyRentals()).thenReturn(List.of(new RentalResponseDTO(2L,2L,"T2",null,null,new BigDecimal("1.00"))));
        var out = controller.getMyRentals();
        assertThat(out).hasSize(1);
    }

    @Test
    void getRentalDetails_delegates() {
        when(rentalService.getRentalDetails(3L)).thenReturn(new RentalDetailsResponseDTO(3L,3L,"T3","PHYSICAL",null,null,new BigDecimal("1.00"),new BigDecimal("3.00")));
        var out = controller.getRentalDetails(3L);
        assertThat(out.rentalId()).isEqualTo(3L);
    }
}
