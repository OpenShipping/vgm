package com.nyshex.cs.rest.dto;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyshex.cs.rest.VgmCertificateRestService;
import com.nyshex.cs.util.JacksonConfig;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for a VgmCertificate {@link VgmCertificateRestService}
 */
@Getter
@Setter
@XmlRootElement
public class VgmCertificateDTO {

    private static final ObjectMapper OBJECT_MAPPER = JacksonConfig.newObjectMapper();


    private List<CargoItemDTO> cargoItems;

    // From container
    private int shippingContainerId;
    private String reference;
    private int size;
    private String type;
    private double tareWeightLb;
    private double tareWeightKg;
    private String carrierCode;
    private String firstNameSignature;
    private String lastNameSignature;
    private Date certifiedAt;
    private int certificateId;
    private double vgmLb;
    private double vgmKg;

    /**
     *
     */
    @Getter
    @Setter
    public static class CargoItemDTO {

        private String description;
        private float weightKg;
        private float weightLb;
    }


    /**
     * @param json
     * @return de-serialized object
     * @throws IOException
     */
    public static VgmCertificateDTO valueOf(final String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, VgmCertificateDTO.class);
    }


}
