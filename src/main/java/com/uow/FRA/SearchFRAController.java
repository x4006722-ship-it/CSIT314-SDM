package com.uow.FRA;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SearchFRAController {
    public List<FRA> searchFRA(String criteria) {
        return FRA.findFRAsByCriteria(criteria);
    }
}