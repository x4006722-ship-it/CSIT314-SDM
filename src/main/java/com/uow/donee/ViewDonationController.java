package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class ViewDonationController {

    private final Donee donee = new Donee();

    public Object viewDonation(int fraId) {
        Object data = donee.getViewDonation(fraId);
        if (data == null) {
            return java.util.Map.of("error", "FRA not found.");
        }
        return data;
    }
}
