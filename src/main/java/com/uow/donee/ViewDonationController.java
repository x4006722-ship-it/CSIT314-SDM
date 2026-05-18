package com.uow.donee;
import com.uow.fra.FRA;
import org.springframework.stereotype.Component;

@Component
public class ViewDonationController {
    public Object viewDonation(int fraId) {
        return FRA.getViewDonation(fraId);
    }
}
