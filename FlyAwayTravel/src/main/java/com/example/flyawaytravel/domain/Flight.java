package com.example.flyawaytravel.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String airLineName;

    @Column(nullable = false, unique = true, length = 6)
    private String flightNumber;

    @Column(nullable = false)
    private Date estDepartureTime;

    @Column(nullable = false)
    private Date estArrivalTime;

    @Column(nullable = false)
    private Integer availableSeats;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();
}
