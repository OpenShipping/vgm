package com.nyshex.cs.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.nyshex.cs.model.ShippingContainer;

/**
 * A repository of Container data
 *
 */
public class ShippingContainerRepository {

    @Inject
    private EntityManager em;

    /**
     * @param reference
     *        The id supplied by the carrier.
     * @return the list of containers
     */
    @ApplicationScoped
    public List<ShippingContainer> findShippingContainers(final String reference) {
        return em
                .createQuery(""
                        + " SELECT sc"
                        + " FROM ShippingContainer sc"
                        + " WHERE 1=1"
                        + "   AND sc.reference = :reference"
                        + " ORDER BY" //
                        + "   sc.id DESC" //
                        + "", ShippingContainer.class) //
                .setParameter("reference", reference)
                .getResultList();
    }


    /**
     * @param id
     * @return The shipping container for a given id.
     */
    @ApplicationScoped
    public ShippingContainer findShippingContainer(final int id) {
        return em.find(ShippingContainer.class, id);
    }


    /**
     * @param shippingContainer
     *             to persist
     */
    public void persist(final ShippingContainer shippingContainer) {
        em.persist(shippingContainer);
    }

}
