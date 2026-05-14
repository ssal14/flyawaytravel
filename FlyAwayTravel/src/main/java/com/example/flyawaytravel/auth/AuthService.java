package com.example.flyawaytravel.auth;

import com.example.flyawaytravel.domain.User;
import com.example.flyawaytravel.dto.RequestAuthDTO;
import com.example.flyawaytravel.dto.ResponseAuthDTO;
import com.example.flyawaytravel.exception.UnauthorizedException;
import com.example.flyawaytravel.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    public ResponseAuthDTO login(RequestAuthDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Email desconocido"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Contraseña incorrecta");
        }
        String token = jwtService.generateToken(user);
        return new ResponseAuthDTO(token);
    }
}
