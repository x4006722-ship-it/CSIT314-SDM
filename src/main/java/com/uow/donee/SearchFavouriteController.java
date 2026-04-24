package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class SearchFavouriteController {

    private final Donee donee = new Donee();

    public Object searchFavourite(int userId, String title, String fraStatus, String categoryName) {
        if (userId <= 0) {
            return java.util.Map.of("error", "Not logged in.");
        }
        return donee.getSearchFavourite(userId, title, fraStatus, categoryName);
    }
}
