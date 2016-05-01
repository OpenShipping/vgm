package com.nyshex.cs.data;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Miscellaneous helper methods for EntityManager access
 */
public class RepositoryHelper {

    @Inject
    private EntityManager em;

    /**
     * Set MySQL local variable @log
     *
     * @param log
     */
    public void setLog(final String log) {
        em.createNativeQuery("SET @log = ?").setParameter(1, log).executeUpdate();
    }

    /**
     * Set MySQL local variable @user
     *
     * @param userId
     */
    public void setUser(final int userId) {
        em.createNativeQuery("SET @user = ?").setParameter(1, userId).executeUpdate();
    }

}
