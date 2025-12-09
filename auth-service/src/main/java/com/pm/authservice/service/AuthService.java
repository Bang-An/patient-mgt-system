package com.pm.authservice.service;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public AuthService(UserService userService, PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  public String encodePassword(String password) {
    return passwordEncoder.encode(password);
  }

  public boolean matchesPassword(String newRawPassword, String currentEncodedPassword) {
    return passwordEncoder.matches(newRawPassword, currentEncodedPassword);
  }

  public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
    Optional<String> token = userService.findByEmail(loginRequestDTO.getEmail())
        .filter(u -> passwordEncoder.matches(loginRequestDTO.getPassword(),
            u.getPassword()))
        .map(u -> jwtUtil.generateToken(u.getId(), u.getEmail(), u.getRole()));

    return token;
  }

  // *** here we are only catching JwtException
  // if jwtUtil.validateToken throw a different exception, our application breaks
  public boolean validateToken(String token) {
    try {
      jwtUtil.validateToken(token);
      return true;
    } catch (JwtException e){
      return false;
    }
  }
  public Claims parseClaims(String token) {
    return jwtUtil.parseClaims(token);
  }
}
