package com.example.flyawaytravel.repository;

import com.example.flyawaytravel.domain.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByFlightNumber(String flightNumber);

    boolean existsByFlightNumber(String flightNumber);

    @Query("""
            SELECT f FROM Flight f
            WHERE (:flightNumber IS NULL OR LOWER(f.flightNumber) LIKE LOWER(CONCAT('%', :flightNumber, '%')))
              AND (:airLineName  IS NULL OR LOWER(f.airLineName)  LIKE LOWER(CONCAT('%', :airLineName,  '%')))
              AND (:from IS NULL OR f.estDepartureTime >= :from)
              AND (:to   IS NULL OR f.estDepartureTime <= :to)
            """)
    List<Flight> search(@Param("flightNumber") String flightNumber,
                        @Param("airLineName") String airLineName,
                        @Param("from") Date from,
                        @Param("to") Date to);
}
