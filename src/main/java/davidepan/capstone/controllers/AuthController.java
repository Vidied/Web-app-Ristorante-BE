package davidepan.capstone.controllers;

import davidepan.capstone.entities.Role;
import davidepan.capstone.entities.User;
import davidepan.capstone.payloads.UserLoginDTO;
import davidepan.capstone.payloads.UserLoginResponseDTO;
import davidepan.capstone.payloads.UserRegistrationDTO;
import davidepan.capstone.payloads.UserResponseDTO;
import davidepan.capstone.services.AuthService;
import davidepan.capstone.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public UserLoginResponseDTO login(@RequestBody @Valid UserLoginDTO body){
        String accessToken = authService.authenticateUserAndGenerateToken(body);
        return new UserLoginResponseDTO(accessToken);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@RequestBody @Valid UserRegistrationDTO body){
        User savedUser = userService.save(body);

        Set<String> roles = savedUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getSurname(),
                savedUser.getEmail(),
                roles
        );
    }
}
