package com.uow.FRA;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ViewFRAViewCountController {

    public List<FRA> viewFRAViewCounts() {
        return FRA.findFRAsForViewEngagementReport();
    }
}
