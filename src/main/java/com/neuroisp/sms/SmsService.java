package com.neuroisp.sms;

import com.neuroisp.entity.IspCompany;
import com.neuroisp.sms.BytewaveSmsSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final BytewaveSmsSender bytewaveSmsSender;

    public void send(IspCompany isp, String phone, String message) {

        String providerName = isp.getSmsProvider().getName();

        switch (providerName) {
            case "BYTEWAVE" -> bytewaveSmsSender.sendSms(phone, message);
            default -> throw new RuntimeException("Unsupported SMS provider");
        }
    }
}
