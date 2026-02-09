package com.neuroisp.service;

import com.neuroisp.entity.IspCompany;
import com.neuroisp.entity.SmsProvider;
import com.neuroisp.repository.IspCompanyRepository;
import com.neuroisp.repository.SmsProviderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IspCompanyService {

    private final IspCompanyRepository ispRepo;
    private final SmsProviderRepository smsProviderRepo;

    public IspCompany create(IspCompany isp) {
        return ispRepo.save(isp);
    }

    public IspCompany update(String id, IspCompany isp) {

        IspCompany existing = ispRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("ISP not found"));

        if (isp.isActive()) {
            deactivateAll();
        }

        existing.setName(isp.getName());
        existing.setEmail(isp.getEmail());
        existing.setPhone(isp.getPhone());
        existing.setAddress(isp.getAddress());
        existing.setActive(isp.isActive());
        existing.setEmailInvoicesEnabled(isp.isEmailInvoicesEnabled());
        existing.setEmailReceiptsEnabled(isp.isEmailReceiptsEnabled());
        existing.setBillingEmailFrom(isp.getBillingEmailFrom());
        existing.setLogoUrl(isp.getLogoUrl());
        existing.setFooterNote(isp.getFooterNote());

        if (isp.getSmsProvider() != null) {
            SmsProvider provider = smsProviderRepo.findById(isp.getSmsProvider().getId())
                    .orElseThrow(() -> new RuntimeException("SMS Provider not found"));
            existing.setSmsProvider(provider);
        } else {
            existing.setSmsProvider(null);
        }

        return ispRepo.save(existing);
    }

    private void deactivateAll() {
        ispRepo.findAll().forEach(c -> c.setActive(false));
    }

    public List<IspCompany> findAll() {
        return ispRepo.findAll();
    }

    public IspCompany findById(String id) {
        return ispRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("ISP not found"));
    }

    public void assignSmsProvider(String ispId, String smsProviderId) {

        IspCompany isp = ispRepo.findById(ispId)
                .orElseThrow(() -> new RuntimeException("ISP not found"));

        SmsProvider provider = smsProviderRepo.findById(smsProviderId)
                .orElseThrow(() -> new RuntimeException("SMS Provider not found"));

        isp.setSmsProvider(provider);
        ispRepo.save(isp);
    }

    public void delete(String id) {
        ispRepo.deleteById(id);
    }
    public IspCompany getActiveCompany() {
        return (IspCompany) ispRepo.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active ISP company configured"));
    }
    @Transactional
    public IspCompany activateCompany(String id) {

        ispRepo.findAll().forEach(c -> c.setActive(false));

        IspCompany isp = ispRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("ISP not found"));

        isp.setActive(true);
        return ispRepo.save(isp);
    }
    public List<IspCompany>  findActive() {
        return ispRepo.findAllByActiveTrue()
               ;
    }


}
