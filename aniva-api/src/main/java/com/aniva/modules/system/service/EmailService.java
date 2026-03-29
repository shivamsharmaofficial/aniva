package com.aniva.modules.system.service;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async("emailTaskExecutor")
    public void sendOrderConfirmation(String email, String orderNumber) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Your Order is Confirmed");
        message.setText(
                "Thank you for your order.\n\nOrder Number: " + orderNumber
        );

        mailSender.send(message);
    }
}
