package com.uniquindio.triage_academy.helpers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BcryptPasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public String hash(String contrasena) {
        return passwordEncoder.encode(contrasena);
    }

    public boolean matches(String constrasena, String hashContrasena) {
        return passwordEncoder.matches(constrasena, hashContrasena);
    }

}