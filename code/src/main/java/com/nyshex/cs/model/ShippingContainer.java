package com.nyshex.cs.model;

import lombok.Data;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 */
@Entity
@Data
public class ShippingContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The reference number given by the carrier/owner.
     */
    private String reference;

    /**
     * The carrier/owner for the given container.
     */
    @ManyToOne
    @JoinColumn
    private Business business;

    private int size;

    private String type;

    private double tareWeightLb;

    private double tareWeightKg;


    // EXTERNAL RELATIONS -->

    @OneToMany
    private List<VgmCertificate> vgmCertificates;


}
