package com.uow;
public class UserAccount {
    // 对应 BCE 图中的私有属性 (-号)
    private String username;
    private String password;
    private String a_status; // 状态：Normal 或 Suspend
    private int profile_id;

    // 构造函数：模拟数据库里存好的数据
    public UserAccount(String username, String password, String a_status) {
        this.username = username;
        this.password = password;
        this.a_status = a_status;
    }

    // 对应时序图 Step 3: 验证凭据
    public boolean verifyCredentials(String inputUsername, String inputPassword) {
        // 如果输入的账号密码和数据库存的一样，返回 true
        return this.username.equals(inputUsername) && this.password.equals(inputPassword);
    }

    // 对应时序图 Step 4: 检查账号状态
    public boolean checkAccountStatus() {
        // 如果状态是 Normal，返回 true
        return "Normal".equalsIgnoreCase(this.a_status);
    }
}
