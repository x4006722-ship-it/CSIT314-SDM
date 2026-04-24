package com.uow.FRA;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SearchCompletedFRAByCategoryAndDateController {

    public List<FRA> searchCompletedByCategoryAndDatePeriod(String categoryId, String startDate, String endDate) {
        return FRA.findCompletedFRAsByCategoryAndDatePeriod(categoryId, startDate, endDate);
    }
}
