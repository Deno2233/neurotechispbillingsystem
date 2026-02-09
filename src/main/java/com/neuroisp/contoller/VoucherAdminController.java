package com.neuroisp.contoller;



import com.neuroisp.entity.Voucher;
import com.neuroisp.repository.VoucherRepository;
import com.neuroisp.service.BulkVoucherService;
import com.neuroisp.service.VoucherExportService;
import com.neuroisp.service.VoucherPdfService;
import com.neuroisp.service.VoucherQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vouchers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class VoucherAdminController {
    private final VoucherQueryService voucherQueryService;
    private final VoucherRepository voucherRepository;
    private final BulkVoucherService bulkVoucherService;
    private final VoucherExportService exportService;
    private final VoucherPdfService voucherPdfService;
    /**
     * Generate vouchers in bulk and export CSV
     *
     * Example:
     * POST /admin/vouchers/generate?packageId=xxx&quantity=10
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateVouchers(
            @RequestParam String packageId,
            @RequestParam int quantity
    ) {
        // Generate vouchers
        List<Voucher> vouchers = bulkVoucherService.generateBulk(packageId, quantity);

        // Export CSV
        String csv = exportService.exportCsv(vouchers);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=vouchers.csv")
                .header("Content-Type", "text/csv")
                .body(csv);
    }

    /**
     * Optional: Get all vouchers for a package (for admin view)
     */
    @GetMapping
    public List<Voucher> getVouchersByPackage(@RequestParam String packageId) {
        return bulkVoucherService.getVouchersByPackage(packageId);
    }
    @PostMapping("/generate/pdf")
    public ResponseEntity<byte[]> generatePdf(
            @RequestParam String packageId,
            @RequestParam int quantity
    ) throws Exception {

        List<Voucher> vouchers = bulkVoucherService.generateBulk(packageId, quantity);

        byte[] pdfBytes = voucherPdfService.generatePdf(vouchers, "https://yourdomain.com");

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=vouchers.pdf")
                .header("Content-Type", "application/pdf")
                .body(pdfBytes);
    }
    @GetMapping("/unused")
    public List<Voucher> getUnusedVouchers() {
        return voucherQueryService.getUnusedVouchers();
    }
    @GetMapping("/{voucherId}/pdf")
    public ResponseEntity<byte[]> downloadVoucherPdf(
            @PathVariable String voucherId
    ) throws Exception {

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        byte[] pdfBytes = voucherPdfService.generatePdf(
                List.of(voucher),
                "https://yourdomain.com"
        );

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=voucher-" + voucher.getCode() + ".pdf"
                )
                .header("Content-Type", "application/pdf")
                .body(pdfBytes);
    }

}
