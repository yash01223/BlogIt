package com.blogit.BlogIt_App.Controller;

import com.blogit.BlogIt_App.Config.JwtTokenProvider;
import com.blogit.BlogIt_App.dto.LoginRequest;
import com.blogit.BlogIt_App.dto.JwtResponse;
import com.blogit.BlogIt_App.dto.UserDTO;
import com.blogit.BlogIt_App.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    /**
     * Login endpoint - Authenticates user and returns JWT token
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtResponse(token, "Bearer", loginRequest.getUsername()));
    }

    /**
     * Register endpoint - Creates a new user with USER role
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        String token = tokenProvider.generateTokenFromUsername(createdUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new JwtResponse(token, "Bearer", createdUser.getUsername()));
    }
}
