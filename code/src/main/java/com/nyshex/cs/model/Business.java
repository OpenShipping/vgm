package com.nyshex.cs.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the Business database table.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Type type;

    /**
     * Possible types of businesses
     */
    public static enum Type {
        /**
         * Shipper/BCO
         */
        SHIPPER,
        /**
         * Carrier/VOCC
         */
        CARRIER,
        /**
         * Freight Forwarder/NVOCC
         */
        FORWARDER,
        /**
         * NYSHEX
         */
        NYSHEX
    }

    private String address;

    private String city;

    private String postalCode;

    private String country;

    @Column(unique=true)
    private String carrierCode;

    // OUTSIDE RELATIONS -->

    /**
     * All users registered under this business
     */
    @OneToMany(mappedBy = "business")
    private List<User> users;
}
