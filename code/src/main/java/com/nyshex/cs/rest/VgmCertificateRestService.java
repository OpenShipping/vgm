package com.nyshex.cs.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.nyshex.cs.data.VgmCertificateRepository;
import com.nyshex.cs.data.RepositoryHelper;
import com.nyshex.cs.data.ShippingContainerRepository;
import com.nyshex.cs.model.CargoItem;
import com.nyshex.cs.model.VgmCertificate;
import com.nyshex.cs.rest.dto.VgmCertificateDTO;
import com.nyshex.cs.rest.dto.VgmCertificateDTO.CargoItemDTO;
import com.nyshex.cs.util.CurrentUser;

/**
 * API and Services for getting certificates
 */
@Path("/private/vgm-certificate")
@RequestScoped
public class VgmCertificateRestService {

    @Inject
    private RepositoryHelper repositoryHelper;

    @Inject
    private CurrentUser currentUser;

    @Inject
    private VgmCertificateRepository vgmCertificateRepository;

    @Inject
    private ShippingContainerRepository shippingContainerRepository;

    @Inject
    private Logger logger;

    /**
     * @return The certificates for the current user's business.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Map<String, List<VgmCertificateDTO>> getCertificates() {

        List<VgmCertificate> certs = vgmCertificateRepository.findCertificates(currentUser.findUser().getBusiness());

        List<VgmCertificateDTO> certDtos = certs.stream().map(c -> {
            VgmCertificateDTO certDto = new VgmCertificateDTO();
            List<CargoItemDTO> cargoItems = c.getCargoItems().stream().map(ci -> {
                CargoItemDTO cargoItemDto = new CargoItemDTO();
                cargoItemDto.setDescription(ci.getDescription());
                cargoItemDto.setWeightKg(ci.getWeightKg());
                cargoItemDto.setWeightLb(ci.getWeightLb());
                return cargoItemDto;
            }).collect(Collectors.toList());
            certDto.setCargoItems(cargoItems);
            certDto.setCertificateId(c.getId());
            certDto.setCarrierCode(c.getShippingContainer().getBusiness().getCarrierCode());
            certDto.setCertifiedAt(c.getArchiveCreate());
            certDto.setFirstNameSignature(c.getFirstNameSignature());
            certDto.setLastNameSignature(c.getLastNameSignature());
            certDto.setReference(c.getShippingContainer().getReference());
            certDto.setShippingContainerId(c.getShippingContainer().getId());
            certDto.setSize(c.getShippingContainer().getSize());
            certDto.setType(c.getShippingContainer().getType());
            certDto.setTareWeightKg(c.getShippingContainer().getTareWeightKg());
            certDto.setTareWeightLb(c.getShippingContainer().getTareWeightLb());
            certDto.setVgmKg(c.getVgmKg());
            certDto.setVgmLb(c.getVgmLb());
            return certDto;
        }).collect(Collectors.toList());
        logger.info(String.format("[/vgm-certificate GET] User '%s %s' id '%d' requested certs for their business '%s'.",
                currentUser.findUser().getFirstName(),
                currentUser.findUser().getLastName(),
                currentUser.getUserId(),
                currentUser.findUser().getBusiness().getName()));
        return Collections.singletonMap("certificates", certDtos);
    }

    /**
     * @param vgmCertDTO
     * @param vgmLb
     * @param vgmKg
     * @return True (1) if the objects have been successfully persisted.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Map<String, Integer> newCertificate(@QueryParam("certificate") final VgmCertificateDTO vgmCertDTO,
            @QueryParam("vgmLb") final double vgmLb,
            @QueryParam("vgmKg") final double vgmKg) {
        VgmCertificate newCert = new VgmCertificate();
        newCert.setShippingContainer(shippingContainerRepository.findShippingContainer(vgmCertDTO.getShippingContainerId()));
        newCert.setFirstNameSignature(vgmCertDTO.getFirstNameSignature());
        newCert.setLastNameSignature(vgmCertDTO.getLastNameSignature());
        newCert.setUser(currentUser.findUser());
        newCert.setVgmLb(vgmLb);
        newCert.setVgmKg(vgmKg);
        List<CargoItem> cargoItems = new ArrayList<>();
        vgmCertDTO.getCargoItems().stream()
        .forEach(dto -> {
            CargoItem cargoItem = new CargoItem();
            cargoItem.setDescription(dto.getDescription());
            cargoItem.setWeightKg(dto.getWeightKg());
            cargoItem.setWeightLb(dto.getWeightLb());
            cargoItem.setVgmCertificate(newCert);
            cargoItems.add(cargoItem);
        });
        newCert.setCargoItems(cargoItems);
        repositoryHelper.setLog("New Cert");
        repositoryHelper.setUser(currentUser.getUserId());
        vgmCertificateRepository.persist(newCert);
        logger.info(String.format("[/vgm-certificate GET] User '%s %s' id '%d' certified container ref '%s'",
                currentUser.findUser().getFirstName(),
                currentUser.findUser().getLastName(),
                currentUser.getUserId(),
                newCert.getShippingContainer().getReference()));
        return Collections.singletonMap("certificateSaved", 1);
    }

}
