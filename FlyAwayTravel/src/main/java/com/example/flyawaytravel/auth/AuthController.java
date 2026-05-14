package com.example.flyawaytravel.auth;

import com.example.flyawaytravel.dto.RequestAuthDTO;
import com.example.flyawaytravel.dto.ResponseAuthDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {this.authService = authService;}

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseAuthDTO> login(@Valid @RequestBody RequestAuthDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
