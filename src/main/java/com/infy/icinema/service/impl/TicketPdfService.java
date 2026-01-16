package com.infy.icinema.service.impl;

import com.infy.icinema.dto.BookingDTO;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class TicketPdfService {

    @Autowired
    private QrCodeService qrCodeService;

    @Value("${jwt.secret}")
    private String secret;

    public String generateSecurePayload(Long bookingId) {
        try {
            String data = "BID:" + bookingId;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String hash = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes()));

            // Format: DATA|SIGNATURE
            return data + "|SIG:" + hash.substring(0, 15); // Shorten for QR readability, still secure
        } catch (Exception e) {
            throw new RuntimeException("Error signing QR code", e);
        }
    }

    public byte[] generateTicketPdf(BookingDTO booking) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20, 20, 20, 20);

            // --- Header (iCinema Brand) ---
            Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 1 }));
            headerTable.setWidth(UnitValue.createPercentValue(100));

            Cell brandCell = new Cell()
                    .add(new Paragraph("iCinema").setFontSize(24).setBold().setFontColor(ColorConstants.RED));
            brandCell.setBorder(Border.NO_BORDER);
            brandCell.setTextAlignment(TextAlignment.CENTER);
            headerTable.addCell(brandCell);

            Cell subTitleCell = new Cell()
                    .add(new Paragraph("Your Ticket to the Future").setFontSize(10).setFontColor(ColorConstants.GRAY));
            subTitleCell.setBorder(Border.NO_BORDER);
            subTitleCell.setTextAlignment(TextAlignment.CENTER);
            headerTable.addCell(subTitleCell);

            document.add(headerTable);
            document.add(new Paragraph("\n")); // Spacer

            // --- Ticket Card Style Container ---
            Table borderTable = new Table(UnitValue.createPercentArray(new float[] { 1 }));
            borderTable.setWidth(UnitValue.createPercentValue(100));
            // borderTable.setBorder(new SolidBorder(ColorConstants.BLACK, 1)); // iText 7
            // doesn't support easy borders on tables like this simply

            // --- Movie Details ---
            document.add(new Paragraph("Booking ID: #" + booking.getId()).setBold().setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(new Paragraph(booking.getMovieTitle())
                    .setBold().setFontSize(20).setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(booking.getTheatreName() + " | " + booking.getCity())
                    .setFontSize(12).setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(booking.getShowDate() + " at " + booking.getShowTime())
                    .setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            // --- Seats & QR Code Row ---
            Table detailsTable = new Table(UnitValue.createPercentArray(new float[] { 2, 1 }));
            detailsTable.setWidth(UnitValue.createPercentValue(100));

            // Left: Seat Numbers
            String seatText = "Seats: " + String.join(", ", booking.getSeatNumbers());
            Cell seatsCell = new Cell().add(new Paragraph(seatText).setFontSize(16).setBold());
            seatsCell.setBorder(Border.NO_BORDER);
            seatsCell.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            detailsTable.addCell(seatsCell);

            // Right: QR Code
            try {
                // Generate Secure Payload
                String securePayload = generateSecurePayload(booking.getId());

                // Generate QR Code for Booking ID
                byte[] qrBytes = qrCodeService.generateQrCode(securePayload, 200, 200);
                ImageData qrData = ImageDataFactory.create(qrBytes);
                Image qrImage = new Image(qrData);
                qrImage.setAutoScale(true);

                Cell qrCell = new Cell().add(qrImage);
                qrCell.setBorder(Border.NO_BORDER);
                qrCell.setTextAlignment(TextAlignment.RIGHT);
                detailsTable.addCell(qrCell);
            } catch (Exception e) {
                // If QR fails, just show text
                detailsTable.addCell(new Cell().add(new Paragraph("QR Error")).setBorder(Border.NO_BORDER));
            }

            document.add(detailsTable);

            document.add(new Paragraph("\n"));

            // --- Price Breakdown ---
            document.add(new Paragraph("Seat Cost: " + String.format("%.2f", booking.getSeatCost()) + " INR").setFontSize(10).setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Convenience Fee: " + String.format("%.2f", booking.getConvenienceFee()) + " INR").setFontSize(10).setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("GST: " + String.format("%.2f", booking.getGst()) + " INR").setFontSize(10).setTextAlignment(TextAlignment.RIGHT));
            
            if (booking.getDiscountAmount() != null && booking.getDiscountAmount() > 0) {
                 document.add(new Paragraph("Discount: -" + String.format("%.2f", booking.getDiscountAmount()) + " INR").setFontSize(10).setFontColor(ColorConstants.GREEN).setTextAlignment(TextAlignment.RIGHT));
            }
            
            document.add(new Paragraph("Total Paid: " + String.format("%.2f", booking.getTotalAmount()) + " INR").setBold().setFontSize(14).setTextAlignment(TextAlignment.RIGHT));

            // --- Footer ---
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Please show this QR code at the entrance.").setFontSize(10)
                    .setFontColor(ColorConstants.GRAY).setTextAlignment(TextAlignment.CENTER));

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
