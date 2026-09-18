package repository;

import java.io.Serializable;

/**
 * Interface representing any entity that possesses a unique identifier.
 * Demonstrates Java Abstraction and Interface usage.
 *
 * @param <ID> The type of the identifier (e.g. String)
 */
public interface Identifiable<ID extends Serializable> extends Serializable {
    ID getId();
}
