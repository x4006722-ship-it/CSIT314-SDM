package com.uow.userprofile;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UpdateUserProfileControllerTest {

    private UpdateUserProfileController updateController;

    @Before
    public void setUp() {
        updateController = new UpdateUserProfileController();
    }

    @Test
    public void testUpdateProfile_NullProfileID_ReturnsFalse() {
        boolean result = updateController.updateProfile(null, "NewRole");
        assertFalse("Result should be false when profileID is null", result);
    }

    @Test
    public void testUpdateProfile_NullRoleName_ReturnsFalse() {
        boolean result = updateController.updateProfile("1", null);
        assertFalse("Result should be false when new role name is null", result);
    }

    @Test
    public void testUpdateProfile_EmptyRoleName_ReturnsFalse() {
        boolean result = updateController.updateProfile("1", "   ");
        assertFalse("Result should be false when new role name is empty", result);
    }
}