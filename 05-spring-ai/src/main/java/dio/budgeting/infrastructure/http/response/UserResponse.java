package dio.budgeting.infrastructure.http.response;

import dio.budgeting.infrastructure.persistence.entity.UserEntity;

public record UserResponse(String id, String email) {
    public static UserResponse from(UserEntity entity) {
        return new UserResponse(entity.getId(), entity.getEmail());
    }
}
