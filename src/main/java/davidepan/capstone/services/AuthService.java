package davidepan.capstone.services;

import davidepan.capstone.entities.User;
import davidepan.capstone.exceptions.UnauthorizedException;
import davidepan.capstone.payloads.UserLoginDTO;
import davidepan.capstone.repositories.UserRepository;
import davidepan.capstone.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTTools jwtTools;

    public String authenticateUserAndGenerateToken(UserLoginDTO body){
        User user = userRepository.findByEmail(body.email())
                .orElseThrow(()-> new UnauthorizedException("Credenziali non valide"));

        if(passwordEncoder.matches(body.password(), user.getPassword())){
            return jwtTools.createToken(user);
        } else {
            throw new UnauthorizedException("Credenziali non valide");
        }
    }
}
