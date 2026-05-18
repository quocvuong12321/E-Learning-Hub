package com.king.lms.e_learning_hub.enums;

public enum AuthProvider {
    LOCAL("local"),        // ✅ Thêm value
    GOOGLE("google"),      // ✅ Thêm value
    FACEBOOK("facebook");  // ✅ Thêm value

    private final String value;

    AuthProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AuthProvider fromValue(String value) {
        for (AuthProvider provider : AuthProvider.values()) {
            if (provider.value.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Invalid auth provider: " + value);
    }
}