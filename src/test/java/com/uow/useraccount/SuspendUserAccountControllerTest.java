package com.uow.useraccount;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SuspendUserAccountControllerTest {

    private SuspendUserAccountController suspendController;

    @Before
    public void setUp() {
        suspendController = new SuspendUserAccountController();
    }

    @Test
    public void testSuspendAccount_TargetIsSelf_ReturnsFalse() {
        // Rule: You cannot suspend your own account
        int targetUserId = 10;
        int currentUserId = 10; // Same ID

        boolean result = suspendController.suspendAccount(targetUserId, currentUserId);
        assertFalse("Result should be false when trying to suspend yourself", result);
    }

    @Test
    public void testSuspendAccount_TargetDoesNotExist_ReturnsFalse() {
        // Rule: Target must exist in the database
        int targetUserId = -999;
        int currentUserId = 1;

        boolean result = suspendController.suspendAccount(targetUserId, currentUserId);
        assertFalse("Result should be false when the target user does not exist", result);
    }
    
    /* Note: To test the 'Cannot suspend User Admin' rule, you would need to 
       ensure a User Admin exists in your database with a specific ID, 
       then attempt to suspend it.
    */
}