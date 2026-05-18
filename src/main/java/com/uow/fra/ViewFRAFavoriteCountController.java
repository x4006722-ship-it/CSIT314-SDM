package com.uow.fra;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ViewFRAFavoriteCountController {

    public List<FRA> viewFRAFavoriteCounts() {
        return FRA.findFRAsForFavoriteEngagementReport();
    }
}
