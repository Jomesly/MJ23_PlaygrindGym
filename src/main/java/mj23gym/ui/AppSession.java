package mj23gym.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class AppSession {
    private static final DateTimeFormatter LOGIN_TIME_FORMAT =
        DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a");

    private static User currentUser;

    private AppSession() {
    }

    static void login(User user) {
        currentUser = user.withLastLogin(LocalDateTime.now());
    }

    static void logout() {
        currentUser = null;
    }

    static User currentUser() {
        if (currentUser == null) {
            return User.guest();
        }
        return currentUser;
    }

    static final class User {
        private final String username;
        private final String firstName;
        private final String lastName;
        private final String displayName;
        private final String role;
        private final String position;
        private final String email;
        private final String phone;
        private final LocalDateTime lastLogin;

        private User(
            String username,
            String firstName,
            String lastName,
            String displayName,
            String role,
            String position,
            String email,
            String phone,
            LocalDateTime lastLogin
        ) {
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.displayName = displayName;
            this.role = role;
            this.position = position;
            this.email = email;
            this.phone = phone;
            this.lastLogin = lastLogin;
        }

        static User admin(String username) {
            return new User(
                username,
                "Admin",
                "User",
                "Administrator",
                "Admin",
                "System Administrator",
                "admin@mj23gym.com",
                "09171234567",
                null
            );
        }

        static User guest() {
            return new User(
                "guest",
                "Guest",
                "User",
                "Guest User",
                "Guest",
                "Guest",
                "",
                "",
                null
            );
        }

        static User fromUserRecord(mj23gym.dao.UserDAO.UserRecord ur) {
            String displayName = ur.fullName() != null && !ur.fullName().isEmpty() 
                ? ur.fullName() 
                : ur.username();
            String position = ur.role() != null 
                ? (ur.role().equals("admin") ? "Administrator" : ur.role())
                : "Staff";
            LocalDateTime lastLogin = ur.lastLogin() != null 
                ? ur.lastLogin().toLocalDateTime()
                : null;
            
            return new User(
                ur.username(),
                ur.fullName() != null ? ur.fullName().split(" ")[0] : ur.username(),
                ur.fullName() != null && ur.fullName().contains(" ") 
                    ? ur.fullName().substring(ur.fullName().indexOf(" ") + 1)
                    : "",
                displayName,
                ur.role(),
                position,
                ur.email() != null ? ur.email() : "",
                ur.phone() != null ? ur.phone() : "",
                lastLogin
            );
        }

        private User withLastLogin(LocalDateTime loginTime) {
            return new User(
                username,
                firstName,
                lastName,
                displayName,
                role,
                position,
                email,
                phone,
                loginTime
            );
        }

        String username() {
            return username;
        }

        String firstName() {
            return firstName;
        }

        String lastName() {
            return lastName;
        }

        String displayName() {
            return displayName;
        }

        String role() {
            return role;
        }

        String position() {
            return position;
        }

        String email() {
            return email;
        }

        String phone() {
            return phone;
        }

        String initial() {
            if (displayName == null || displayName.isBlank()) {
                return "?";
            }
            return displayName.substring(0, 1).toUpperCase();
        }

        String lastLoginText() {
            if (lastLogin == null) {
                return "Last login: Current session";
            }
            return "Last login: " + lastLogin.format(LOGIN_TIME_FORMAT);
        }
    }
}
