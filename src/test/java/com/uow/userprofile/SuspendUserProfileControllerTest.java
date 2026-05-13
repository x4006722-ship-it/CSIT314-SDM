package com.uow.userprofile;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SuspendUserProfileControllerTest {

    private SuspendUserProfileController suspendController;

    @Before
    public void setUp() {
        suspendController = new SuspendUserProfileController();
    }

    @Test
    public void testSuspendProfile_NonExistentID_ReturnsFalse() {
        // Provide an ID that definitely does not exist in the DB
        boolean result = suspendController.suspendProfile("fake_id_999");
        assertFalse("Result should be false when trying to suspend a non-existent profile", result);
    }

    @Test
    public void testReactivateProfile_NonExistentID_ReturnsFalse() {
        // Provide an ID that definitely does not exist in the DB
        boolean result = suspendController.reactivateProfile("fake_id_999");
        assertFalse("Result should be false when trying to reactivate a non-existent profile", result);
    }
}
