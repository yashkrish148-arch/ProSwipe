package com.proswipe.controller;

import com.proswipe.config.JwtUtil;
import com.proswipe.model.User;
import com.proswipe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // REQ-F-01: Register with name, email, password, role
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");
        String role = body.get("role");

        if (name == null || email == null || password == null || role == null || name.isBlank() || email.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Name, email, password and role are required"));

        // REQ-F-02: minimum 8-character password
        if (password.length() < 8)
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 8 characters"));

        // REQ-F-03: reject duplicate email
        if (userRepository.existsByEmail(email))
            return ResponseEntity.badRequest().body(Map.of("error", "Email is already registered"));

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        // REQ-NF-07: BCrypt with 10 rounds
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setSkills(body.getOrDefault("skills", ""));
        user.setCompany(body.getOrDefault("company", ""));

        User saved = userRepository.save(user);
        // REQ-F-04: return JWT on success
        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), saved.getRole());
        return ResponseEntity.ok(buildResponse(saved, token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "No account found with this email"));

        User user = opt.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash()))
            return ResponseEntity.badRequest().body(Map.of("error", "Incorrect password"));

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return ResponseEntity.ok(buildResponse(user, token));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(buildResponse(u, null)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return userRepository.findById(id).map(user -> {
            if (body.containsKey("name") && !body.get("name").isBlank()) user.setName(body.get("name"));
            if (body.containsKey("skills")) user.setSkills(body.get("skills"));
            if (body.containsKey("company")) user.setCompany(body.get("company"));
            userRepository.save(user);
            return ResponseEntity.ok(buildResponse(user, null));
        }).orElse(ResponseEntity.notFound().build());
    }

    private Map<String, Object> buildResponse(User user, String token) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("role", user.getRole());
        map.put("skills", user.getSkills());
        map.put("company", user.getCompany());
        map.put("createdAt", user.getCreatedAt());
        if (token != null) map.put("token", token);
        return map;
    }
}
