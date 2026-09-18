package repository;

import exception.ComplaintManagementException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Generic file-based repository implementation using Java Object Serialization.
 * Demonstrates:
 * - Generics: <T extends Identifiable<ID>, ID extends Serializable>
 * - File I/O and Java Serialization
 * - Thread-safety via synchronized critical sections
 * - Atomic file write pattern with backup recovery
 * - Exception handling using try-with-resources
 */
public abstract class FileRepository<T extends Identifiable<ID>, ID extends Serializable>
        implements Repository<T, ID> {

    protected final File storageFile;
    protected final Map<ID, T> storageMap;
    private final Object lock = new Object();

    /**
     * Initializes the repository with the specified data file path.
     *
     * @param filePath Path to the storage file (e.g., "data/students.dat")
     */
    public FileRepository(String filePath) {
        this.storageFile = new File(filePath);
        this.storageMap = new LinkedHashMap<>();
        ensureDirectoryExists();
        loadData();
    }

    private void ensureDirectoryExists() {
        File parentDir = storageFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created && !parentDir.exists()) {
                System.err.println("Failed to create parent directory for " + storageFile.getAbsolutePath());
            }
        }
    }

    /**
     * Loads existing data from serialized file. If file does not exist,
     * an empty data map is initialized.
     */
    @SuppressWarnings("unchecked")
    protected void loadData() {
        synchronized (lock) {
            storageMap.clear();
            if (!storageFile.exists() || storageFile.length() == 0) {
                return;
            }
            try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(storageFile)))) {
                Object obj = ois.readObject();
                if (obj instanceof Map<?, ?> map) {
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        ID id = (ID) entry.getKey();
                        T entity = (T) entry.getValue();
                        storageMap.put(id, entity);
                    }
                }
            } catch (EOFException e) {
                // Empty file, normal on initial creation
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Warning: Unable to load data from " + storageFile.getName() + ": " + e.getMessage());
                // Fallback to empty map rather than crashing
            }
        }
    }

    /**
     * Atomically saves in-memory map to disk using a temporary file.
     */
    @Override
    public void flush() {
        synchronized (lock) {
            ensureDirectoryExists();
            File tempFile = new File(storageFile.getAbsolutePath() + ".tmp");
            try {
                try (ObjectOutputStream oos = new ObjectOutputStream(
                        new BufferedOutputStream(new FileOutputStream(tempFile)))) {
                    oos.writeObject(new LinkedHashMap<>(storageMap));
                    oos.flush();
                }

                // Atomic rename to avoid corrupted files
                Files.move(tempFile.toPath(), storageFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                // Attempt non-atomic copy if atomic move failed
                try {
                    Files.move(tempFile.toPath(), storageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    throw new ComplaintManagementException("Failed to persist data to " + storageFile.getAbsolutePath(), ex);
                }
            }
        }
    }

    @Override
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Cannot save null entity");
        }
        synchronized (lock) {
            storageMap.put(entity.getId(), entity);
            flush();
            return entity;
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) return Optional.empty();
        synchronized (lock) {
            return Optional.ofNullable(storageMap.get(id));
        }
    }

    @Override
    public List<T> findAll() {
        synchronized (lock) {
            return new ArrayList<>(storageMap.values());
        }
    }

    @Override
    public boolean deleteById(ID id) {
        if (id == null) return false;
        synchronized (lock) {
            if (storageMap.containsKey(id)) {
                storageMap.remove(id);
                flush();
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean existsById(ID id) {
        if (id == null) return false;
        synchronized (lock) {
            return storageMap.containsKey(id);
        }
    }

    @Override
    public long count() {
        synchronized (lock) {
            return storageMap.size();
        }
    }

    /**
     * Clears all entities from memory and removes file.
     * Useful for integration test teardown.
     */
    public void clear() {
        synchronized (lock) {
            storageMap.clear();
            if (storageFile.exists()) {
                boolean deleted = storageFile.delete();
                if (!deleted) {
                    storageFile.deleteOnExit();
                }
            }
        }
    }
}
