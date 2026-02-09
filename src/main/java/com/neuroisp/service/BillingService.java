package com.neuroisp.service;

import com.neuroisp.entity.*;
import com.neuroisp.invoice.BillingPdfService;
import com.neuroisp.repository.BillingTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillingTransactionRepository billingRepo;
    private final BillingPdfService pdfService;
    private final BillingEmailService billingEmailService;
    private final IspCompanyService ispCompanyService;
    /* ------------------ HELPERS ------------------ */

    private String generateTxCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
    }

    private double calculateBalance(String userId) {
        return billingRepo.findByUserIdOrderByTransactionTimeAsc(userId)
                .stream()
                .mapToDouble(tx ->
                        tx.getType() == TransactionType.CREDIT
                                ? tx.getAmount()
                                : -tx.getAmount()
                ).sum();
    }

    /* ------------------ CHARGE (CREDIT) ------------------ */

    public BillingTransaction createSubscriptionCharge(
            PppoeSubscription subscription
    ) {
        double balanceBefore = calculateBalance(subscription.getUser().getId());
        double balanceAfter = balanceBefore + subscription.getPppoePackage().getPrice();

        BillingTransaction tx = BillingTransaction.builder()
                .transactionCode(generateTxCode())
                .user(subscription.getUser())
                .subscription(subscription)
                .type(TransactionType.CREDIT)
                .paymentMethod(PaymentMethod.SYSTEM)
                .amount(subscription.getPppoePackage().getPrice())
                .description("PPPoE Subscription: " + subscription.getPppoePackage().getName())
                .transactionTime(LocalDateTime.now())
                .balanceAfter(balanceAfter)
                .build();

        billingRepo.save(tx);

        // 🔽 EMAIL INVOICE (OPTIONAL)
        maybeEmailInvoice(tx);

        return tx;
    }


    /* ------------------ PAYMENT ------------------ */

    public BillingTransaction recordPayment(
            PppoeUser user,
            Double amount,
            PaymentMethod method,
            String reference,
            String description
    ) {
        if (method == PaymentMethod.MPESA && reference != null) {
            if (billingRepo.existsByPaymentReference(reference)) {
                throw new RuntimeException("Duplicate MPESA receipt: " + reference);
            }
        }

        double balanceBefore = getBalance(user.getId());
        double balanceAfter = balanceBefore - amount;

        BillingTransaction tx = BillingTransaction.builder()
                .transactionCode(generateTxCode())
                .user(user)
                .type(TransactionType.PAYMENT)
                .paymentMethod(method)
                .amount(amount)
                .paymentReference(reference)
                .description(description)
                .transactionTime(LocalDateTime.now())
                .balanceAfter(balanceAfter)
                .build();

        return billingRepo.save(tx);
    }


    /* ------------------ BALANCE ------------------ */

    //  public double getAccountBalance(String userId) {
    //    return calculateBalance(userId);
    // }

    // public List<BillingTransaction> getStatement(String userId) {
    //    return billingRepo.findByUserIdOrderByTransactionTimeAsc(userId);
    // }
    public List<BillingTransaction> getStatement(String userId) {
        return billingRepo.findByUserIdOrderByTransactionTimeAsc(userId);
    }

    public double getBalance(String userId) {
        return billingRepo.findByUserIdOrderByTransactionTimeAsc(userId)
                .stream()
                .mapToDouble(tx ->
                        tx.getType() == TransactionType.CREDIT
                                ? tx.getAmount()
                                : -tx.getAmount()
                ).sum();
    }
 /* ===============================
       PRIVATE EMAIL HELPERS
       =============================== */

    private void maybeEmailInvoice(BillingTransaction tx) {

        IspCompany isp = ispCompanyService.getActiveCompany();

        // Admin toggle OFF → do nothing
        if (!isp.isEmailInvoicesEnabled()) return;

        // No customer email → do nothing
        if (tx.getUser().getEmail() == null) return;

        byte[] pdf = pdfService.generateInvoice(tx, isp);

        billingEmailService.sendPdf(
                tx.getUser().getEmail(),
                isp.getBillingEmailFrom(),
                "Invoice " + tx.getTransactionCode(),
                "Dear " + tx.getUser().getFullName()
                        + ",\n\nPlease find your invoice attached.\n\nRegards,\n"
                        + isp.getName(),
                pdf,
                "invoice-" + tx.getTransactionCode() + ".pdf"
        );
    }

    private void maybeEmailReceipt(BillingTransaction tx) {

        IspCompany isp = ispCompanyService.getActiveCompany();

        if (!isp.isEmailReceiptsEnabled()) return;
        if (tx.getUser().getEmail() == null) return;

        byte[] pdf = pdfService.generateReceipt(tx, isp);

        billingEmailService.sendPdf(
                tx.getUser().getEmail(),
                isp.getBillingEmailFrom(),
                "Payment Receipt " + tx.getTransactionCode(),
                "Dear " + tx.getUser().getFullName()
                        + ",\n\nThank you for your payment. Receipt attached.\n\nRegards,\n"
                        + isp.getName(),
                pdf,
                "receipt-" + tx.getTransactionCode() + ".pdf"
        );
    }
    /* ------------------ UPGRADE SUBSCRIPTION ------------------ */

    @Transactional
    public void createUpgradeCharge(
            PppoeSubscription currentSubscription,
            PppoePackage newPackage
    ) {
        PppoeUser user = currentSubscription.getUser();

        boolean isExpired = currentSubscription.getExpiryTime() == null
                || currentSubscription.getExpiryTime().isBefore(LocalDateTime.now());

        double upgradeCost;

        if (isExpired) {
            upgradeCost = newPackage.getPrice();
        } else {
            double oldPrice = currentSubscription.getPppoePackage().getPrice();
            double newPrice = newPackage.getPrice();
            upgradeCost = Math.max(newPrice - oldPrice, 0);
        }

        if (upgradeCost <= 0) return;

        double balanceBefore = getBalance(user.getId());

    /* ============================
       CASE 1: USER HAS ADVANCE
       ============================ */
        if (balanceBefore < 0) {

            double advance = Math.abs(balanceBefore);

            // Fully covered by advance
            if (advance >= upgradeCost) {

                recordPayment(
                        user,
                        upgradeCost,
                        PaymentMethod.SYSTEM,
                        "ADVANCE-UPGRADE",
                        "Upgrade paid from advance balance"
                );
                return;
            }

            // Partially covered by advance
            recordPayment(
                    user,
                    advance,
                    PaymentMethod.SYSTEM,
                    "ADVANCE-UPGRADE",
                    "Partial upgrade paid from advance balance"
            );

            upgradeCost += advance;
        }

    /* ============================
       CASE 2: REMAINING COST → CREDIT
       ============================ */
        double balanceAfter = getBalance(user.getId()) + upgradeCost;

        BillingTransaction creditTx = BillingTransaction.builder()
                .transactionCode(generateTxCode())
                .user(user)
                .subscription(currentSubscription)
                .type(TransactionType.CREDIT)
                .paymentMethod(PaymentMethod.SYSTEM)
                .amount(upgradeCost)
                .description(
                        "Subscription upgrade to " + newPackage.getName()
                )
                .transactionTime(LocalDateTime.now())
                .balanceAfter(balanceAfter)
                .build();

        billingRepo.save(creditTx);
        maybeEmailInvoice(creditTx);
    }


}
