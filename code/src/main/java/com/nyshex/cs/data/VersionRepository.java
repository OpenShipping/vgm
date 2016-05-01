package com.nyshex.cs.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.nyshex.cs.model.Version;

/**
 * Helper methods for checking database version
 */
@ApplicationScoped
public class VersionRepository {

    @Inject
    private EntityManager em;

    /**
     * Check the database version against given version.
     *
     * @param expectedVersion
     *            expected version of database
     * @throws WrongVersionException
     *             If the database version is wrong
     */
    public void checkVersion(final String expectedVersion) {
        final String actualVersion = actualVersion();
        if (!actualVersion.equals(expectedVersion)) {
            throw new WrongVersionException(actualVersion, expectedVersion);
        }
    }

    /**
     * Check the database version against the version the application expects
     *
     * @throws WrongVersionException
     *             If the database version is wrong
     */
    public void checkVersion() {
        checkVersion(expectedVersion());
    }

    /**
     * @return the current version of the database, it is stored in the database
     */
    public String actualVersion() {
        return em.createQuery("SELECT v FROM Version v", Version.class).getSingleResult().getVersion();
    }

    /**
     * @return the version of the database that the application expects
     */
    public String expectedVersion() {
        return "0.0.2"; // Here the expected version is configured
    }

    /**
     * Exception thrown for wrong version
     */
    public static class WrongVersionException extends RuntimeException {
        /**
         * Constructor
         *
         * @param actualVersion
         * @param expectedVersion
         */
        public WrongVersionException(final String actualVersion, final String expectedVersion) {
            super("Invalid database version: actualVersion(" + actualVersion + ") != expectedVersion(" + expectedVersion + ")");
        }
    }

}
