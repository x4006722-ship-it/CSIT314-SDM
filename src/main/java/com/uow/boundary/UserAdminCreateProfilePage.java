package com.uow.boundary;

import com.uow.control.UserAdminCreateProfileController;
import java.util.Scanner;

public class UserAdminCreateProfilePage {
    private UserAdminCreateProfileController controller;

    public UserAdminCreateProfilePage(UserAdminCreateProfileController controller) {
        this.controller = controller;
    }

    public void displayForm() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== [Admin Page] Create New User Profile ===");
        
        System.out.print("1. Enter new Role Name (e.g., FundRaiser): ");
        String roleName = scanner.nextLine();
        
        System.out.print("2. Enter Status (Active/Suspended): ");
        String status = scanner.nextLine();

        System.out.println("\n[Clicking 'Submit' button...]");
        
        boolean isSuccess = controller.createProfile(roleName, status);
        
        if (isSuccess) {
            System.out.println("-> Returning to Admin Dashboard...");
        } else {
            System.out.println("-> Please try a different name.");
        }
    }
}