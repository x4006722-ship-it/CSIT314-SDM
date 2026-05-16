package com.uow.useraccount;

import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class SuspendUserAccountControllerTest {

    private SuspendUserAccountController controller;

    @Before
    public void setUp() throws Exception {
        controller = new SuspendUserAccountController();

        // 优化点：使用匿名内部类和反射来 Mock 掉底层 DAO，只测试控制器的业务逻辑
        UserAccount mockUserAccount = new UserAccount() {
            @Override
            public Object getViewAccount(int userId) {
                Map<String, Object> map = new HashMap<>();
                if (userId == 1) { // 模拟发起操作的当前用户
                    map.put("roleName", "System Admin");
                    return map;
                }
                if (userId == 2) { // 模拟普通目标用户
                    map.put("roleName", "Donee");
                    return map;
                }
                if (userId == 3) { // 模拟受保护的 User Admin
                    map.put("roleName", "User Admin");
                    return map;
                }
                return null; // 返回 null 代表用户不存在
            }

            @Override
            public boolean saveSuspendAccount(int targetUserId, int currentUserId) {
                return true; // 只要能通过所有的 if 校验走到这一步，我们就认为成功
            }
        };

        Field field = SuspendUserAccountController.class.getDeclaredField("userAccount");
        field.setAccessible(true);
        field.set(controller, mockUserAccount);
    }

    @Test
    public void test_Suspend_fails_when_target_equals_current_user() {
        assertFalse("Should return false when target user is the same as current user", 
                    controller.suspendAccount(1, 1));
    }

    @Test
    public void test_Suspend_fails_when_user_does_not_exist() {
        assertFalse("Should return false when target user does not exist", 
                    controller.suspendAccount(999, 1));
    }

    // 【新增】测试：成功拦截试图封禁 User Admin 的违规操作
    @Test
    public void test_Suspend_fails_when_target_is_user_admin() {
        assertFalse("Should return false when attempting to suspend a User Admin", 
                    controller.suspendAccount(3, 1));
    }

    // 【新增】测试：正常用户的封禁流程可以顺利走通
    @Test
    public void test_Suspend_succeeds_for_normal_users() {
        assertTrue("Should return true when target is a normal non-admin user", 
                   controller.suspendAccount(2, 1));
    }
}