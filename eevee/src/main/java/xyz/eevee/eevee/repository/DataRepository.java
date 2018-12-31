package xyz.eevee.eevee.repository;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class DataRepository {
    public static DataRepository getInstance() {
        throw new UnsupportedOperationException();
    }

    /**
     * Swaps out an existing data repository for a new one.
     *
     * @return The newly swapped in repository.
     */
    public static DataRepository reload() {
        throw new UnsupportedOperationException();
    }
}
