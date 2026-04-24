package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class ViewFavouriteFraController {

    private final Donee donee = new Donee();

    public Object viewFavouriteFraDetails(int fraId) {
        return donee.getFraViewForModal(fraId);
    }

    public String getLastErrorMessage() {
        return donee.lastErrorMessage;
    }
}
