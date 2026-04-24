package com.uow.donee;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class SearchFavouriteController {

    private final Donee donee = new Donee();

    public List<Map<String, Object>> searchFavourite(int userId, String keyword, String status, String category) {
        return donee.searchFavourites(userId, keyword, status, category);
    }
}
