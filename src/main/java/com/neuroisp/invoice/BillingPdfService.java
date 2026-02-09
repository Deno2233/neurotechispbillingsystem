package com.neuroisp.invoice;


import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.neuroisp.entity.BillingTransaction;
import com.neuroisp.entity.IspCompany;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class BillingPdfService {

    public byte[] generateInvoice(BillingTransaction tx, IspCompany isp) {

        Document doc = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, isp, "INVOICE");

            addLine(doc, "Invoice No: " + tx.getTransactionCode());
            addLine(doc, "Date: " + tx.getTransactionTime()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));

            doc.add(Chunk.NEWLINE);

            addLine(doc, "Billed To:");
            addLine(doc, tx.getUser().getFullName());
            addLine(doc, tx.getUser().getPhoneNumber());

            doc.add(Chunk.NEWLINE);

            addLine(doc, "Description: " + tx.getDescription());
            addLine(doc, "Amount Due: KES " + tx.getAmount());
            addLine(doc, "Account Balance: KES " + tx.getBalanceAfter());

            doc.close();

        } catch (Exception e) {
            throw new RuntimeException("Invoice PDF generation failed", e);
        }

        return out.toByteArray();
    }

    public byte[] generateReceipt(BillingTransaction tx, IspCompany isp) {

        Document doc = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, isp, "PAYMENT RECEIPT");

            addLine(doc, "Receipt No: " + tx.getTransactionCode());
            addLine(doc, "Date: " + tx.getTransactionTime()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));

            doc.add(Chunk.NEWLINE);

            addLine(doc, "Received From:");
            addLine(doc, tx.getUser().getFullName());
            addLine(doc, tx.getUser().getPhoneNumber());

            doc.add(Chunk.NEWLINE);

            addLine(doc, "Payment Method: " + tx.getPaymentMethod());
            if (tx.getPaymentReference() != null) {
                addLine(doc, "Reference: " + tx.getPaymentReference());
            }

            addLine(doc, "Amount Paid: KES " + tx.getAmount());
            addLine(doc, "Balance After Payment: KES " + tx.getBalanceAfter());

            doc.close();

        } catch (Exception e) {
            throw new RuntimeException("Receipt PDF generation failed", e);
        }

        return out.toByteArray();
    }

    private void addHeader(Document doc, IspCompany isp, String title)
            throws DocumentException {

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font normal = new Font(Font.HELVETICA, 11);

        Paragraph p = new Paragraph(isp.getName(), titleFont);
        p.setAlignment(Element.ALIGN_CENTER);
        doc.add(p);

        Paragraph t = new Paragraph(title, titleFont);
        t.setAlignment(Element.ALIGN_CENTER);
        doc.add(t);

        doc.add(new Paragraph(" ", normal));
    }

    private void addLine(Document doc, String text)
            throws DocumentException {
        doc.add(new Paragraph(text, new Font(Font.HELVETICA, 11)));
    }
}
