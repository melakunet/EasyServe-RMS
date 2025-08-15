
package com.easyserve.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class NotificationService {

    @Value("${app.notifications.email.from:no-reply@easyserve.com}")
    private String fromEmail;

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    // Mock notification methods - perfect for MVP demonstration

    @Async
    public void sendReservationConfirmation(String customerEmail, String customerName, 
                                          String restaurantName, LocalDate date, LocalTime time) {
        String subject = "Reservation Confirmed at " + restaurantName;
        String message = "Hi " + customerName +
                ", your reservation on " + date + " at " + time + " is confirmed.";

        sendEmail(customerEmail, subject, message);
        log.info("Reservation confirmation sent to {}", customerEmail);
    }

    @Async
    public void sendOrderStatusUpdate(String customerEmail, String customerPhone, 
                                    Long orderId, String status) {
        String subject = "Order Update - " + orderId;
        String message = "Order status updated to: " + status;
        
        sendEmail(customerEmail, subject, message);
        sendSMS(customerPhone, "[EasyServe] " + message);
        log.info("Order status update sent to {} for order #{}", customerEmail, orderId);
    }

    @Async
    public void sendWelcomeEmail(String userEmail, String firstName) {
        String subject = "Welcome to EasyServe!";
        String body = "Hello " + firstName + ",\n\nWelcome to EasyServe. Let's make your restaurant run smarter!";
        sendEmail(userEmail, subject, body);
    }

    @Async
    public void sendReservationCancellation(String customerEmail, String restaurantName) {
        String subject = "Reservation Cancelled";
        String body = "Your reservation at " + restaurantName +
                " has been cancelled. We hope to see you again!";
        sendEmail(customerEmail, subject, body);
    }

    @Async
    public void sendDailyReport(String restaurantEmail, String restaurantName) {
        String subject = "Daily Report for " + restaurantName;
        String body = "Summary of today's activity at your restaurant:\n\n" +
                     "• Total Reservations: 12\n" +
                     "• Total Orders: 8\n" +
                     "• Revenue: $450.00\n" +
                     "• No-shows: 1\n\n" +
                     "This is a mock report for MVP demonstration.";
        sendEmail(restaurantEmail, subject, body);
    }

    @Async
    public void sendMarketingMessage(List<String> customerEmails, String restaurantName, String message) {
        String subject = "Exclusive Offer from " + restaurantName;
        for (String email : customerEmails) {
            sendEmail(email, subject, message);
            log.info("Marketing email sent to {}", email);
        }
    }

    @Async
    public void sendTableReadyNotification(String customerEmail, String customerPhone, 
                                         String restaurantName, int partySize) {
        String subject = "Your Table is Ready!";
        String message = "Hi! Your table for " + partySize + " at " + restaurantName + 
                        " is now ready. Please come to the restaurant.";
        
        sendEmail(customerEmail, subject, message);
        sendSMS(customerPhone, "[EasyServe] Table ready at " + restaurantName + "!");
    }

    @Async
    public void sendOrderReadyNotification(String customerEmail, String customerPhone, 
                                         Long orderId, String orderType) {
        String subject = "Order Ready - " + orderId;
        String message = "Your " + orderType.toLowerCase() + " order is ready!";
        
        if ("PICKUP".equals(orderType)) {
            message += " Please come to pick up your order.";
        } else if ("DELIVERY".equals(orderType)) {
            message += " Your order is on the way!";
        }
        
        sendEmail(customerEmail, subject, message);
        sendSMS(customerPhone, "[EasyServe] " + message);
        log.info("Order ready notification sent to {} for order #{}", customerEmail, orderId);
    }

    @Async
    public void sendPasswordResetEmail(String userEmail, String resetToken) {
        String subject = "Password Reset Request";
        String body = "Click the link below to reset your password:\n\n" +
                     "http://localhost:8080/reset-password?token=" + resetToken + "\n\n" +
                     "This link will expire in 1 hour.";
        sendEmail(userEmail, subject, body);
    }

    // Mock email sending - logs instead of actual email for MVP
    private void sendEmail(String to, String subject, String text) {
        log.info("=== EMAIL NOTIFICATION ===");
        log.info("To: {}", to);
        log.info("From: {}", fromEmail);
        log.info("Subject: {}", subject);
        log.info("Body: {}", text);
        log.info("========================");
        
        // In production, this would actually send email
        // For MVP, we just log it to show the functionality works
    }

    // Mock SMS sending - logs instead of actual SMS for MVP
    private void sendSMS(String phoneNumber, String message) {
        log.info("=== SMS NOTIFICATION ===");
        log.info("To: {}", phoneNumber);
        log.info("Message: {}", message);
        log.info("======================");
        
        // In production, this would integrate with Twilio or similar service
        // For MVP, we just log it to demonstrate the functionality
    }

    // Utility methods for testing
    public void sendTestEmail(String to) {
        sendEmail(to, "EasyServe Test Email", "This is a test email from EasyServe system.");
    }

    @Async
    public void sendBulkNotification(List<String> emails, String subject, String message) {
        for (String email : emails) {
            sendEmail(email, subject, message);
        }
        log.info("Bulk notification sent to {} recipients", emails.size());
    }

    // Business notification methods
    @Async
    public void notifyStaffNewReservation(String staffEmail, String customerName, LocalDate date, LocalTime time) {
        String subject = "New Reservation Alert";
        String message = "New reservation: " + customerName + " for " + date + " at " + time;
        sendEmail(staffEmail, subject, message);
    }

    @Async
    public void notifyKitchenNewOrder(Long orderId, String orderDetails) {
        log.info("=== KITCHEN NOTIFICATION ===");
        log.info("New Order: {}", orderId);
        log.info("Details: {}", orderDetails);
        log.info("===========================");
    }

    // Additional methods needed by OrderService and other services
    @Async
    public void sendReservationConfirmation(String customerEmail, String customerPhone, 
                                          Long reservationId, String restaurantName, 
                                          String dateTime, int partySize) {
        String subject = "Reservation Confirmed at " + restaurantName;
        String message = String.format("Your reservation #%d for %d people on %s is confirmed!", 
                                      reservationId, partySize, dateTime);
        
        sendEmail(customerEmail, subject, message);
        if (customerPhone != null && !customerPhone.isEmpty()) {
            sendSMS(customerPhone, "[EasyServe] " + message);
        }
        log.info("Reservation confirmation sent to {} for reservation #{}", customerEmail, reservationId);
    }

    @Async
    public void sendWelcomeNotification(String customerEmail, String customerName) {
        String subject = "Welcome to EasyServe!";
        String message = String.format("Welcome to EasyServe, %s! Thank you for joining us.", customerName);
        
        sendEmail(customerEmail, subject, message);
        log.info("Welcome notification sent to {} ({})", customerName, customerEmail);
    }
}