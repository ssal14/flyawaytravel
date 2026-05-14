package com.example.flyawaytravel.service;

import com.example.flyawaytravel.repository.BookingRepository;
import com.example.flyawaytravel.repository.FlightRepository;
import com.example.flyawaytravel.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CleanupService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    public CleanupService(BookingRepository bookingRepository,
                          FlightRepository flightRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void cleanAll() {
        bookingRepository.deleteAllInBatch();
        flightRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
}
