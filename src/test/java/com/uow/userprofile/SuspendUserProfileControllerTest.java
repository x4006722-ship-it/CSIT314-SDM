package com.uow.userprofile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class SuspendUserProfileControllerTest {

    private SuspendUserProfileController suspendController;
    private final String TEST_PREFIX = "Pragm_Profile_";
    private String existingProfileId;

    @Before
    public void setUp() {
        suspendController = new SuspendUserProfileController();
        CreateUserProfileController createController = new CreateUserProfileController();

        String roleName = TEST_PREFIX + "ToSuspend_" + System.currentTimeMillis();
        createController.createProfile(new UserProfile(roleName, "Active"));

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
    public void test_Suspend_and_reactivate_lifecycle_succeeds() {
        // 1. Suspend the profile
        assertTrue("Suspension should succeed", suspendController.suspendProfile(existingProfileId));

        // 2. Reactivate the profile
        assertTrue("Reactivation should succeed", suspendController.reactivateProfile(existingProfileId));
    }

    @Test
    public void test_Suspend_fails_for_non_existent_id() {
        assertFalse("Should return false for non-existent profile ID", suspendController.suspendProfile("-999"));
    }
}
