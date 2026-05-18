package com.uow.useraccount;

import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;

@Controller
public class ViewUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    public Object viewAccount(Integer userId, HttpSession session) {
        int resolved = userId != null && userId > 0 ? userId : 0;

        if (resolved <= 0 && session != null) {
            Object sid = session.getAttribute("userId");
            if (sid instanceof Number n) {
                int v = n.intValue();
                if (v > 0) resolved = v;
            } else if (sid != null) {
                try {
                    int v = Integer.parseInt(String.valueOf(sid).trim());
                    if (v > 0) resolved = v;
                } catch (NumberFormatException ignored) {}
            }
        }

        if (resolved <= 0 && session != null) {
            Object un = session.getAttribute("username");
            if (un != null) {
                String username = String.valueOf(un).trim();
                if (!username.isBlank()) {
                    resolved = userAccount.findUserIdByUsername(username);
                }
            }
        }

        if (resolved <= 0) {
            return null;
        }
        return userAccount.getViewAccount(resolved);
    }
}
