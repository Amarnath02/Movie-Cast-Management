package com.moviefix.auth.service;

import com.moviefix.auth.entity.User;
import com.moviefix.auth.entity.UserRole;
import com.moviefix.auth.repository.UserRepository;
import com.moviefix.auth.utils.AuthResponse;
import com.moviefix.auth.utils.LoginRequest;
import com.moviefix.auth.utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password((passwordEncoder.encode(request.getPassword())))
                .role(UserRole.USER)
                .build();

        System.out.println("User details : " + user.toString());

        User savedUser = userRepository.save(user);
        var accessToken = jwtService.generateTokenFromUsername(savedUser);
        System.out.println("accessToken : " + accessToken);

        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());
        System.out.println("refreshToken : " + refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                ));

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        var accessToken = jwtService.generateTokenFromUsername(user);
        var refreshToken = refreshTokenService.createRefreshToken(request.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
