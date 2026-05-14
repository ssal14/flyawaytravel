package com.example.flyawaytravel.repository;

import com.example.flyawaytravel.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            SELECT b FROM Booking b
            WHERE b.user.id = :userId
              AND b.flight.estDepartureTime < :arrival
              AND b.flight.estArrivalTime  > :departure
            """)
    List<Booking> findOverlappingBookings(@Param("userId") Long userId,
                                          @Param("departure") Date departure,
                                          @Param("arrival") Date arrival);

    boolean existsByUserIdAndFlightId(Long userId, Long flightId);
}
