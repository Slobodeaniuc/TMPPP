package com.chiril.library.service;

import com.chiril.library.domain.UserAccount;
import com.chiril.library.dto.LoginResponse;
import com.chiril.library.dto.MemberProfileDto;
import com.chiril.library.repo.UserAccountRepository;
import com.chiril.library.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserAccountRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userRepo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(String username, String password) {
        UserAccount account = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(password, account.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(
                account.getMemberId(), account.getUsername(), account.getDisplayName());
        return new LoginResponse(token, MemberProfileDto.from(account));
    }

    public MemberProfileDto getProfile(String memberId) {
        return userRepo.findByMemberId(memberId)
                .map(MemberProfileDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));
    }

    public List<MemberProfileDto> getAllMembers() {
        return userRepo.findAll().stream().map(MemberProfileDto::from).toList();
    }
}
