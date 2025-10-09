package com.simple.taxi.auth.service;

public interface EmailService {
    void send(String to, String subject, String message);
}