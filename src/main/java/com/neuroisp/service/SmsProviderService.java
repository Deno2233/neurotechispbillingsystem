package com.neuroisp.service;

import com.neuroisp.entity.SmsProvider;
import com.neuroisp.repository.SmsProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsProviderService {

    private final SmsProviderRepository smsProviderRepo;

    public SmsProvider create(SmsProvider provider) {
        return smsProviderRepo.save(provider);
    }

    public SmsProvider update(String id, SmsProvider provider) {
        SmsProvider existing = smsProviderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("SMS Provider not found"));

        existing.setName(provider.getName());
        existing.setApiUrl(provider.getApiUrl());
        existing.setApiKey(provider.getApiKey());
        existing.setSenderId(provider.getSenderId());
        existing.setActive(provider.isActive());

        return smsProviderRepo.save(existing);
    }

    public List<SmsProvider> findAll() {
        return smsProviderRepo.findAll();
    }

    public SmsProvider findById(String id) {
        return smsProviderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("SMS Provider not found"));
    }

    public void delete(String id) {
        smsProviderRepo.deleteById(id);
    }
}
