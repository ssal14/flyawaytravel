package com.example.flyawaytravel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestFlightDTO {

    @NotBlank
    private String airLineName;

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{1,6}$",
             message = "flightNumber debe contener solo A-Z y 0-9, con máximo 6 caracteres")
    private String flightNumber;

    @NotNull
    private Date estDepartureTime;

    @NotNull
    private Date estArrivalTime;

    @NotNull
    @Min(value = 1, message = "availableSeats debe ser mayor a 0")
    private Integer availableSeats;
}
