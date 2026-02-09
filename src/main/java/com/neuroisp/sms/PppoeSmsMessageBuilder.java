package com.neuroisp.sms;



import com.neuroisp.entity.IspCompany;
import com.neuroisp.entity.PppoeSubscription;

import java.time.format.DateTimeFormatter;

public class PppoeSmsMessageBuilder {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Welcome + activation message
     */
    public static String buildActivationMessage(
            IspCompany isp,
            PppoeSubscription subscription
    ) {
        return String.format(
                """
                %s INTERNET

                Welcome to %s 🎉
                Your PPPoE internet subscription is now ACTIVE.

                Package: %s
                Username: %s
                Password:%s
                Valid until: %s

                Thank you for choosing %s.
                Enjoy fast & reliable internet 🚀
                """,
                isp.getName().toUpperCase(),
                isp.getName(),
                subscription.getPppoePackage().getName(),
                subscription.getUser().getUsername(),
                subscription.getUser().getPassword(),
                subscription.getExpiryTime().format(DATE_FORMAT),
                isp.getName()
        ).trim();
    }

    /**
     * Expiry notification message
     */
    public static String buildExpiryMessage(
            IspCompany isp,
            PppoeSubscription subscription
    ) {
        return String.format(
                """
                %s INTERNET

                Dear customer,
                Your PPPoE internet subscription has EXPIRED.

                Package: %s
                Username: %s
                Expired on: %s

                Please renew to continue enjoying our services.
                %s
                """,
                isp.getName().toUpperCase(),
                subscription.getPppoePackage().getName(),
                subscription.getUser().getUsername(),
                subscription.getExpiryTime().format(DATE_FORMAT),
                isp.getName()
        ).trim();
    }
}
