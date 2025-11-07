package org.example.service;

import jakarta.validation.constraints.NotBlank;
import org.example.auth.dto.TokenResponse;
import org.example.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Validated
public class AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    // demo users; should be replaced with DB/LDAP later
    private final Map<String, String> users = new ConcurrentHashMap<>();

    public AuthService(JwtService jwtService, PasswordEncoder encoder) {
        this.jwtService = jwtService;
        this.encoder = encoder;
        users.put("alex", encoder.encode("password"));
    }

    public TokenResponse authUser(@NotBlank String name, @NotBlank String password) {
        String hash = users.get(name);
        if (hash != null && encoder.matches(password, hash)) {
            String token = jwtService.generate(name);
            return new TokenResponse(token);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
}
