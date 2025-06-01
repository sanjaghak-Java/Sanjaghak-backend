package com.example.Sanjaghak_Login.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bookstore.java.1403@gmail.com");
        message.setTo(to);
        message.setSubject("کد تأیید ثبت‌نام");
        message.setText("کد تأیید شما: " + code);

        mailSender.send(message);
    }



}
