package com.neuroisp.contoller;

import com.neuroisp.entity.BillingTransaction;
import com.neuroisp.invoice.BillingPdfService;
import com.neuroisp.repository.BillingTransactionRepository;
import com.neuroisp.service.IspCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing/pdf")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://192.168.100.3:4567",
        "http://192.168.15.26:8061"
})
public class BillingPdfController {

    private final BillingTransactionRepository billingRepo;
    private final BillingPdfService pdfService;
    private final IspCompanyService ispCompanyService;

    @GetMapping("/invoice/{txId}")
    public ResponseEntity<byte[]> invoice(@PathVariable String txId) {

        BillingTransaction tx = billingRepo.findById(txId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        byte[] pdf = pdfService.generateInvoice(
                tx,
                ispCompanyService.getActiveCompany()
        );

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=invoice-" + tx.getTransactionCode() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/receipt/{txId}")
    public ResponseEntity<byte[]> receipt(@PathVariable String txId) {

        BillingTransaction tx = billingRepo.findById(txId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        byte[] pdf = pdfService.generateReceipt(
                tx,
                ispCompanyService.getActiveCompany()
        );

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=receipt-" + tx.getTransactionCode() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
