package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RemoveFRAControllerTest {

    private RemoveFRAController removeController;

    @Before
    public void setUp() {
        removeController = new RemoveFRAController();
    }

    @Test
    public void testDeleteFRA_NonExistentId_ReturnsFalse() {
        // Setup: Provide a completely random ID that does not exist in the database
        String fakeId = "invalid_id_999999";

        // Execution
        boolean result = removeController.deleteFRA(fakeId);

        // Verification: The database update should fail (0 rows affected) and return false
        assertFalse("Result should be false when attempting to delete a non-existent ID", result);
    }
}
