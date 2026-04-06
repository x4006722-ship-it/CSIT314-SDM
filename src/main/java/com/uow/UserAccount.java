public class LoginPage {
    // 界面上的输入框
    private String username;
    private String password;
    
    // 界面需要认识 Controller，才能把数据递给大脑
    private LoginControl controller;

    public void setController(LoginControl controller) {
        this.controller = controller;
    }

    public void displayPage() {
        System.out.println("=== 欢迎来到系统登录页面 ===");
    }

    // 对应时序图 Step 1: 模拟用户在输入框打字
    public void inputData(String inputUser, String inputPass) {
        this.username = inputUser;
        this.password = inputPass;
    }

    // 对应时序图 Step 1: 模拟用户点击 Login 按钮
    public void onLoginClick() {
        // 把输入框里的字，交给 Controller 去处理
        controller.processLogin(username, password);
    }

    // 对应时序图中的成功反馈
    public void showSuccessMessage() {
        System.out.println("提示: Login successful. 正在跳转...");
    }

    // 对应时序图中的报错反馈
    public void showErrorMessage(String msg) {
        System.out.println("错误提示: " + msg);
    }
}
