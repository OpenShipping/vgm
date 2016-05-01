package com.nyshex.cs.rest.dto;

import java.io.IOException;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyshex.cs.model.Business;
import com.nyshex.cs.util.JacksonConfig;

import lombok.Getter;
import lombok.Setter;

/**
 * Parameters for creating a User
 */
@Getter
@Setter
@XmlRootElement
public class UserParamsDTO {

    private static final ObjectMapper OBJECT_MAPPER = JacksonConfig.newObjectMapper();

    private String firstName;

    private String lastName;

    private String email;

    private String businessName;

    private Business.Type businessType;

    private String businessAddress;

    private String businessCity;

    private String businessPostalCode;

    private String businessCountry;

    private String password;

    /**
     * @param json
     * @return de-serialized object
     * @throws IOException
     */
    public static UserParamsDTO valueOf(final String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, UserParamsDTO.class);
    }



}
