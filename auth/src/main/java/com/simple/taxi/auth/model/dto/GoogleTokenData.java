package com.simple.taxi.auth.model.dto;

import lombok.Data;

@Data
public class GoogleTokenData implements SocialTokenData {
    private String access_token;
    private String scope;

    @Override
    public String getToken() {
        return access_token;
    }
}