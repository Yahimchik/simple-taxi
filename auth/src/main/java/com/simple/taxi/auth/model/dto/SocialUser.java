package com.simple.taxi.auth.model.dto;

import com.simple.taxi.auth.model.entity.User;
import lombok.Builder;
import lombok.Data;

@Builder
public record SocialUser(
        User user,
        String userId,
        String accessToken
) {
}