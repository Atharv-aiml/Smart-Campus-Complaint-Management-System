package repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface defining standard CRUD operations.
 * Demonstrates Java Generics and Abstraction.
 *
 * @param <T>  The entity type extending Identifiable
 * @param <ID> The entity's unique identifier type
 */
public interface Repository<T extends Identifiable<ID>, ID extends Serializable> {

    /**
     * Saves or updates an entity.
     *
     * @param entity The entity to persist
     * @return The persisted entity
     */
    T save(T entity);

    /**
     * Finds an entity by its unique ID.
     *
     * @param id The entity ID
     * @return Optional containing the entity if found, or empty
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all entities in the repository.
     *
     * @return An unmodifiable or fresh list of all entities
     */
    List<T> findAll();

    /**
     * Deletes an entity by its ID.
     *
     * @param id The ID of the entity to delete
     * @return true if deleted, false if not found
     */
    boolean deleteById(ID id);

    /**
     * Checks if an entity exists by ID.
     *
     * @param id The entity ID
     * @return true if exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Returns total count of entities in repository.
     */
    long count();

    /**
     * Forces flushing cache or committing changes to storage.
     */
    void flush();
}
