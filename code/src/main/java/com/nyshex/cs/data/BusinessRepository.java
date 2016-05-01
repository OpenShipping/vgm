package com.nyshex.cs.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.nyshex.cs.model.Business;

/**
 * Repository of Business data
 */
@ApplicationScoped
public class BusinessRepository {

    @Inject
    private EntityManager em;

    /**
     * @param businessId
     * @return Business for given id
     */
    public Business findBusiness(final int businessId) {
        return em.find(Business.class, businessId);
    }

    /**
     * @param carrierCode
     * @return Business for given SCAC code
     */
    public List<Business> findBusinessByCarrierCode(final String carrierCode) {
        return em
                .createQuery(""
                        + " SELECT b"
                        + " FROM Business b"
                        + " WHERE 1=1"
                        + " AND b.carrierCode = :carrierCode"
                        + "", Business.class)
                .setParameter("carrierCode", carrierCode)
                .getResultList();
    }

    /**
     * @param name
     * @return Business with the given name
     * @throws NoResultException
     *             if not found
     */
    public Business findBusinessByName(final String name) {
        final TypedQuery<Business> query = em
                .createQuery("" //
                        + " SELECT b" //
                        + " FROM Business b" //
                        + " WHERE b.name = :name" //
                        + "", Business.class) //
                .setParameter("name", name);
        return query.getSingleResult();
    }


    /**
     * @return the business representing NYSHEX
     */
    public Business findNYSHEX() {
        return findBusinessByName("NYSHEX, LLC");
    }

    /**
     * @return all businesses
     */
    public List<Business> findBusinesses() {
        return em
                .createQuery("" //
                        + " SELECT b" //
                        + " FROM Business b" //
                        + " WHERE 1=1" //
                        + " ORDER BY id DESC" //
                        + "", Business.class) //
                .getResultList();
    }

    /**
     * @param business
     *        JPA persist the business
     */
    public void persist(final Business business) {
        em.persist(business);
    }
}
