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
public class ResponseFlightDTO {
    private Long id;
    private String airLineName;
    private String flightNumber;
    private Date estDepartureTime;
    private Date estArrivalTime;
    private Integer availableSeats;
}
