package com.casualapp.android;

import com.casualapp.android.model.User;

public class UserSession {
    private static User currentUser;
    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }
    public static boolean isLoggedIn() { return currentUser != null; }
    public static void clear() { currentUser = null; }
}