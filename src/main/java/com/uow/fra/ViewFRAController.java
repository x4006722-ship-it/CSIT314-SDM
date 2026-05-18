package com.uow.fra;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

import com.uow.useraccount.UserAccount;

@Service
public class ViewFRAController {

    private final UserAccount userAccount = new UserAccount();

    public List<FRA> viewAllFRAs(String fundRaiserId) {
        if (fundRaiserId == null || fundRaiserId.isBlank()) {
            return new ArrayList<>();
        }
        return FRA.findAllFRAs(fundRaiserId);
    }

    public Object getDoneeOptions() {
        return userAccount.getDoneeOptions();
    }

    public Object getFundRaiserOptions() {
        return userAccount.getFundRaiserOptions();
    }
}
