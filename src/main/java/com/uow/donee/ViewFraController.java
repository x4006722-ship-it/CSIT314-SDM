package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class ViewFraController {

    private final Donee donee = new Donee();

    public Object viewFra(int fraId) {
        Object data = donee.getViewFra(fraId);
        if (data == null) {
            return java.util.Map.of("error", "FRA not found.");
        }
        return data;
    }
}
