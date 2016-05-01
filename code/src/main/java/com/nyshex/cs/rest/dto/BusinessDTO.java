package com.nyshex.cs.rest.dto;

import com.nyshex.cs.model.Business;

import lombok.Getter;

/**
 * The Data Transfer Object for the the business
 */
@Getter
public class BusinessDTO {

    private final int id;

    private final String name;

    /**
     * @param entity
     *            database entity to generate DTO from
     */
    public BusinessDTO(final Business entity) {
        id = entity.getId();
        name = entity.getName();
    }

}
