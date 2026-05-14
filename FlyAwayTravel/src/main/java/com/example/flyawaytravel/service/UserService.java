package com.example.flyawaytravel.service;

import com.example.flyawaytravel.domain.User;
import com.example.flyawaytravel.dto.RequestUserDTO;
import com.example.flyawaytravel.dto.ResponseUserDTO;
import com.example.flyawaytravel.exception.ConflictException;
import com.example.flyawaytravel.exception.ResourceNotFoundException;
import com.example.flyawaytravel.exception.UnauthorizedException;
import com.example.flyawaytravel.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public ResponseUserDTO register(RequestUserDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("El email ya está registrado");
        }

        User user = modelMapper.map(request, User.class);
        user.setId(null);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            user.setUsername(request.getEmail());
        }

        User saved = userRepository.save(user);
        return new ResponseUserDTO(saved.getId());
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    public ResponseUserDTO getResponseById(Long id) {
        return new ResponseUserDTO(getById(id).getId());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("No autenticado");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return userRepository.findById(user.getId())
                    .orElseThrow(() -> new UnauthorizedException("Usuario inválido"));
        }
        throw new UnauthorizedException("Principal inválido");
    }
}
