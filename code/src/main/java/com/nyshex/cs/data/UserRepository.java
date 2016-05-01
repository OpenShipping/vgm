package com.nyshex.cs.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.nyshex.cs.model.Business;
import com.nyshex.cs.model.User;

/**
 * Repository of User data
 */
@ApplicationScoped
public class UserRepository {

    @Inject
    private EntityManager em;

    /**
     * @param userId
     * @return User for given id
     */
    public User findUser(final int userId) {
        return em.find(User.class, userId);
    }

    /**
     * @param email
     * @return User for a given email
     */
    public User findUserByEmail(final String email) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<User> cq = cb.createQuery(User.class);
        final Root<User> userRoot = cq.from(User.class);
        cq.where(cb.equal(userRoot.get("email"), email));
        return em.createQuery(cq).getSingleResult();
    }

    /**
     * @param email
     * @return Users for a given email, should not return more than 1
     */
    public List<User> findUsersByEmail(final String email) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<User> cq = cb.createQuery(User.class);
        final Root<User> userRoot = cq.from(User.class);
        cq.where(cb.equal(userRoot.get("email"), email));
        return em.createQuery(cq).getResultList();
    }

    /**
     * @param business
     * @return all users of given business
     */
    public List<User> findUserByBusiness(final Business business) {
        return em.createQuery(
            "SELECT u from User u where u.business = :business", User.class)
            .setParameter("business", business)
            .getResultList();
    }

    /**
     * @return all email login names from User
     */
    public List<String> findAllLoginNames() {
        final TypedQuery<String> query = em
            .createQuery(
                "" + " SELECT loginName" + " FROM MyUserTypeEntity" + "", String.class);
        return query.getResultList();
    }

    /**
     * @param updatedUser
     */
    public void update(final User updatedUser) {
        em.merge(updatedUser);
    }

    /**
     * @param user
     *            user to persist
     */
    public void persist(final User user) {
        em.persist(user);
    }

}
