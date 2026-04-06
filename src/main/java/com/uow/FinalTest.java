package com.uow;

import com.uow.entity.UserAccount;

public class FinalTest {
    public static void main(String[] args) {
        String testUser = "admin_cheng"; // 数据库里已有的用户名
        String testPass = "pass123";     // 数据库里已有的密码

        System.out.println("--- 🔍 正在从云端查询用户: " + testUser + " ---");

        // 1. 调用你刚写的 fetchFromDB (对应时序图第 6 步)
        UserAccount account = UserAccount.fetchFromDB(testUser);

        if (account != null) {
            System.out.println("✅ 找到用户了！");

            // 2. 验证密码 (对应时序图第 7 步)
            if (account.verifyCredentials(testPass)) {
                System.out.println("✅ 密码验证通过！");

                // 3. 检查账号状态 (对应时序图第 8/9 步)
                if (account.checkAccountStatus()) {
                    System.out.println("🎊 [Login Successful");
                } else {
                    System.out.println("❌ [Account Suspended] 账号已被封禁！");
                }
            } else {
                System.out.println("❌ [Invalid Password] 密码错误！");
            }
        } else {
            System.out.println("❌ [User Not Found] 数据库里没这个人！");
        }
    }
}