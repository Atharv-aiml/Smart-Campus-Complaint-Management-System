package model;

import java.io.Serial;
import java.util.Objects;

/**
 * Concrete Student entity representing a registered college student.
 * Demonstrates:
 * - Inheritance (extends User)
 * - Encapsulation (student-specific private fields and validation)
 * - Method Overriding (implements getRoleTitle, getSummary, and toString)
 * - Method Overloading (multiple constructors for flexible instantiation)
 */
public class Student extends User {

    @Serial
    private static final long serialVersionUID = 1L;

    private String registrationNumber;
    private String department;
    private String hostelBlock;
    private String roomNumber;

    /**
     * Default constructor for serialization support.
     */
    public Student() {
        super();
        setRole(UserRole.STUDENT);
    }

    /**
     * Minimal constructor demonstrating constructor overloading.
     */
    public Student(String userId, String username, String password, String fullName,
                   String email, String phone, String registrationNumber) {
        this(userId, username, password, fullName, email, phone, registrationNumber,
                "General", "Day Scholar", "N/A");
    }

    /**
     * Full parameterized constructor.
     */
    public Student(String userId, String username, String password, String fullName,
                   String email, String phone, String registrationNumber,
                   String department, String hostelBlock, String roomNumber) {
        super(userId, username, password, fullName, email, phone, UserRole.STUDENT);
        this.registrationNumber = Objects.requireNonNull(registrationNumber, "Reg number cannot be null").trim().toUpperCase();
        this.department = department != null ? department.trim() : "SCOPE";
        this.hostelBlock = hostelBlock != null ? hostelBlock.trim() : "Day Scholar";
        this.roomNumber = roomNumber != null ? roomNumber.trim() : "N/A";
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getHostelBlock() {
        return hostelBlock;
    }

    public void setHostelBlock(String hostelBlock) {
        this.hostelBlock = hostelBlock;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public String getRoleTitle() {
        return "Student (" + registrationNumber + ")";
    }

    @Override
    public String getSummary() {
        return String.format("Student: %s | Reg No: %s | Dept: %s | Residence: %s Room %s",
                getFullName(), registrationNumber, department, hostelBlock, roomNumber);
    }

    @Override
    public String toString() {
        return String.format("Student[regNo=%s, name=%s, dept=%s, hostel=%s, room=%s]",
                registrationNumber, getFullName(), department, hostelBlock, roomNumber);
    }
}
