package com.uow.useraccount;

import org.junit.Test;
import static org.junit.Assert.*;

public class SuspendUserAccountControllerTest {

    @Test
    public void test_Suspend_fails_when_target_equals_current_user() {
        SuspendUserAccountController controller = new SuspendUserAccountController();
        
        // A user cannot suspend themselves
        boolean result = controller.suspendAccount(5, 5);
        assertFalse("Should return false when target user is the same as current user", result);
    }

    @Test
    public void test_Suspend_fails_when_user_does_not_exist() {
        SuspendUserAccountController controller = new SuspendUserAccountController();
        
        boolean result = controller.suspendAccount(-999, 1);
        assertFalse("Should return false when target user does not exist", result);
    }
}