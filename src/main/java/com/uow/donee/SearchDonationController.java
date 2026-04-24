package com.uow.donee;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class SearchDonationController {

    private final Donee donee = new Donee();

    public Object searchDonation(int userId, String title, String categoryName, String fraStatus) {
        if (userId <= 0) {
            return java.util.Map.of("error", "Not logged in.");
        }
        Object rows = donee.getSearchDonation(userId, title, categoryName, fraStatus);
        String em = donee.lastErrorMessage;
        String err = (em == null || em.isBlank()) ? "" : em;
        if (rows instanceof List && ((List<?>) rows).isEmpty() && !err.isBlank()) {
            return java.util.Map.of("error", err, "rows", rows);
        }
        return rows;
    }
}
