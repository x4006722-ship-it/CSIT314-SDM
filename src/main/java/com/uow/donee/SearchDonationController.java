package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class SearchDonationController {

    private final Donee donee = new Donee();

    public Object searchDonation(int userId, String title, String categoryName, String fraStatus) {
        if (userId <= 0) return java.util.List.of();
        return donee.getSearchDonation(userId, title, categoryName, fraStatus);
    }
}
