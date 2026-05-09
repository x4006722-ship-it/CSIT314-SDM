package com.uow.donee;

import com.uow.fra.FRA;
import org.springframework.stereotype.Component;

@Component
public class SearchDonationController {

    public Object searchDonation(Object searchDonationData) {
        return FRA.getSearchDonation(searchDonationData);
    }
}
