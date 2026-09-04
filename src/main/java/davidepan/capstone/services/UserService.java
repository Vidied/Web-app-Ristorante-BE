package davidepan.capstone.services;

import davidepan.capstone.entities.Role;
import davidepan.capstone.entities.User;
import davidepan.capstone.exceptions.BadRequestException;
import davidepan.capstone.exceptions.NotFoundException;
import davidepan.capstone.payloads.UserRegistrationDTO;
import davidepan.capstone.repositories.RoleRepository;
import davidepan.capstone.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Utente con ID " + id + " non trovato"));
    }

    public User save(UserRegistrationDTO body){
        if(userRepository.existsByEmail(body.email())){
            throw new BadRequestException("L'email " + body.email() + " è già stato utilizzato");
        }

        User user = new User();
        user.setEmail(body.email());
        user.setName(body.name());
        user.setSurname(body.surname());
        user.setPassword(passwordEncoder.encode(body.password()));

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(()-> new NotFoundException("Ruolo ROLE_USER non trovato"));
        user.getRoles().add(defaultRole);

        return userRepository.save(user);
    }

    public User update(Long id, UserRegistrationDTO body){
        User found = this.findById(id);

        if(!found.getEmail().equals(body.email()) && userRepository.existsByEmail(body.email())){
            throw new BadRequestException("L'email " + body.email() + " è già in uso");
        }

        found.setName(body.name());
        found.setSurname(body.surname());
        found.setEmail(body.email());

        if(body.password() != null && !body.password().isBlank()){
            found.setPassword(passwordEncoder.encode(body.password()));
        }

        return userRepository.save(found);
    }

    public void delete(Long id){
        User found = this.findById(id);

        userRepository.delete(found);
    }




}
