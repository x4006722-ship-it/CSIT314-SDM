package com.uow.entity;

import jakarta.servlet.http.HttpSession;

public class UserSession {

    public void invalidateSession(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}