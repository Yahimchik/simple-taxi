package com.simple.taxi.user.model.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("user_profiles")
public class UserProfile {

    @Id
    private UUID id;

    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String email;
    private String phone;
    private Instant createdAt;
    private Instant updatedAt;
}
