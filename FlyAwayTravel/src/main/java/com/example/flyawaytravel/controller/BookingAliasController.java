package com.example.flyawaytravel.controller;

import com.example.flyawaytravel.dto.ResponseBookingDTO;
import com.example.flyawaytravel.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flight")
public class BookingAliasController {

    private final BookingService bookingService;

    public BookingAliasController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/book/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseBookingDTO> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getById(id));
    }
}
