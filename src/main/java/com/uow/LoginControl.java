package com.uow;
public class LoginControl {
    // Controller 需要认识界面(UI)和数据库(DB)，才能在中间当传话筒
    private LoginPage ui;
    private UserAccount db;

    // 绑定具体的界面和数据
    public LoginControl(LoginPage ui, UserAccount db) {
        this.ui = ui;
        this.db = db;
    }

    // 对应时序图 Step 2: 接收界面的登录请求
    public void processLogin(String inputUser, String inputPass) {
        
        // 时序图第一层 alt：验证账号密码
        boolean isValid = db.verifyCredentials(inputUser, inputPass);
        
        if (isValid) {
            // 时序图第二层 alt：账号密码对了，继续检查状态
            boolean isNormal = db.checkAccountStatus();
            
            if (isNormal) {
                // 状态正常 -> 呼叫界面显示成功
                ui.showSuccessMessage();
            } else {
                // 状态异常 (Suspend) -> 呼叫界面显示封禁报错
                ui.showErrorMessage("Account suspended");
            }
        } else {
            // 凭据错误 -> 呼叫界面显示密码错误报错
            ui.showErrorMessage("Invalid credentials");
        }
    }

    public boolean validateSession() {
        return true; // 简单的会话验证占位符
    }
}
