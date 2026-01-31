package com.personal.bookstoreapi.controller;

import com.personal.bookstoreapi.dto.request.CreateRentalRequestDTO;
import com.personal.bookstoreapi.dto.response.RentalDetailsResponseDTO;
import com.personal.bookstoreapi.dto.response.RentalResponseDTO;
import com.personal.bookstoreapi.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponseDTO createRental(@Valid @RequestBody CreateRentalRequestDTO request) {
        return rentalService.createRental(request);
    }

    @GetMapping("/me")
    public List<RentalResponseDTO> getMyRentals() {
        return rentalService.getMyRentals();
    }

    @GetMapping("/{rentalId}")
    public RentalDetailsResponseDTO getRentalDetails(@PathVariable Long rentalId) {
        return rentalService.getRentalDetails(rentalId);
    }
}