package repository;

import model.Student;

import java.util.Optional;

/**
 * Repository for managing Student entities persisted to students.dat.
 */
public class StudentRepository extends FileRepository<Student, String> {

    public StudentRepository(String filePath) {
        super(filePath);
    }

    public Optional<Student> findByUsername(String username) {
        if (username == null) return Optional.empty();
        String normalized = username.trim().toLowerCase();
        return findAll().stream()
                .filter(s -> s.getUsername().equalsIgnoreCase(normalized))
                .findFirst();
    }

    public Optional<Student> findByRegistrationNumber(String regNo) {
        if (regNo == null) return Optional.empty();
        String normalized = regNo.trim().toUpperCase();
        return findAll().stream()
                .filter(s -> s.getRegistrationNumber().equalsIgnoreCase(normalized))
                .findFirst();
    }

    public Optional<Student> findByEmail(String email) {
        if (email == null) return Optional.empty();
        String normalized = email.trim().toLowerCase();
        return findAll().stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(normalized))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public boolean existsByRegistrationNumber(String regNo) {
        return findByRegistrationNumber(regNo).isPresent();
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}
