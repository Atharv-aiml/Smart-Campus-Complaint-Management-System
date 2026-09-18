package model;

import repository.Identifiable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class representing a user within the campus system.
 * Demonstrates:
 * - Abstraction (cannot be instantiated directly)
 * - Encapsulation (private fields with controlled accessors)
 * - Polymorphism (abstract template methods implemented by subclasses)
 */
public abstract class User implements Identifiable<String>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private LocalDateTime createdAt;

    /**
     * Default constructor for serialization support.
     */
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Parameterized constructor demonstrating constructor initialization with validation.
     */
    public User(String userId, String username, String password, String fullName,
                String email, String phone, UserRole role) {
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null").trim();
        this.username = Objects.requireNonNull(username, "Username cannot be null").trim();
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "Full name cannot be null").trim();
        this.email = Objects.requireNonNull(email, "Email cannot be null").trim().toLowerCase();
        this.phone = Objects.requireNonNull(phone, "Phone cannot be null").trim();
        this.role = Objects.requireNonNull(role, "User role cannot be null");
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String getId() {
        return userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Abstract method demonstrating Polymorphism.
     * Each concrete user role provides its own title/designation display.
     */
    public abstract String getRoleTitle();

    /**
     * Abstract method returning a concise human-readable profile summary.
     */
    public abstract String getSummary();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) || Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, userId);
    }

    @Override
    public String toString() {
        return String.format("%s [%s] - %s (%s)", fullName, username, getRoleTitle(), email);
    }
}
