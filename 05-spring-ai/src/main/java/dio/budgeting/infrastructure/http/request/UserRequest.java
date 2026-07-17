package dio.budgeting.infrastructure.http.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password
) {}

