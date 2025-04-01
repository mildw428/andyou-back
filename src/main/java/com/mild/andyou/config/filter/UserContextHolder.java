package com.mild.andyou.config.filter;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

    public static void setUserContext(UserContext context) {
        userContext.set(context);
    }

    public static UserContext getUserContext() {
        return userContext.get();
    }

    public static Long userId() {
        if(userContext.get() == null) {
            return null;
        }
        return userContext.get().getUserId();
    }

    public static void clear() {
        userContext.remove();
    }
}
