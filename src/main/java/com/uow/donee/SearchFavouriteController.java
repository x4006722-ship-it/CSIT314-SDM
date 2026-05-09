package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class SearchFavouriteController {

    private final Favourite favourite = new Favourite();

    public Object searchFavourite(Object searchFavouriteData) {
        return favourite.getSearchFavourite(searchFavouriteData);
    }
}
