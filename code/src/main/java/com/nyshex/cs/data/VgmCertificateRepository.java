package com.nyshex.cs.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.nyshex.cs.model.Business;
import com.nyshex.cs.model.User;
import com.nyshex.cs.model.VgmCertificate;

/**
 * Repository of Contract data
 */
@ApplicationScoped
public class VgmCertificateRepository {

    @Inject
    private EntityManager em;

    /**
     * @param vgmCertificateId
     * @return certificate for a given id
     */
    public VgmCertificate findCertificate(final int vgmCertificateId) {
        return em.find(VgmCertificate.class, vgmCertificateId);
    }

    /**
     * @param vgmCertificate
     *            new Contract to insert in database
     */
    public void persist(final VgmCertificate vgmCertificate) {
        em.persist(vgmCertificate);
    }

    /**
     * @param user
     * @return vgmCertificates for the given user, order by age (id) newest first
     */
    public List<VgmCertificate> findCertificates(final User user) {
        return em
                .createQuery(""
                        + " SELECT vc"
                        + " FROM VgmCertificate vc"
                        + " WHERE 1=1"
                        + "   AND vc.user = :user"
                        + " ORDER BY id DESC"
                        + "", VgmCertificate.class)
                .setParameter("user", user.getBusiness())
                .getResultList();
    }


    /**
     * @param business
     * @return vgmCertificates for the given business, order by age (id) newest first
     */
    public List<VgmCertificate> findCertificates(final Business business) {
        return em
                .createQuery(""
                        + " SELECT vc"
                        + " FROM VgmCertificate vc"
                        + " WHERE 1=1"
                        + "   AND vc.user.business = :business"
                        + " ORDER BY id DESC"
                        + "", VgmCertificate.class)
                .setParameter("business", business)
                .getResultList();
    }

}
