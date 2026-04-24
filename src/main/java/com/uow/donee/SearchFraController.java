package com.uow.donee;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class SearchFraController {

    private final Donee donee = new Donee();

    public List<Map<String, Object>> searchCatalog(String keyword, String status, String category) {
        return donee.searchFraCatalog(keyword, status, category);
    }

    public Map<String, List<String>> getFilterOptions() {
        return donee.getBrowseFilterOptions();
    }

    public Set<Integer> getFavouritedFraIdsForUser(int userId) {
        return donee.getFavouritedFraIdsForUser(userId);
    }

    public String getLastErrorMessage() {
        return donee.lastErrorMessage;
    }
}
