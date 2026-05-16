package com.uow.userprofile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class CreateUserProfileControllerTest {

    private CreateUserProfileController controller;
    private final String TEST_PREFIX = "Pragm_Profile_";

    @Before
    public void setUp() {
        controller = new CreateUserProfileController();
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
    public void test_Creation_succeeds_with_valid_data() {
        UserProfile newProfile = new UserProfile(TEST_PREFIX + System.currentTimeMillis(), "Active");
        assertTrue("Creation should succeed", controller.createProfile(newProfile));
    }

    @Test
    public void test_Creation_fails_when_role_name_is_empty() {
        UserProfile invalidProfile = new UserProfile("", "Active");
        assertFalse("Should return false when role name is empty", controller.createProfile(invalidProfile));
    }

    @Test
    public void test_Creation_fails_when_status_is_empty() {
        UserProfile invalidProfile = new UserProfile(TEST_PREFIX + "Valid", "");
        assertFalse("Should return false when status is empty", controller.createProfile(invalidProfile));
    }

    @Test
    public void test_Creation_throws_exception_when_duplicate_role_exists() {
        String duplicateRole = TEST_PREFIX + "Duplicate";
        UserProfile firstProfile = new UserProfile(duplicateRole, "Active");
        
        // First creation succeeds
        controller.createProfile(firstProfile);

        // Second creation should trigger the duplicate check
        UserProfile duplicateProfile = new UserProfile(duplicateRole, "Active");
        try {
            controller.createProfile(duplicateProfile);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("duplicate", e.getMessage());
        }
    }
}