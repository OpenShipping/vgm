package com.nyshex.cs.rest.dto;

import java.io.IOException;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyshex.cs.util.JacksonConfig;

import lombok.Getter;
import lombok.Setter;

/**
 * Parameters for creating a User
 */
@Getter
@Setter
@XmlRootElement
public class ShippingContainerDTO {

    private static final ObjectMapper OBJECT_MAPPER = JacksonConfig.newObjectMapper();

    private String reference;
    private int size;
    private String type;
    private double tareWeightLb;
    private double tareWeightKg;
    private String carrierCode;
    private int shippingContainerId;

    /**
     * @param json
     * @return de-serialized object
     * @throws IOException
     */
    public static ShippingContainerDTO valueOf(final String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, ShippingContainerDTO.class);
    }



}
