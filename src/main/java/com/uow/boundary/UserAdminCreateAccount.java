package com.uow.boundary;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uow.control.UserAdminCreateAccountController;

@Controller
@RequestMapping("/api/accounts")
/**
 * Boundary for user admin account creation form submission.
 * Redirects back to ManageAccount.html with a toast message.
 */
public class UserAdminCreateAccount {

    private final UserAdminCreateAccountController controller;

    @Autowired
    public UserAdminCreateAccount(UserAdminCreateAccountController controller) {
        this.controller = controller;
    }

    @PostMapping("/create")
    public String submitForm(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("profileId") String profileId,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone") String phone) {

        String error = controller.createAccount(username, password, profileId, fullName, email, phone);

        if (error == null) {
            String msg = URLEncoder.encode("Success: User account '" + username + "' has been created!", StandardCharsets.UTF_8);
            return "redirect:/ManageAccount.html?toast=" + msg;
        }

        String msg = URLEncoder.encode("Error: " + error, StandardCharsets.UTF_8);
        return "redirect:/ManageAccount.html?toast=" + msg;
    }
}
