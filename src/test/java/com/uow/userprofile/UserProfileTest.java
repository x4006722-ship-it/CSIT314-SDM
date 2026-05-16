package com.uow.userprofile;

import org.junit.After;
import org.junit.Test;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class UserProfileTest {

    private final String TEST_PREFIX = "Pragm_DAO_";

    @After
    public void tearDown() {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM user_profile WHERE role LIKE ?")) {
            ps.setString(1, TEST_PREFIX + "%");
            ps.executeUpdate();
        } catch (Exception e) {}
    }

    @Test
    public void test_UserProfile_DAO_lifecycle_succeeds() {
        String uniqueRole = TEST_PREFIX + System.currentTimeMillis();
        UserProfile dao = new UserProfile(uniqueRole, "Active");

        // 1. Create
        assertTrue("Creation should succeed", dao.save());

        // 2. Search
        List<UserProfile> searchResults = UserProfile.findAll(uniqueRole, "Active");
        assertFalse("Search should find the newly created profile", searchResults.isEmpty());

        String generatedId = searchResults.get(0).getProfileId();

        // 3. View
        UserProfile viewedProfile = UserProfile.findByID(generatedId);
        assertNotNull("View should return the profile details", viewedProfile);
        assertEquals(uniqueRole, viewedProfile.getRoleName());

        // 4. Duplicate Check
        assertTrue("Duplicate check should return true for exact match", UserProfile.isDuplicateRole(uniqueRole, null));

        // 5. Update Role Name
        assertTrue("Role name update should succeed", viewedProfile.updateRoleName(uniqueRole + "_Updated"));

        // 6. Update Status
        assertTrue("Status update should succeed", viewedProfile.updateStatus("Suspended"));
    }
}
