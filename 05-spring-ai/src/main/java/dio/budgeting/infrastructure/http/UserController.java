package dio.budgeting.infrastructure.http;

import dio.budgeting.infrastructure.http.request.UserRequest;
import dio.budgeting.infrastructure.http.response.UserResponse;
import dio.budgeting.infrastructure.persistence.entity.UserEntity;
import dio.budgeting.infrastructure.persistence.repository.JpaUserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private JpaUserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerUser(@RequestBody @Valid UserRequest request) {
        String encryptedPassword = passwordEncoder.encode(request.password());

        UserEntity newUser = new UserEntity(request.email(), encryptedPassword);
        UserEntity savedUser = repository.save(newUser);

        return UserResponse.from(savedUser);
    }
}
