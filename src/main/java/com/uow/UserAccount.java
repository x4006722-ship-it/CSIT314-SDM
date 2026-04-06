package com.uow;

public class UserAccount {
    private String username;
    private String password;
    private String a_status;
    private int profile_id;

    public UserAccount(String username, String password, String a_status) {
        this.username = username;
        this.password = password;
        this.a_status = a_status;
    }

    public boolean verifyCredentials(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername) && this.password.equals(inputPassword);
    }

    public boolean checkAccountStatus() {
        return "Normal".equalsIgnoreCase(this.a_status);
    }
}