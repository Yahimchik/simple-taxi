package com.simple.taxi.user.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserCreatedEvent {
    private Payload payload;

    @Data
    public static class Payload {
        private User after;
        private User before;
        private String op;
    }

    @Data
    public static class User {
        private UUID id;
        private String email;
        private String phone;
    }
}
