package com.example.flyawaytravel.service;

import com.example.flyawaytravel.domain.Flight;
import com.example.flyawaytravel.dto.RequestFlightDTO;
import com.example.flyawaytravel.dto.ResponseFlightDTO;
import com.example.flyawaytravel.exception.BusinessException;
import com.example.flyawaytravel.exception.ConflictException;
import com.example.flyawaytravel.exception.ResourceNotFoundException;
import com.example.flyawaytravel.repository.FlightRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final ModelMapper modelMapper;

    public FlightService(FlightRepository flightRepository, ModelMapper modelMapper) {
        this.flightRepository = flightRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseFlightDTO create(RequestFlightDTO request) {
        if (!request.getEstDepartureTime().before(request.getEstArrivalTime())) {
            throw new BusinessException("estDepartureTime debe ser anterior a estArrivalTime");
        }
        if (flightRepository.existsByFlightNumber(request.getFlightNumber())) {
            throw new ConflictException("flightNumber ya existe: " + request.getFlightNumber());
        }

        Flight flight = modelMapper.map(request, Flight.class);
        flight.setId(null);
        Flight saved = flightRepository.save(flight);
        return modelMapper.map(saved, ResponseFlightDTO.class);
    }

    public Flight getEntityById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado: " + id));
    }

    public ResponseFlightDTO getById(Long id) {
        return modelMapper.map(getEntityById(id), ResponseFlightDTO.class);
    }

    public List<ResponseFlightDTO> search(String flightNumber,
                                          String airLineName,
                                          String fromIso8601,
                                          String toIso8601) {
        Date from = parseIso(fromIso8601);
        Date to   = parseIso(toIso8601);

        return flightRepository.search(emptyToNull(flightNumber), emptyToNull(airLineName), from, to)
                .stream()
                .map(f -> modelMapper.map(f, ResponseFlightDTO.class))
                .toList();
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private Date parseIso(String iso) {
        if (iso == null || iso.isBlank()) return null;
        try {
            return Date.from(Instant.parse(iso));
        } catch (Exception e) {
            throw new BusinessException("Fecha inválida (se espera ISO 8601): " + iso);
        }
    }
}
