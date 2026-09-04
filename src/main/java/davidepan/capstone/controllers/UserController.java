package davidepan.capstone.controllers;

import davidepan.capstone.entities.User;
import davidepan.capstone.payloads.UserRegistrationDTO;
import davidepan.capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public User getProfile(@AuthenticationPrincipal User currentUser){
        return currentUser;
    }

    @PutMapping("/me")
    public User updateProfile(@AuthenticationPrincipal User currentUser, @RequestBody UserRegistrationDTO body){
        return userService.update(currentUser.getId(), body);
    }
}
