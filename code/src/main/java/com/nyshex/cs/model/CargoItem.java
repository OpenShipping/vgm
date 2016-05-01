package com.nyshex.cs.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

/**
 * The persistent class for the CargoItem database table.
 */
@Entity
@Data
public class CargoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn
    private VgmCertificate vgmCertificate;

    private float weightKg;

    private float weightLb;

    private String description;


}
