package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class SaveFavouriteController {

    private final Favourite favourite = new Favourite();

    public String saveFavourite(int fraId, int userId, boolean remove) {
        if (fraId <= 0 || userId <= 0) {
            return "Invalid fraId=" + fraId + " or userId=" + userId;
        }
        return favourite.saveFavourite(fraId, userId, remove);
    }
}
