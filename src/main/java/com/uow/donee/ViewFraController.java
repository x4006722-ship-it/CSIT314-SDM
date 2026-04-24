package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class ViewFraController {

    private final Donee donee = new Donee();

    public Object viewFraDetails(int fraId) {
        return donee.getFraViewForModal(fraId);
    }

    public String getLastErrorMessage() {
        return donee.lastErrorMessage;
    }
}
