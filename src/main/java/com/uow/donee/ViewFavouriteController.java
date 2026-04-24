package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class ViewFavouriteController {

    private final Donee donee = new Donee();

    public Object viewFavourite(int fraId) {
        Object data = donee.getViewFavourite(fraId);
        if (data == null) {
            return java.util.Map.of("error", "FRA not found.");
        }
        return data;
    }
}
