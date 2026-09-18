package repository;

import model.Admin;

import java.util.Optional;

/**
 * Repository for managing Admin entities persisted to admins.dat.
 */
public class AdminRepository extends FileRepository<Admin, String> {

    public AdminRepository(String filePath) {
        super(filePath);
    }

    public Optional<Admin> findByUsername(String username) {
        if (username == null) return Optional.empty();
        String normalized = username.trim().toLowerCase();
        return findAll().stream()
                .filter(a -> a.getUsername().equalsIgnoreCase(normalized))
                .findFirst();
    }

    public Optional<Admin> findByAdminId(String adminId) {
        if (adminId == null) return Optional.empty();
        String normalized = adminId.trim();
        return findAll().stream()
                .filter(a -> a.getAdminId().equalsIgnoreCase(normalized))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }
}
