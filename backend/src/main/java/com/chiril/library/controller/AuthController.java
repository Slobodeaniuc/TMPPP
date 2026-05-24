package com.chiril.library.controller;

import com.chiril.library.dto.LoginRequest;
import com.chiril.library.dto.LoginResponse;
import com.chiril.library.dto.MemberProfileDto;
import com.chiril.library.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return authService.login(req.username(), req.password());
    }

    @GetMapping("/me")
    public MemberProfileDto me(Authentication auth) {
        return authService.getProfile((String) auth.getPrincipal());
    }

    @GetMapping("/members")
    public List<MemberProfileDto> listMembers() {
        return authService.getAllMembers();
    }
}
