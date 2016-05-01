package com.nyshex.cs.model;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the User database table.
 */
@Entity
@Getter
@Setter
@Vetoed
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;

    private String lastName;

    @ManyToOne
    @JoinColumn
    private Business business;

    private String email;

}
