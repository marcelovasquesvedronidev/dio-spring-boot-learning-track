package dio.budgeting.domain;

import lombok.Getter;

@Getter
public class User {
    private final String id;
    private final String email;
    private final String password;

    public User(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

}
