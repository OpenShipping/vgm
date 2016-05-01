package com.nyshex.cs.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;

/**
 * The persistent class for the Version database table.
 * <p>
 * Contains a single record with the current version of the database.
 */
@Entity
@Getter
public class Version {

    @Id
    private String version;

}
