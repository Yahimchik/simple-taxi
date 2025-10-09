package com.simple.taxi.auth.service;

import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.entity.VerificationToken;

public interface VerificationTokenService {

    VerificationToken createVerificationToken(User user);

    boolean verifyToken(String token);

    void resendVerificationToken(String email);

    VerificationToken findVerificationTokenByTokenValue(String token);
}