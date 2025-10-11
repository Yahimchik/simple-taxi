package com.simple.taxi.user.model.entities;

import com.simple.taxi.user.model.enums.Language;
import com.simple.taxi.user.model.enums.PaymentMethod;
import com.simple.taxi.user.model.enums.Theme;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("user_settings")
public class UserSettings {
    @Id
    private UUID id;
    private Language language;
    private Boolean pushEnabled;
    private Boolean emailNotifications;
    private Theme theme;
    private PaymentMethod paymentMethod;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}