package service;

import exception.AuthenticationException;
import exception.DuplicateResourceException;
import exception.ValidationException;
import model.Admin;
import model.Student;
import model.User;
import model.UserRole;
import repository.AdminRepository;
import repository.StudentRepository;
import util.InputValidator;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Service handling authentication, student registration, and user session state.
 * Demonstrates:
 * - Business logic encapsulation
 * - Domain validation
 * - Custom exception handling
 */
public class AuthService {

    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private User currentUser;

    public AuthService(StudentRepository studentRepository, AdminRepository adminRepository) {
        this.studentRepository = Objects.requireNonNull(studentRepository);
        this.adminRepository = Objects.requireNonNull(adminRepository);
    }

    /**
     * Registers a new student account with comprehensive input validation.
     */
    public Student registerStudent(String username, String password, String fullName,
                                   String email, String phone, String registrationNumber,
                                   String department, String hostelBlock, String roomNumber) {

        InputValidator.validateUsername(username);
        InputValidator.validatePassword(password);
        InputValidator.validateNotEmpty(fullName, "Full Name");
        InputValidator.validateEmail(email);
        InputValidator.validatePhone(phone);
        InputValidator.validateRegistrationNumber(registrationNumber);

        String normalizedUser = username.trim().toLowerCase();
        String normalizedReg = registrationNumber.trim().toUpperCase();
        String normalizedEmail = email.trim().toLowerCase();

        if (studentRepository.existsByUsername(normalizedUser) || adminRepository.existsByUsername(normalizedUser)) {
            throw new DuplicateResourceException("Username", normalizedUser);
        }

        if (studentRepository.existsByRegistrationNumber(normalizedReg)) {
            throw new DuplicateResourceException("Registration Number", normalizedReg);
        }

        if (studentRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("Email Address", normalizedEmail);
        }

        String userId = "STU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Student student = new Student(
                userId,
                normalizedUser,
                password,
                fullName.trim(),
                normalizedEmail,
                phone.trim(),
                normalizedReg,
                department != null ? department.trim() : "General",
                hostelBlock != null ? hostelBlock.trim() : "Day Scholar",
                roomNumber != null ? roomNumber.trim() : "N/A"
        );

        return studentRepository.save(student);
    }

    /**
     * Authenticates a user against specified role credentials.
     */
    public User login(String username, String password, UserRole role) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ValidationException("Credentials", "Username and password cannot be empty.");
        }

        String normalizedUser = username.trim().toLowerCase();

        if (role == UserRole.STUDENT) {
            Optional<Student> studentOpt = studentRepository.findByUsername(normalizedUser);
            if (studentOpt.isEmpty() || !studentOpt.get().getPassword().equals(password)) {
                throw new AuthenticationException("Invalid student username or password.");
            }
            this.currentUser = studentOpt.get();
            return this.currentUser;
        } else if (role == UserRole.ADMIN) {
            Optional<Admin> adminOpt = adminRepository.findByUsername(normalizedUser);
            if (adminOpt.isEmpty() || !adminOpt.get().getPassword().equals(password)) {
                throw new AuthenticationException("Invalid administrator username or password.");
            }
            this.currentUser = adminOpt.get();
            return this.currentUser;
        } else {
            throw new AuthenticationException("Unsupported user role: " + role);
        }
    }

    /**
     * Clears current active user session.
     */
    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public boolean isStudent() {
        return currentUser instanceof Student;
    }

    public boolean isAdmin() {
        return currentUser instanceof Admin;
    }
}
