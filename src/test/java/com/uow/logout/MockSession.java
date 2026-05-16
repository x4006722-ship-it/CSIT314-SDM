package com.uow.logout;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * 这是一个为了 TDD 能够运行而实现的极简 Mock 类。
 * 它只实现了核心的 invalidate() 逻辑。
 */
public class MockSession implements HttpSession {
    private boolean invalidated = false;

    public boolean isInvalidated() { return invalidated; }

    @Override
    public void invalidate() {
        this.invalidated = true;
    }

    // 以下为接口要求的其他方法，留空即可，测试用不到
    @Override public long getCreationTime() { return 0; }
    @Override public String getId() { return null; }
    @Override public long getLastAccessedTime() { return 0; }
    @Override public ServletContext getServletContext() { return null; }
    @Override public void setMaxInactiveInterval(int interval) {}
    @Override public int getMaxInactiveInterval() { return 0; }
    @Override public Object getAttribute(String name) { return null; }
    @Override public Enumeration<String> getAttributeNames() { return null; }
    @Override public void setAttribute(String name, Object value) {}
    @Override public void removeAttribute(String name) {}
    @Override public boolean isNew() { return false; }
}