package com.finanzas.auth;

import com.finanzas.auth.dto.AuthResponse;
import com.finanzas.auth.dto.LoginRequest;
import com.finanzas.auth.dto.RegisterRequest;
import com.finanzas.auth.dto.UserResponse;
import com.finanzas.auth.exception.EmailAlreadyExistsException;
import com.finanzas.auth.exception.InvalidCredentialsException;
import com.finanzas.user.User;
import com.finanzas.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request) {
        String emailNormalizado = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(emailNormalizado)) {
            throw new EmailAlreadyExistsException("El correo ya se encuentra registrado");
        }

        User user = new User();
        user.setNombre(request.nombre().trim());
        user.setEmail(emailNormalizado);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        String emailNormalizado = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new InvalidCredentialsException("Correo o contrasena incorrectos"));

        boolean passwordCorrecto = passwordEncoder.matches(request.password(), user.getPasswordHash());
        if (!passwordCorrecto) {
            throw new InvalidCredentialsException("Correo o contrasena incorrectos");
        }

        return new AuthResponse("Inicio de sesion exitoso", toUserResponse(user));
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNombre(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
