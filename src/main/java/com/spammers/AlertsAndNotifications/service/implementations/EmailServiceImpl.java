package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * This class implements the service Email Service. Providing the
 * features to send an email.
 * @since 20-11-2024
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${CORREO}")
    private String email;

    /**
     * This method sends an email by the given receiver, the subject and the content.
     * @param to The receiver of the email.
     * @param subject The subject of the email.
     * @param body The content of the content.
     * @throws SpammersPrivateExceptions If there is email is not sent correctly.
     */
    @Override
    public void sendEmail(String to, String subject, String body) throws SpammersPrivateExceptions {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email); // Desde qué correo se enviará
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        try {
            javaMailSender.send(message);
            logger.info("Email sent correctly!");
        } catch (MailException e) {
            logger.error("There has been an error, the email was not correctly sent: "+e.getMessage());
            throw new SpammersPrivateExceptions(SpammersPrivateExceptions.EMAIL_EXCEPTION);
        }
    }

}