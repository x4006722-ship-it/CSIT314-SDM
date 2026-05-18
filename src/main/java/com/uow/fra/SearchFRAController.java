package com.uow.fra;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SearchFRAController {

    public List<FRA> searchFRA(String criteria, String categoryId, String status, String role, String userId, String startDate) {
        return FRA.findFRAsByCriteria(criteria, categoryId, status, role, userId, startDate);
    }
}
