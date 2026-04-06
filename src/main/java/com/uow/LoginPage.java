package com.uow;

public class LoginPage {
    private String username;
    private String password;
    private LoginControl controller;

    public void setController(LoginControl controller) {
        this.controller = controller;
    }

    public void displayPage() {
        System.out.println("=== Welcome to Login Page ===");
    }

    public void inputData(String inputUser, String inputPass) {
        this.username = inputUser;
        this.password = inputPass;
    }

    public void onLoginClick() {
        controller.processLogin(username, password);
    }

    public void showSuccessMessage() {
        System.out.println("Login successful.");
    }

    public void showErrorMessage(String msg) {
        System.out.println("Error: " + msg);
    }
}