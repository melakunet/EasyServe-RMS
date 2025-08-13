
package com.easyserve.service;

import com.easyserve.model.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${app.notifications.email.from:no-reply@easyserve.com}")
    private String fromEmail;

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Async
    public void sendReservationConfirmation(Reservation reservation) {
        String subject = "Reservation Confirmed at " + reservation.getRestaurant().getName();
        String message = "Hi " + reservation.getCustomer().getFirstName() +
                ", your reservation on " + reservation.getReservationDate() + " at " +
                reservation.getReservationTime() + " is confirmed.";

        sendEmail(reservation.getCustomer().getEmail(), subject, message);
        sendSMS(reservation.getCustomer().getPhone(), "[EasyServe] Your reservation is confirmed.");
    }

    @Async
    public void sendOrderStatusUpdate(Order order, Order.Status status) {
        String message = "Order status updated to: " + status.name();
        sendEmail(order.getCustomer().getEmail(), "Order Update", message);
        sendSMS(order.getCustomer().getPhone(), "[EasyServe] " + message);
    }

    @Async
    public void sendWelcomeEmail(User user) {
        String subject = "Welcome to EasyServe!";
        String body = "Hello " + user.getFirstName() + ",\n\nWelcome to EasyServe. Let’s make your restaurant run smarter!";
        sendEmail(user.getEmail(), subject, body);
    }

    @Async
    public void sendReservationCancellation(Reservation reservation) {
        String subject = "Reservation Cancelled";
        String body = "Your reservation at " + reservation.getRestaurant().getName() +
                " has been cancelled. We hope to see you again!";
        sendEmail(reservation.getCustomer().getEmail(), subject, body);
    }

    @Async
    public void sendDailyReport(Restaurant restaurant) {
        String subject = "Daily Report for " + restaurant.getName();
        String body = "Summary of today's activity at your restaurant. (MVP mock report)";
        sendEmail(restaurant.getEmail(), subject, body);
    }

    @Async
    public void sendMarketingMessage(List<Customer> customers, String message) {
        for (Customer c : customers) {
            if (c.isMarketingOptIn()) {
                sendEmail(c.getEmail(), "Exclusive Offer from " + c.getRestaurant().getName(), message);
                sendSMS(c.getPhone(), message);
            }
        }
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false); // plain text fallback for MVP

            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        // Mocked SMS sending (e.g. Twilio integration here later)
        log.info("SMS to {}: {}", phoneNumber, message);
    }
}
