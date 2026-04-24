package com.uow.donee;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ViewDoneeFraHistoryController {

    private final Donee donee = new Donee();

    public List<Map<String, Object>> viewHistory(
            int userId,
            String category,
            String dateFrom,
            String dateTo,
            String status) {
        return donee.viewMyFraHistory(userId, category, dateFrom, dateTo, status);
    }

    public String getErrorMessage() {
        return donee.lastErrorMessage;
    }
}
