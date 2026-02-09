package com.neuroisp.sms;

import com.neuroisp.entity.IspCompany;
import com.neuroisp.entity.UserSubscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionSmsBuilder {

    public String build(UserSubscription sub , IspCompany isp) {

        return String.format(
                "Welcome to %s!\n" +
                        "Package: %s\n" +
                        "Username: %s\n" +
                        "Password: %s\n" +
                        "Enjoy your internet 😊",
                isp.getName().toUpperCase(),
               // isp.getName(),
                sub.getInternetPackage().getName(),
                sub.getUsername(),
                sub.getPassword()
        );
    }
}
