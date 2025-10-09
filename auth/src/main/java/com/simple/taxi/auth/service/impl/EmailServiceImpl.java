package com.simple.taxi.auth.service.impl;

import com.simple.taxi.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;
    @Value("${dev.email.verification}")
    private boolean verification;

    @Override
    public void send(String to, String subject, String message) {
        if (verification) {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(to);
            simpleMailMessage.setFrom(from);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(message);
            log.info("Sending email to {} with subject '{}'", to, subject);

            mailSender.send(simpleMailMessage);
        }
    }
}