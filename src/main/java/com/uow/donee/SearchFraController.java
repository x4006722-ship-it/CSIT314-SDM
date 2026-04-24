package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class SearchFraController {

    private final Donee donee = new Donee();

    public Object searchFra(int userId, String title, String fraStatus, String categoryName) {
        return donee.getSearchFra(userId, title, fraStatus, categoryName);
    }
}
