package com.uow.useraccount;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class UpdateUserAccountControllerTest {

    private UpdateUserAccountController updateController;
    private UserAccount dao;
    private final String TEST_PREFIX = "PragmTest_";
    private int existingUserId;

    @Before
    public void setUp() {
        updateController = new UpdateUserAccountController();
        dao = new UserAccount();

        // Dynamically create a real user to test the update logic
        String uniqueName = TEST_PREFIX + "Update_" + System.currentTimeMillis();
        Map<String, Object> newAcc = new HashMap<>();
        newAcc.put("username", uniqueName);
        newAcc.put("password", "123456");
        newAcc.put("fullName", "To Update");
        newAcc.put("email", uniqueName + "@test.com");
        newAcc.put("phoneNumber", "88888888");
        newAcc.put("accountStatus", "Active");
        newAcc.put("profileId", 3);
        dao.saveCreateAccount(newAcc);

        // Fetch its ID
        Map<String, Object> searchParam = new HashMap<>();
        searchParam.put("username", uniqueName);
        List<Map<String, Object>> results = (List<Map<String, Object>>) dao.getSearchAccount(searchParam);
        existingUserId = (Integer) results.get(0).get("userId");
    }

    @After
    public void tearDown() {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM user_account WHERE username LIKE ?")) {
            ps.setString(1, TEST_PREFIX + "%");
            ps.executeUpdate();
        } catch (Exception e) {}
    }

    @Test
    public void test_Update_succeeds_with_valid_data() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("userId", existingUserId);
        updateData.put("fullName", "Updated Name");
        
        assertTrue("Should return true on successful update", updateController.updateAccount(updateData));
    }

    @Test
    public void test_Update_fails_when_input_is_not_a_map() {
        assertFalse(updateController.updateAccount("Invalid String"));
    }

    @Test
    public void test_Update_fails_when_input_is_null() {
        assertFalse(updateController.updateAccount(null));
    }

    @Test
    public void test_Update_fails_when_user_id_is_missing() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("fullName", "Updated Name");
        
        assertFalse(updateController.updateAccount(updateData));
    }

    @Test
    public void test_Update_fails_when_user_id_is_invalid() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("userId", -999); // Invalid ID
        
        assertFalse("Should return false for invalid ID", updateController.updateAccount(updateData));
    }

    @Test
    public void test_Update_fails_when_user_does_not_exist() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("userId", 999999); // Non-existent ID
        updateData.put("fullName", "Updated Name");
        
        assertFalse("Should return false if user not found", updateController.updateAccount(updateData));
    }
}