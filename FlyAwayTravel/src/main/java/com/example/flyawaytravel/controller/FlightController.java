package com.example.flyawaytravel.controller;

import com.example.flyawaytravel.dto.*;
import com.example.flyawaytravel.service.BookingService;
import com.example.flyawaytravel.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;
    private final BookingService bookingService;

    public FlightController(FlightService flightService, BookingService bookingService) {
        this.flightService = flightService;
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseFlightDTO> create(@Valid @RequestBody RequestFlightDTO request) {
        ResponseFlightDTO created = flightService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ResponseFlightDTO>> search(
            @RequestParam(required = false) String flightNumber,
            @RequestParam(required = false) String airLineName,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(flightService.search(flightNumber, airLineName, from, to));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseFlightDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getById(id));
    }

    @PostMapping("/book")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseBookingDTO> book(@Valid @RequestBody RequestBookingDTO request) {
        ResponseBookingDTO booking = bookingService.book(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping("/book/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseBookingDTO> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getById(id));
    }
}
