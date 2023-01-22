package com.vesey.documentable.entity.api;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.vesey.documentable.entity.Mergefieldoption;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.MergefieldoptionDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.MergefieldoptionMapperImpl;
import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.security.Logged;
import com.vesey.documentable.security.Secured;
import com.vesey.documentable.session.AuthFacade;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Stateless
@Path("/mergefieldoption")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class MergefieldoptionAPI {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	@Inject
	AuthFacade authFacade;

	@Inject
	MergefieldoptionMapperImpl mapper;

	@POST
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Create Merge field option", description = "Create a new Merge field (Form Field) option", tags = {
			"Mergefieldoption" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The created Merge field option", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MergefieldoptionDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Merge field option not modified (created)") })
	public Response createMergefieldoption(@Context HttpHeaders headers, final MergefieldoptionDTO dto) throws ConflictException {
		log.info("createMergefieldoption: Start");
		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("createMergefieldoption: User not found");
			return Response.status(Status.FORBIDDEN).entity(" User not found").build();
		}

		if (dto == null) {
			log.warn("createMergefieldoption: DTO is null");
			return Response.status(Status.BAD_REQUEST).entity("DTO is null").build();
		}

		// Mergefieldtemplate must exist
		Mergefieldtemplate mergefieldtemplateInstance = Mergefieldtemplate.getByUuid(dbFacade, dto.getMergefieldtemplateUuid());
		if (mergefieldtemplateInstance == null) {
			log.warn("createMergefieldoption: No Mergefieldtemplate found for UUID : " + dto.getMergefieldtemplateUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Mergefieldtemplate found for UUID :" + dto.getMergefieldtemplateUuid()).build();
		}

		// see if mergefieldoption already exists - it shouldn't if we are creating one
		Mergefieldoption mergefieldoptionInstance = Mergefieldoption.getByUuid(dbFacade, dto.getUuid());
		if (mergefieldoptionInstance != null) {
			log.warn("createMergefieldoption: Mergefieldoption already exists for UUID : " + dto.getUuid());
			return Response.status(Status.BAD_REQUEST).entity("Mergefieldoption already exists for UUID : " + dto.getUuid()).build();
		}

		// check authorization
		if (!mergefieldtemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("createMergefieldoption: Mergefieldtemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Mergefieldtemplate does not belong to this organistation.").build();
		}

		// all good - create the new mergefieldoption

		Mergefieldoption newMergefieldoption = mapper.getMergefieldoptionFromDTO(dto, new Mergefieldoption(), new CycleAvoidingMappingContext(user));
		newMergefieldoption.setMergefieldtemplate(mergefieldtemplateInstance);
		newMergefieldoption.setSortorder(Utils.getMaxSortorder(mergefieldtemplateInstance));
		dbFacade.persist(newMergefieldoption);

		MergefieldoptionDTO responseDTO = mapper.getDTOFromMergefieldoption(newMergefieldoption, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();

		log.info("createMergefieldoption: End");
		return response;
	}

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@Path("/{uuid}")
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Update Mergefieldoption", description = "Update an existing Snippet template", tags = {
			"Mergefieldoption" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Mergefieldoption", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MergefieldoptionDTO.class))),
					@ApiResponse(responseCode = "304", description = "Snippet template not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Snippet template has been modified by another user") })
	public Response updateMergefieldoption(@Context HttpHeaders headers, @PathParam("uuid") final String uuid, final MergefieldoptionDTO dto) throws ConflictException {
		log.info("updateMergefieldoption: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("updateMergefieldoption: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		Mergefieldoption mergefieldoptionInstance = Mergefieldoption.getByUuid(dbFacade, uuid);
		if (mergefieldoptionInstance == null) {
			log.warn("updateMergefieldoption: No Mergefieldoption found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).entity("No Mergefieldoption found for UUID : " + uuid).build();
		}

		// Mergefieldtemplate must exist
		Mergefieldtemplate mergefieldtemplateInstance = Mergefieldtemplate.getByUuid(dbFacade, dto.getMergefieldtemplateUuid());
		if (mergefieldtemplateInstance == null) {
			log.warn("updateMergefieldoption: No Mergefieldtemplate found for UUID : " + dto.getMergefieldtemplateUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Mergefieldtemplate found for UUID :" + dto.getMergefieldtemplateUuid()).build();
		}

		// check authorization
		if (!mergefieldtemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateMergefieldoption: Mergefieldtemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Mergefieldtemplate does not belong to this organistation.").build();
		}
		// OK to return
		Mergefieldoption updatedMergefieldoption = mapper.getMergefieldoptionFromDTO(dto, mergefieldoptionInstance, new CycleAvoidingMappingContext(user));
		updatedMergefieldoption = dbFacade.merge(updatedMergefieldoption);

		MergefieldoptionDTO responseDTO = mapper.getDTOFromMergefieldoption(updatedMergefieldoption, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("updateMergefieldoption: End");
		return response;

	}

	@DELETE
	@Path("/{uuid}")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Delete Mergefieldoption", description = "Delete an existing Mergefieldoption", tags = {
			"Mergefieldoption" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "200", description = "Mergefieldoption deleted"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Mergefieldoption not modified (updated)") })
	public Response deleteMergefieldoption(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) throws ConflictException {
		log.info("deleteMergefieldoption: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("deleteMergefieldoption: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		Mergefieldoption mergefieldoptionInstance = Mergefieldoption.getByUuid(dbFacade, uuid);
		if (mergefieldoptionInstance == null) {
			log.warn("deleteMergefieldoption: No Mergefieldoption found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).entity("No Mergefieldoption found for UUID : " + uuid).build();
		}

		// check authorization
		if (!mergefieldoptionInstance.getMergefieldtemplate().getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("deleteMergefieldoption: Mergefieldtemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Mergefieldtemplate does not belong to this organistation.").build();
		}
		dbFacade.delete(mergefieldoptionInstance);

		Response response = Response.ok().build();
		log.info("deleteMergefieldoption: End");
		return response;
	}

}
