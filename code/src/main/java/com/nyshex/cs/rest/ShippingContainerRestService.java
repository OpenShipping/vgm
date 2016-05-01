package com.nyshex.cs.rest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.nyshex.cs.data.BusinessRepository;
import com.nyshex.cs.data.ShippingContainerRepository;
import com.nyshex.cs.model.Business;
import com.nyshex.cs.model.ShippingContainer;
import com.nyshex.cs.rest.dto.ShippingContainerDTO;
import com.nyshex.cs.util.CurrentUser;

/**
 * API and Services for getting certificates
 */
@Path("/private/shipping-containers")
@RequestScoped
public class ShippingContainerRestService {

    @Inject
    private ShippingContainerRepository shippingContainerRepository;

    @Inject
    private BusinessRepository businessRepository;

    @Inject
    private CurrentUser currentUser;

    @Inject
    private Logger logger;

    /**
     * @param input
     * @return List with a single message
     * @throws Exception
     */
    @POST
    @Path("/parse-csv")
    @Consumes("multipart/form-data")
    public Map<String, Object> uploadFile(final MultipartFormDataInput input) throws Exception {


        final Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        final List<InputPart> inputParts = uploadForm.get("file");
        if (inputParts.size() != 1) {
            throw new RuntimeException("inputParts.size() != 1, " + inputParts);
        }
        final InputPart inputPart = inputParts.get(0);
        try (
            final InputStream inputStream = inputPart.getBody(InputStream.class, null);
            final Reader reader =  new InputStreamReader(inputStream);
            final CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
                ) {
        List<ShippingContainerDTO> shippingContainers = new ArrayList<>();
            for (final CSVRecord record : parser) {
                String carrierCode = record.get("Carrier Code");
                List<Business> carriers = businessRepository.findBusinessByCarrierCode(carrierCode);
                if (carriers.size() > 1) {
                    logger.severe(String
                            .format("[private/shipping-containers/parse-csv] Duplicate Carrier codes found for '%s'",
                                    carrierCode));
                    return Collections.singletonMap("duplicateCarrierCodesFound", 1);
                }
                if (carriers.isEmpty()) {
                    logger.warning(String
                            .format("[private/shipping-containers/parse-csv] Carrier code not found for '%s'",
                                    carrierCode));
                    return Collections.singletonMap("carrierNotFound", 1);
                }
                ShippingContainerDTO sc = new ShippingContainerDTO();
                sc.setReference(record.get("Reference"));
                sc.setSize(Integer.parseInt(record.get("Size")));
                sc.setType(record.get("Type"));
                sc.setTareWeightLb(Double.parseDouble(record.get("Tare Lb")));
                sc.setTareWeightKg(Double.parseDouble(record.get("Tare Kg")));
                sc.setCarrierCode(carriers.get(0).getCarrierCode());
                shippingContainers.add(sc);
            }
            logger.info(String
                    .format("[private/shipping-containers/parse-csv] Parsed csv for User '%s %s', id '%d'",
                            currentUser.findUser().getFirstName(), currentUser.findUser().getLastName(),currentUser.getUserId()));
            return Collections.singletonMap("shippingContainers", shippingContainers);
        }
    }

    /**
     * @param shippingContainers
     * @return List with a single message
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Map<String, Integer> newShippingContainers(
            @QueryParam("shippingContainers") final List<ShippingContainerDTO> shippingContainers) {
        shippingContainers.stream().forEach(dto -> {
            List<Business> carriers = businessRepository.findBusinessByCarrierCode(dto.getCarrierCode());
            ShippingContainer shippingContainer = new ShippingContainer();
            shippingContainer.setReference(dto.getReference());
            shippingContainer.setSize(dto.getSize());
            shippingContainer.setType(dto.getType());
            shippingContainer.setTareWeightKg(dto.getTareWeightKg());
            shippingContainer.setTareWeightLb(dto.getTareWeightLb());
            shippingContainer.setBusiness(carriers.get(0));
            shippingContainerRepository.persist(shippingContainer);
        });
        logger.info(String.format(
                "[private/shipping-containers POST] User '%s %s', id '%d' Saved new containers.",
                currentUser.findUser().getFirstName(), currentUser.findUser().getLastName(),currentUser.getUserId()));
        return Collections.singletonMap("persisted", 1);
    }

    /**
     * @param reference
     * @return shipping containers for given reference
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<ShippingContainerDTO>> shippingContainers(@QueryParam("reference") final String reference) {
        List<ShippingContainerDTO> shippingContainerDTOs = shippingContainerRepository
            .findShippingContainers(reference).stream()
            .map(sc -> {
                ShippingContainerDTO dto = new ShippingContainerDTO();
                dto.setCarrierCode(sc.getBusiness().getCarrierCode());
                dto.setReference(sc.getReference());
                dto.setSize(sc.getSize());
                dto.setType(sc.getType());
                dto.setTareWeightKg(sc.getTareWeightKg());
                dto.setTareWeightLb(sc.getTareWeightLb());
                dto.setShippingContainerId(sc.getId());
                return dto;
            }).collect(Collectors.toList());
        logger.info(String.format("[/shipping-containers GET] User '%s %s' id '%d' queried for container ref '%s'.",
                        currentUser.findUser().getFirstName(),
                        currentUser.findUser().getLastName(),
                        currentUser.getUserId(),
                        reference));
        return Collections.singletonMap("shippingContainers", shippingContainerDTOs);
    }


}
