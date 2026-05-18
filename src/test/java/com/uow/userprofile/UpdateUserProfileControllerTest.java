package com.uow.userprofile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class UpdateUserProfileControllerTest {

    private UpdateUserProfileController updateController;
    private CreateUserProfileController createController;
    private final String TEST_PREFIX = "Pragm_Profile_";
    private String existingProfileId;

    @Before
    public void setUp() {
        updateController = new UpdateUserProfileController();
        createController = new CreateUserProfileController();

        String roleName = TEST_PREFIX + "ToUpdate_" + System.currentTimeMillis();
        createController.createProfile(roleName, "Active");

        List<UserProfile> profiles = UserProfile.findAll(roleName, "all");
        if (!profiles.isEmpty()) {
            existingProfileId = profiles.get(0).getProfileId();
        }
    }

    @After
    public void tearDown() {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM user_profile WHERE role LIKE ?")) {
            ps.setString(1, TEST_PREFIX + "%");
            ps.executeUpdate();
        } catch (Exception e) {}
    }

    @Test
    public void test_Update_succeeds_with_valid_data() {
        assertTrue("Update should succeed", updateController.updateProfile(existingProfileId, TEST_PREFIX + "Updated_Role"));
    }

    @Test
    public void test_Update_fails_when_role_name_is_empty() {
        assertFalse("Should return false when new role name is empty", updateController.updateProfile(existingProfileId, "   "));
    }

    @Test
    public void test_Update_fails_when_profile_id_is_null() {
        assertFalse("Should return false when profile ID is null", updateController.updateProfile(null, "Valid_Role"));
    }

    @Test
    public void test_Update_throws_exception_on_duplicate_role() {
        // Create another profile to cause a conflict
        String conflictRole = TEST_PREFIX + "Conflict_" + System.currentTimeMillis();
        createController.createProfile(conflictRole, "Active");

        try {
            updateController.updateProfile(existingProfileId, conflictRole);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("duplicate", e.getMessage());
        }
    }
}