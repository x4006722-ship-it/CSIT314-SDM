package com.uow.control;

import com.uow.boundary.LoginPage;
import com.uow.entity.UserAccount;

public class LoginControl {
    private LoginPage ui;
    private UserAccount db;

    public LoginControl(LoginPage ui, UserAccount db) {
        this.ui = ui;
        this.db = db;
    }

    public void processLogin(String inputUser, String inputPass) {
        
    boolean isValid = db.verifyCredentials(inputPass);
        
        if (isValid) {
            boolean isNormal = db.checkAccountStatus();
            
            if (isNormal) {
                ui.showSuccessMessage();
            } else {
                ui.showErrorMessage("Account suspended");
            }
        } else {
            ui.showErrorMessage("Invalid credentials");
        }
    }

    public boolean validateSession() {
        return true;
    }
}
