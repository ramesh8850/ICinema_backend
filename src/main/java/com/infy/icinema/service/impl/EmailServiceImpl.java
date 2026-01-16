package com.infy.icinema.service.impl;

import com.infy.icinema.dto.BookingDTO;
import com.infy.icinema.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TicketPdfService ticketPdfService;

    @Async
    @Override
    public void sendBookingConfirmation(String toEmail, BookingDTO bookingDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Booking Confirmed! #" + bookingDTO.getId());

            String htmlContent = buildEmailContent(bookingDTO);
            helper.setText(htmlContent, true); // true = HTML

            // --- Attach PDF Ticket ---
            try {
                byte[] pdfBytes = ticketPdfService.generateTicketPdf(bookingDTO);
                helper.addAttachment("iCinema_Ticket.pdf", new ByteArrayResource(pdfBytes));
            } catch (Exception e) {
                System.err.println("Error generating/attaching PDF ticket: " + e.getMessage());
                // Continue sending email even if PDF fails
            }

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildEmailContent(BookingDTO booking) {
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
