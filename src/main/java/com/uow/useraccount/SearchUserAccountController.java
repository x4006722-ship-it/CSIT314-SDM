package com.uow.useraccount;
import org.springframework.stereotype.Controller;

@Controller
public class SearchUserAccountController {

    private final UserAccount userAccount = new UserAccount();
    public Object searchAccount(Object searchData) {
        return userAccount.getSearchAccount(searchData);
    }
}
