package com.uow.donee;

import org.springframework.stereotype.Component;

/**
 * Handles searching within a user's favorite FRAs.
 * 
 * Responsibilities:
 * - Accept search/filter criteria
 * - Delegate to Favourite entity for database query
 * - Return filtered favorite FRAs
 */
@Component
public class SearchFavouriteController {

    private final Favourite favourite = new Favourite();

    /**
     * Searches within a user's favorite FRAs using filter criteria.
     * 
     * @param searchFavouriteData A Map containing userId and optional filters (title, fraStatus, categoryName)
     * @return List of favorite FRAs matching the criteria
     */
    public Object searchFavourite(Object searchFavouriteData) {
        return favourite.getSearchFavourite(searchFavouriteData);
    }
}
