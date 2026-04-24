package com.uow.FRA;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ViewFRAFavoriteCountController {

    public List<FRA> viewFRAFavoriteCounts() {
        return FRA.findFRAsForFavoriteEngagementReport();
    }
}
