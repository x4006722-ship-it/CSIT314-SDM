package com.uow.useraccount;

import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class UpdateUserAccountControllerTest {

    private UpdateUserAccountController updateController;

    @Before
    public void setUp() {
        updateController = new UpdateUserAccountController();
    }

    @Test
    public void testUpdateAccount_WithInvalidUserId_ReturnsFalse() {
        // Rule: userId must be > 0
        Map<String, Object> data = new HashMap<>();
        data.put("userId", 0);

        boolean result = updateController.updateAccount(data);
        assertFalse("Result should be false when userId is 0 or negative", result);
    }

    @Test
    public void testUpdateAccount_NonExistentUser_ReturnsFalse() {
        // Rule: User must exist in database to be updated
        Map<String, Object> data = new HashMap<>();
        data.put("userId", 99999); 

        boolean result = updateController.updateAccount(data);
        assertFalse("Result should be false when attempting to update a non-existent user", result);
    }
}