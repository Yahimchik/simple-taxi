package com.simple.taxi.auth.model.entity;

import com.simple.taxi.auth.model.enums.LoginType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "auth_users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "complete", nullable = false)
    private boolean complete;
}