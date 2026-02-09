package com.neuroisp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "isp_company")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IspCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // ===============================
    // CORE COMPANY INFO
    // ===============================
    private boolean active;           // ✅ SINGLE ACTIVE ISP
    private String name;

    private String email;             // billing/support email
    private String phone;             // support phone
    private String address;           // optional (PDFs)

    // ===============================
    // SMS CONFIG
    // ===============================
    @ManyToOne
    @JoinColumn(name = "sms_provider_id")
    private SmsProvider smsProvider;

    // ===============================
    // EMAIL BILLING TOGGLES (FRONTEND CONTROLLED)
    // ===============================
    private boolean emailInvoicesEnabled;   // 📄 Invoice emails ON/OFF
    private boolean emailReceiptsEnabled;   // 🧾 Receipt emails ON/OFF

    private String billingEmailFrom;        // e.g. billing@yourisp.co.ke

    // ===============================
    // OPTIONAL BRANDING (PDFs)
    // ===============================
    private String logoUrl;           // future: logo on invoices
    private String footerNote;        // e.g. "Thank you for choosing us"
}
