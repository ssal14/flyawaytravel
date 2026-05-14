package com.example.flyawaytravel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestBookingDTO {

    @NotNull
    private Long flightId;
}
