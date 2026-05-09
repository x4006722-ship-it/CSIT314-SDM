package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class SaveFavouriteController {

    private final Favourite favourite = new Favourite();

    public boolean saveFavourite(int fraId, int userId, boolean remove) {
        return favourite.saveFavourite(fraId, userId, remove);
    }
}
