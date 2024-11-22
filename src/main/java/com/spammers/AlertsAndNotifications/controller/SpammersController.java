package com.spammers.AlertsAndNotifications.controller;


import com.spammers.AlertsAndNotifications.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SpammersController {

    private final EmailService emailService;

    @GetMapping("/sendEmail")
    @ResponseStatus(HttpStatus.OK)
    public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        emailService.sendEmailCustomised(to, subject, body);
        return "Email Sent!";
    }
}