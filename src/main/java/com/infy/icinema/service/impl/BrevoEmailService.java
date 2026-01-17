package com.infy.icinema.service.impl;

import com.infy.icinema.dto.BookingDTO;
import com.infy.icinema.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Primary
public class BrevoEmailService implements EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    @Autowired
    private TicketPdfService ticketPdfService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    @Override
    public void sendBookingConfirmation(String toEmail, BookingDTO bookingDTO) {
        try {
            if (brevoApiKey == null || brevoApiKey.isEmpty()) {
                System.err.println("Brevo API Key is missing. Skipping email.");
                return;
            }

            // 1. Prepare Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            // 2. Prepare Payload
            Map<String, Object> payload = new HashMap<>();

            // Sender
            Map<String, String> sender = new HashMap<>();
            sender.put("name", senderName);
            sender.put("email", senderEmail);
            payload.put("sender", sender);

            // To
            List<Map<String, String>> toList = new ArrayList<>();
            Map<String, String> to = new HashMap<>();
            to.put("email", toEmail);
            toList.add(to);
            payload.put("to", toList);

            // Content
            payload.put("subject", "Booking Confirmed! #" + bookingDTO.getId());
            payload.put("htmlContent", buildEmailContent(bookingDTO));

            // Attachment (PDF)
            try {
                byte[] pdfBytes = ticketPdfService.generateTicketPdf(bookingDTO);
                String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

                List<Map<String, String>> attachments = new ArrayList<>();
                Map<String, String> attachment = new HashMap<>();
                attachment.put("name", "iCinema_Ticket.pdf");
                attachment.put("content", base64Pdf);
                attachments.add(attachment);

                payload.put("attachment", attachments);
            } catch (Exception e) {
                System.err.println("Error generating PDF for Brevo: " + e.getMessage());
            }

            // 3. Send Request
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out
                        .println("Brevo Email sent successfully to: " + toEmail + ". Response: " + response.getBody());
            } else {
                System.err.println("Brevo API Error: " + response.getBody());
            }

        } catch (Exception e) {
            System.err.println("Failed to send email via Brevo: " + e.getMessage());
        }
    }

    private String buildEmailContent(BookingDTO booking) {
        // Reusing the same HTML template logic
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>"
                +
                "<h2 style='color: #E50914; text-align: center;'>iCinema Booking Confirmed!</h2>" +
                "<p>Hi there,</p>" +
                "<p>Your tickets are ready! Here are the details:</p>" +
                "<div style='background: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                "<p><strong>Movie:</strong> " + booking.getMovieTitle() + "</p>" +
                "<p><strong>Theatre:</strong> " + booking.getTheatreName() + " (" + booking.getCity() + ")</p>" +
                "<p><strong>Date & Time:</strong> " + booking.getShowDate() + " | " + booking.getShowTime() + "</p>" +
                "<p><strong>Seats:</strong> " + String.join(", ", booking.getSeatNumbers()) + "</p>" +
                "<p><strong>Total Paid:</strong> ₹" + booking.getTotalAmount() + "</p>" +
                "</div>" +
                "<p style='text-align: center; margin-top: 30px;'>" +
                "<a href='http://localhost:4200/bookings' style='background-color: #E50914; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>View My Bookings</a>"
                +
                "</p>" +
                "<p style='font-size: 12px; color: #777; text-align: center; margin-top: 20px;'>Thank you for choosing iCinema!</p>"
                +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
