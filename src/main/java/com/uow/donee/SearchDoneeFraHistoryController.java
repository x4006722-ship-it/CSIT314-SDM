package com.uow.donee;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class SearchDoneeFraHistoryController {

    private final Donee donee = new Donee();

    public List<Map<String, Object>> searchHistory(
            int userId,
            String keyword,
            String category,
            String dateFrom,
            String dateTo,
            String status) {
        return donee.searchMyFraHistory(userId, keyword, category, dateFrom, dateTo, status);
    }

    public String getErrorMessage() {
        return donee.lastErrorMessage;
    }
}
