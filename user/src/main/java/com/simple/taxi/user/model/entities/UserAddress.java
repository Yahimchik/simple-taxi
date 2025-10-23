package com.simple.taxi.user.model.entities;

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
@Table("user_addresses")
public class UserAddress {
    @Id
    private UUID id;

    @Column("user_id")
    private UUID userId;

    private String title;

    @Column("address_id")
    private UUID addressId;

    private Instant createdAt;
    private Instant updatedAt;
}