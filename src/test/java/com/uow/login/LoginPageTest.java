package com.uow.login;

import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class LoginPageTest {

    private LoginPage loginPage;

    @Before
    public void setUp() {
        loginPage = new LoginPage();
    }

    @Test
    public void testUserLogin_EmptyUsername_ReturnsErrorRedirect() {
        // Setup: Provide an empty username
        Map<String, String> formData = new HashMap<>();
        formData.put("username", "");
        formData.put("password", "password123");

        // Execution
        String result = (String) loginPage.userLogin(formData);
        
        // Verification: It should be intercepted before hitting the database
        assertTrue("Should redirect to login page with an error", result.startsWith("redirect:/LoginPage.html?error="));
        assertTrue("Error message should mention empty fields", result.contains("Empty+field"));
    }

    @Test
    public void testUserLogin_InvalidUsernameRegex_ReturnsErrorRedirect() {
        // Setup: Provide a username with special characters (fails the ^[A-Za-z0-9_]+$ regex)
        Map<String, String> formData = new HashMap<>();
        formData.put("username", "hacker@name!"); 
        formData.put("password", "password123");

        // Execution
        String result = (String) loginPage.userLogin(formData);
        
        // Verification
        assertTrue("Should redirect to login page with an error", result.startsWith("redirect:/LoginPage.html?error="));
        assertTrue("Error message should mention invalid format", result.contains("Invalid+username+format"));
    }

    @Test
    public void testRedirectPage_RoutesProperly() {
        // Test all the role-based routing paths defined in your code
        assertEquals("redirect:/ManageProfile.html", loginPage.redirectPage("User Admin"));
        
        assertEquals("redirect:/FundRaiserPage.html", loginPage.redirectPage("Fund Raiser"));
        
        assertEquals("redirect:/DoneePage.html", loginPage.redirectPage("Donee"));
        
        assertEquals("redirect:/PlatformPage.html", loginPage.redirectPage("Platform Management"));
        
        // Test the fallback/default route for unknown roles
        assertEquals("redirect:/LoginPage.html", loginPage.redirectPage("Unknown Role")); 
    }
}