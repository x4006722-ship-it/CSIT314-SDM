package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class SaveFavouriteController {

    public final Donee donee = new Donee();

    public boolean saveFavourite(int userId, int fraId, boolean remove) {
        if (userId <= 0) { donee.lastErrorMessage = "Not logged in."; return false; }
        return donee.keepSaveFavourite(fraId, userId, remove);
    }
}
