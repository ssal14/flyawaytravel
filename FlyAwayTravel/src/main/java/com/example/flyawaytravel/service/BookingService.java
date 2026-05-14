package com.example.flyawaytravel.service;

import com.example.flyawaytravel.domain.Booking;
import com.example.flyawaytravel.domain.Flight;
import com.example.flyawaytravel.domain.User;
import com.example.flyawaytravel.dto.RequestBookingDTO;
import com.example.flyawaytravel.dto.ResponseBookingDTO;
import com.example.flyawaytravel.exception.BusinessException;
import com.example.flyawaytravel.exception.ConflictException;
import com.example.flyawaytravel.exception.ResourceNotFoundException;
import com.example.flyawaytravel.repository.BookingRepository;
import com.example.flyawaytravel.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserService userService;

    public BookingService(BookingRepository bookingRepository,
                          FlightRepository flightRepository,
                          UserService userService) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.userService = userService;
    }

    @Transactional
    public ResponseBookingDTO book(RequestBookingDTO request) {
        User user = userService.getCurrentUser();

        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado: " + request.getFlightId()));

        Date now = new Date();

        if (!flight.getEstDepartureTime().after(now)) {
            throw new BusinessException("No se puede reservar un vuelo pasado o en tránsito");
        }

        if (flight.getAvailableSeats() == null || flight.getAvailableSeats() <= 0) {
            throw new ConflictException("No hay asientos disponibles para este vuelo");
        }

        if (!bookingRepository.findOverlappingBookings(
                user.getId(), flight.getEstDepartureTime(), flight.getEstArrivalTime()).isEmpty()) {
            throw new ConflictException("Conflicto de horario con otra reserva del usuario");
        }

        flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        flightRepository.save(flight);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setBookingDate(now);
        Booking saved = bookingRepository.save(booking);

        writeConfirmationEmail(saved);

        return toDto(saved);
    }

    public ResponseBookingDTO getById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada: " + id));
        return toDto(booking);
    }

    private ResponseBookingDTO toDto(Booking b) {
        return new ResponseBookingDTO(
                b.getId(),
                b.getUser().getId(),
                b.getUser().getFirstName(),
                b.getUser().getLastName(),
                b.getFlight().getId(),
                b.getFlight().getFlightNumber(),
                b.getFlight().getEstDepartureTime(),
                b.getFlight().getEstArrivalTime(),
                b.getBookingDate()
        );
    }

    private void writeConfirmationEmail(Booking booking) {
        DateTimeFormatter iso = DateTimeFormatter.ISO_INSTANT;

        String content = String.format("""
                Booking Confirmation
                --------------------
                Booking ID:      %d
                Passenger:       %s %s
                Flight Number:   %s
                Airline:         %s
                Departure (UTC): %s
                Arrival   (UTC): %s
                Booked at (UTC): %s
                """,
                booking.getId(),
                booking.getUser().getFirstName(),
                booking.getUser().getLastName(),
                booking.getFlight().getFlightNumber(),
                booking.getFlight().getAirLineName(),
                iso.format(booking.getFlight().getEstDepartureTime().toInstant().atOffset(ZoneOffset.UTC)),
                iso.format(booking.getFlight().getEstArrivalTime().toInstant().atOffset(ZoneOffset.UTC)),
                iso.format(Instant.ofEpochMilli(booking.getBookingDate().getTime()).atOffset(ZoneOffset.UTC))
        );

        try {
            Path file = Paths.get("flight_booking_email_" + booking.getId() + ".txt");
            Files.writeString(file, content);
        } catch (IOException e) {
            System.err.println("No se pudo escribir el archivo de confirmación: " + e.getMessage());
        }
    }
}
