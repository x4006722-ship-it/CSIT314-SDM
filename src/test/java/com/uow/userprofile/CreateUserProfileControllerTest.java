package com.uow.userprofile;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CreateUserProfileControllerTest {

    private CreateUserProfileController createController;

    @Before
    public void setUp() {
        createController = new CreateUserProfileController();
    }

    @Test
    public void testCreateProfile_NullProfile_ReturnsFalse() {
        boolean result = createController.createProfile(null);
        assertFalse("Result should be false when the profile object is null", result);
    }

    @Test
    public void testCreateProfile_NullRoleName_ReturnsFalse() {
        UserProfile invalidProfile = new UserProfile(null, "Active");
        boolean result = createController.createProfile(invalidProfile);
        assertFalse("Result should be false when the role name is null", result);
    }

    @Test
    public void testCreateProfile_EmptyRoleName_ReturnsFalse() {
        UserProfile invalidProfile = new UserProfile("   ", "Active"); // Blank spaces
        boolean result = createController.createProfile(invalidProfile);
        assertFalse("Result should be false when the role name is empty or blank", result);
    }
    
    // Note: To test the "duplicate" exception without Mockito, you would need 
    // to manually insert a profile into your database, try to create it again 
    // to trigger the exception, and then manually delete it from the database.
}