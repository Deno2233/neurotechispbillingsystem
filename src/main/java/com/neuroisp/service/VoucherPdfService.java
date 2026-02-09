package com.neuroisp.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.neuroisp.entity.Voucher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.imageio.ImageIO;

@Service
@RequiredArgsConstructor
public class VoucherPdfService {

    /**
     * Generate PDF bytes with vouchers + QR codes
     */
    public byte[] generatePdf(List<Voucher> vouchers, String domain) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        for (Voucher v : vouchers) {
            // Voucher header
            Paragraph title = new Paragraph("Voucher PIN: " + v.getCode(), titleFont);
            document.add(title);
            document.add(new Paragraph("Package: " + v.getInternetPackage().getName(), normalFont));
            document.add(new Paragraph("Status: " + (v.isUsed() ? "Used" : "Unused"), normalFont));
            document.add(Chunk.NEWLINE);

            // Generate QR code
            Image qrImage = Image.getInstance(generateQrImage(domain + "/hotspot/voucher/redeem?pin=" + v.getCode()));
            qrImage.scaleAbsolute(150, 150);
            document.add(qrImage);

            // Spacer between vouchers
            document.add(Chunk.NEWLINE);
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);
        }

        document.close();
        return out.toByteArray();
    }

    /**
     * Generate QR code as byte array (PNG)
     */
    private byte[] generateQrImage(String text) throws WriterException {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(
                    qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 150, 150)
            );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
}
