package com.nyshex.cs.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;

/**
 * The persistent class for the Contract database table.
 */
@Entity
@Data
public class VgmCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn
    private ShippingContainer shippingContainer;

    @ManyToOne
    @JoinColumn
    private User user;

    private String firstNameSignature;

    private String lastNameSignature;

    private double vgmLb;

    private double vgmKg;

    private Date archiveCreate;

    // EXTERNAL RELATIONS -->

    @OneToMany(mappedBy = "vgmCertificate", cascade = CascadeType.PERSIST)
    private List<CargoItem> cargoItems;

}
