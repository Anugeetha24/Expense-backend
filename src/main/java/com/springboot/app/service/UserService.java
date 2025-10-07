package com.springboot.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.app.model.User;
import com.springboot.app.repository.UserRepository;
import com.springboot.app.dto.LoginDTO;
import com.springboot.app.dto.RegisterDTO;
import com.springboot.app.dto.JwtResponseDTO;
import com.springboot.app.security.JwtTokenUtil;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String register(RegisterDTO registerDTO) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            return "Username already exists";
        }

        // Check if email already exists
        if (userRepository.existsByEmailId(registerDTO.getEmailId())) {
            return "Email already exists";
        }

        // Validate role
        String role = registerDTO.getRole().toLowerCase();
        if (!role.equals("user") && !role.equals("admin")) {
            return "Invalid role. Role must be either 'user' or 'admin'";
        }

        // Create new user
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword()); // In a real application, you should hash the password
        user.setEmailId(registerDTO.getEmailId());
        user.setRole(role);

        userRepository.save(user);
        return "User registered successfully";
    }

    public JwtResponseDTO login(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername());
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // In a real application, you should compare hashed passwords
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Generate JWT token
        String token = jwtTokenUtil.generateToken(user.getUsername());
        
        return new JwtResponseDTO(token, user.getUsername(), user.getRole());
    }
}