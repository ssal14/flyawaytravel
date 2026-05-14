package com.example.flyawaytravel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBookingDTO {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private Long flightId;
    private String flightNumber;
    private Date estDepartureTime;
    private Date estArrivalTime;
    private Date bookingDate;
}
