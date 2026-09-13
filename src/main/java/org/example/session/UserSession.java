package org.example.session;

/**
 * UserSession - Luu thong tin nguoi dung sau khi dangNhap.
 * Dung Singleton de truy cap tu bat ky dau trong ung dung.
 */
public class UserSession {

    private static volatile UserSession instance;

    private String username;
    private String role;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public void clear() {
        instance = null;
    }
}
