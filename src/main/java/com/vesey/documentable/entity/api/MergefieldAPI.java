package com.vesey.documentable.entity.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.vesey.documentable.entity.Document;
import com.vesey.documentable.entity.Matter;
import com.vesey.documentable.entity.Mergefield;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.MergefieldDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.MergefieldMapperImpl;
import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.security.Logged;
import com.vesey.documentable.security.NoCache;
import com.vesey.documentable.security.Secured;
import com.vesey.documentable.session.AuthFacade;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.session.DocumentBean;
import com.vesey.documentable.utils.Utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Stateless
@Path("/mergefield")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class MergefieldAPI {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	@Inject
	AuthFacade authFacade;

	@Inject
	DocumentBean documentBean;

	@Inject
	MergefieldMapperImpl mapper;

	@GET
	@Path("/matter/{matterUuid}")
	@Logged
	@NoCache
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Get Mergefields", description = "Get a list of Mergefields for a matter", tags = {
			"Mergefield" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(description = "The list of Mergefields", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MergefieldDTO.class)))) })
	public Response listMergefieldsForMatter(@Context HttpServletRequest request, @Context HttpHeaders headers, @PathParam("matterUuid") final String matterUuid) {
		log.info("listMergefieldsForMatter: Start. MATTER UUID : " + matterUuid);

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			try {
				user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
			} catch (Exception e) {
				Throwable cause = e.getCause();
				if (cause instanceof TokenExpiredException) {
					log.warn("Token Expired: " + e.getLocalizedMessage());
					return Response.status(Status.UNAUTHORIZED).build();
				} else {
					return Response.status(Status.FORBIDDEN).build();
				}
			}
		}

		if (user == null) {
			log.warn("listMergefieldsForMatter: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found.").build();
		}

		// check authorisation
		Matter matterInstance = Matter.getByUuid(dbFacade, matterUuid);
		if (matterInstance == null) {
			log.warn("listMergefieldsForMatter: No Matter found for matter UUID : " + matterUuid);
			return Response.status(Status.BAD_REQUEST).entity("No Matter found for matter UUID : " + matterUuid).build();
		}
		if (!matterInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("listMergefieldsForMatter: Matter does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Matter does not belong to this organistation.").build();
		}

		Collection<Mergefield> instances = Mergefield.getForMatter(dbFacade, matterInstance.getId());

		List<MergefieldDTO> dtos = new ArrayList<>();
		if (Utils.isNotEmpty(instances)) {
			for (Mergefield thisMergefield : instances) {
				dtos.add(mapper.getDTOFromMergefield(thisMergefield, new CycleAvoidingMappingContext(user)));
			}
		}
		String result = Utils.convertToJSON(dtos);
		Response response = Response.ok(result).build();
		log.info("listMergefieldsForMatter: End");
		return response;
	}

	@GET
	@Path("/document/{documentUuid}")
	@Logged
	@NoCache
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Get Mergefields", description = "Get a list of Mergefields for a document", tags = {
			"Mergefield" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(description = "The list of Mergefields", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MergefieldDTO.class)))) })
	public Response listMergefieldsForDocument(@Context HttpServletRequest request, @Context HttpHeaders headers, @PathParam("documentUuid") final String documentUuid) {
		log.info("listMergefieldsForDocument: Start. MATTER UUID : " + documentUuid);

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			try {
				user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
			} catch (Exception e) {
				Throwable cause = e.getCause();
				if (cause instanceof TokenExpiredException) {
					log.warn("Token Expired: " + e.getLocalizedMessage());
					return Response.status(Status.UNAUTHORIZED).build();
				} else {
					return Response.status(Status.FORBIDDEN).build();
				}
			}
		}

		if (user == null) {
			log.warn("listMergefieldsForDocument: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found.").build();
		}

		// check authorisation
		Document documentInstance = Document.getByUuid(dbFacade, documentUuid);
		if (documentInstance == null) {
			log.warn("listMergefieldsForDocument: No Document found for document UUID : " + documentUuid);
			return Response.status(Status.BAD_REQUEST).entity("No Document found for document UUID : " + documentUuid).build();
		}
		if (!documentInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("listMergefieldsForDocument: Matter does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Matter does not belong to this organistation.").build();
		}

		Collection<Mergefield> instances = documentBean.getForDocument(dbFacade, documentInstance);

		List<MergefieldDTO> dtos = new ArrayList<>();
		if (Utils.isNotEmpty(instances)) {
			for (Mergefield thisMergefield : instances) {
				dtos.add(mapper.getDTOFromMergefield(thisMergefield, new CycleAvoidingMappingContext(user)));
			}
		}
		String result = Utils.convertToJSON(dtos);
		Response response = Response.ok(result).build();
		log.info("listMergefieldsForMatter: End");
		return response;
	}

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Update Mergefield", description = "Update an existing Merge field", tags = {
			"Mergefield" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Mergefield", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MergefieldDTO.class))),
					@ApiResponse(responseCode = "304", description = "Merge field not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Merge field has been modified by another user") })
	public Response updateMergefield(@Context HttpHeaders headers, final MergefieldDTO dto) throws ConflictException {
		log.info("updateMergefield: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("updateMergefield: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		Mergefield mergefieldInstance = Mergefield.getByUuid(dbFacade, dto.getUuid());
		if (mergefieldInstance == null) {
			log.warn("updateMergefield: No Mergefield found for UUID : " + dto.getUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Mergefield found for UUID : " + dto.getUuid()).build();
		}

		// check authorization

		if (!mergefieldInstance.getMergefieldtemplate().getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateMergefield: Mergefield does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("ergefield does not belong to this organistation. ").build();
		}

		// OK to return
		Mergefield updatedMergefield = mapper.getMergefieldFromDTO(dto, mergefieldInstance, new CycleAvoidingMappingContext(user));
		// copy all value fields as null fields don't get mapped and they are valid
		mergefieldInstance.setBooleanvalue(dto.getBooleanvalue());
		mergefieldInstance.setStringvalue(dto.getStringvalue());
		mergefieldInstance.setIntegervalue(dto.getIntegervalue());
		mergefieldInstance.setDecimalvalue(dto.getDecimalvalue());
		mergefieldInstance.setDatevalue(dto.getDatevalue());

		updatedMergefield = dbFacade.merge(updatedMergefield);

		MergefieldDTO responseDTO = mapper.getDTOFromMergefield(updatedMergefield, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("updateMergefield: End");
		return response;

	}

}
