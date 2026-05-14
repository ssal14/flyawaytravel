package com.example.flyawaytravel.controller;

import com.example.flyawaytravel.dto.RequestUserDTO;
import com.example.flyawaytravel.dto.ResponseUserDTO;
import com.example.flyawaytravel.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseUserDTO> register(@Valid @RequestBody RequestUserDTO request) {
        ResponseUserDTO created = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseUserDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getResponseById(id));
    }
}
