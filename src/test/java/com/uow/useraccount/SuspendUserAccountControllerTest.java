package com.uow.useraccount;

import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.Map;
import static org.junit.Assert.*;

public class SuspendUserAccountControllerTest {

    private SuspendUserAccountController controller;

    @Before
    public void setUp() throws Exception {
        controller = new SuspendUserAccountController();
        
        UserAccount mock = new UserAccount() {
            @Override
            public Object getViewAccount(int id) {
                if (id == 1) return Map.of("roleName", "System Admin"); // 【修复】：补上当前操作人的 Mock
                if (id == 3) return Map.of("roleName", "User Admin");
                if (id == 2) return Map.of("roleName", "Donee");
                return null; // 其他 ID 视为不存在
            }
            @Override
            public boolean saveSuspendAccount(int t, int c) { return true; }
        };
        
        Field f = SuspendUserAccountController.class.getDeclaredField("userAccount");
        f.setAccessible(true);
        f.set(controller, mock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_Suspend_fails_when_target_equals_current() {
        // 期待抛出：You cannot suspend your own account.
        controller.suspendAccount(1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_Suspend_fails_when_target_is_user_admin() {
        // 期待抛出：Accounts with the User Admin role cannot be suspended.
        controller.suspendAccount(3, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_Suspend_fails_when_user_does_not_exist() {
        // 期待抛出：Target account not found.
        controller.suspendAccount(999, 1);
    }

    @Test
    public void test_Suspend_succeeds_for_normal_user() {
        // 正常操作，期待返回 true
        assertTrue("Suspend should succeed for non-admin user", controller.suspendAccount(2, 1));
    }
}