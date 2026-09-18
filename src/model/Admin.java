package model;

import java.io.Serial;
import java.util.Objects;

/**
 * Concrete Admin entity representing campus authorities and department managers.
 * Demonstrates:
 * - Inheritance (extends User)
 * - Encapsulation (admin specific fields and validations)
 * - Method Overriding (polymorphic role titles and summaries)
 */
public class Admin extends User {

    @Serial
    private static final long serialVersionUID = 1L;

    private String adminId;
    private String department;
    private String designation;

    /**
     * Default constructor for serialization support.
     */
    public Admin() {
        super();
        setRole(UserRole.ADMIN);
    }

    /**
     * Parameterized constructor.
     */
    public Admin(String userId, String username, String password, String fullName,
                 String email, String phone, String adminId,
                 String department, String designation) {
        super(userId, username, password, fullName, email, phone, UserRole.ADMIN);
        this.adminId = Objects.requireNonNull(adminId, "Admin ID cannot be null").trim();
        this.department = department != null ? department.trim() : "Campus Administration";
        this.designation = designation != null ? designation.trim() : "Administrator";
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String getRoleTitle() {
        return designation + " (" + department + ")";
    }

    @Override
    public String getSummary() {
        return String.format("Admin: %s [%s] | Dept: %s | Role: %s",
                getFullName(), adminId, department, designation);
    }

    @Override
    public String toString() {
        return String.format("Admin[adminId=%s, name=%s, dept=%s, designation=%s]",
                adminId, getFullName(), department, designation);
    }
}
