package com.uow.fra;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class SearchFRAController {
    public List<FRA> searchFRA(String criteria, String categoryId, String status, String role, String userId, String startDate) {
        // role and userId are required (access control); all other params are optional
        if (role == null || userId == null || userId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return FRA.findFRAsByCriteria(criteria, categoryId, status, role, userId, startDate);
    }
}