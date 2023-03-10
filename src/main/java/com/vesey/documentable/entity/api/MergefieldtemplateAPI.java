package com.vesey.documentable.entity.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.vesey.documentable.entity.Datasource;
import com.vesey.documentable.entity.Mergefieldoption;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.MergefieldoptionDTO;
import com.vesey.documentable.entity.dto.MergefieldtemplateDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.MergefieldoptionMapperImpl;
import com.vesey.documentable.entity.map.MergefieldtemplateMapperImpl;
import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.security.Logged;
import com.vesey.documentable.security.NoCache;
import com.vesey.documentable.security.Secured;
import com.vesey.documentable.session.AuthFacade;
import com.vesey.documentable.session.DBFacade;
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
@Path("/mergefieldtemplate")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class MergefieldtemplateAPI {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	@Inject
	AuthFacade authFacade;

	@Inject
	MergefieldtemplateMapperImpl mapper;
	@Inject
	MergefieldoptionMapperImpl optionMapper;

	@GET
	@Logged
	@NoCache
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Get Mergefieldtemplates", description = "Get a list of Mergefieldtemplate for a Matter", tags = {
			"Mergefieldtemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(description = "The list of Mergefieldtemplates", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MergefieldtemplateDTO.class)))) })
	public Response listMergefieldtemplates(@Context HttpServletRequest request, @Context HttpHeaders headers) {
		log.info("listMergefieldtemplates: Start.");

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
			log.warn("listMergefieldtemplates: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		List<Mergefieldtemplate> instances = Mergefieldtemplate.getForUser(dbFacade, user);

		List<MergefieldtemplateDTO> dtos = new ArrayList<>();
		if (Utils.isNotEmpty(instances)) {
			for (Mergefieldtemplate thisMergefieldtemplate : instances) {
				dtos.add(mapper.getDTOFromMergefieldtemplate(thisMergefieldtemplate, new CycleAvoidingMappingContext(user)));
			}
		}
		String result = Utils.convertToJSON(dtos);
		Response response = Response.ok(result).build();
		log.info("listMergefieldtemplates: End");
		return response;
	}

	@POST
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Create Mergefieldtemplate", description = "Create a new Mergefieldtemplate", tags = {
			"Mergefieldtemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The created Mergefieldtemplate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MergefieldtemplateDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Mergefieldtemplate not modified (created)") })
	public Response createMergefieldtemplate(@Context HttpHeaders headers, final MergefieldtemplateDTO dto) {
		log.info("createMergefieldtemplate: Start");
		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("createMergefieldtemplate: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		if (dto == null) {
			log.warn("createMergefieldtemplate: DTO is null");
			return Response.status(Status.BAD_REQUEST).entity("DTO is null").build();
		}

		// datasource instance must exist
		Datasource datasourceInstance = Datasource.getByUuid(dbFacade, dto.getDatasourceUuid());
		if (datasourceInstance == null) {
			log.warn("createMergefieldtemplate: No Datasource found for UUID : " + dto.getDatasourceUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Datasource found for UUID : " + dto.getDatasourceUuid()).build();
		}

		// see if this merge field template already exists
		Mergefieldtemplate mergefieldtemplateInstance = Mergefieldtemplate.getByUuid(dbFacade, dto.getUuid());
		if (mergefieldtemplateInstance != null) {
			log.warn("createMergefieldtemplate: Exisiting Mergefieldtemplate found for UUID : " + dto.getUuid());
		} else {
			mergefieldtemplateInstance = new Mergefieldtemplate();
			mergefieldtemplateInstance = mapper.getMergefieldtemplateFromDTO(dto, mergefieldtemplateInstance, new CycleAvoidingMappingContext(user));
			mergefieldtemplateInstance.setCreatedby(user);
			mergefieldtemplateInstance.setDatasource(datasourceInstance);
			dbFacade.persist(mergefieldtemplateInstance);
		}
		MergefieldtemplateDTO responseDTO = mapper.getDTOFromMergefieldtemplate(mergefieldtemplateInstance, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("createMergefieldtemplate: End");
		return response;
	}

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Update Mergefieldtemplate", description = "Update an existing Merge field template", tags = {
			"Mergefieldtemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Mergefieldtemplate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MergefieldtemplateDTO.class))),
					@ApiResponse(responseCode = "304", description = "Merge field template not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Merge field template has been modified by another user") })
	public Response updateMergefieldtemplate(@Context HttpHeaders headers, final MergefieldtemplateDTO dto) throws ConflictException {
		log.info("updateMergefieldtemplate: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}

		if (user == null) {
			log.warn("updateMergefieldtemplate: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		Mergefieldtemplate mergefieldtemplateInstance = Mergefieldtemplate.getByUuid(dbFacade, dto.getUuid());
		if (mergefieldtemplateInstance == null) {
			log.warn("updateMergefieldtemplate: No Mergefieldtemplate found for UUID : " + dto.getUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Mergefieldtemplate found for UUID : " + dto.getUuid()).build();
		}

		Datasource datasourceInstance = null;
		if (Utils.isNotEmpty(dto.getDatasourceUuid())) {
			datasourceInstance = Datasource.getByUuid(dbFacade, dto.getDatasourceUuid());
			// datasource instance must exist
			if (datasourceInstance == null) {
				log.warn("updateMergefieldtemplate: No Datasource found for UUID : " + dto.getDatasourceUuid());
				return Response.status(Status.BAD_REQUEST).entity("No Datasource found for UUID : " + dto.getDatasourceUuid()).build();
			}
		} else {
			datasourceInstance = mergefieldtemplateInstance.getDatasource();
		}

		// check authorization
		if (!mergefieldtemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateMergefieldtemplate: Mergefieldtemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Mergefieldtemplate does not belong to this organistation.").build();
		}

		// OK to return
		Mergefieldtemplate updatedMergefieldtemplate = mapper.getMergefieldtemplateFromDTO(dto, mergefieldtemplateInstance, new CycleAvoidingMappingContext(user));
		mergefieldtemplateInstance.setDatasource(datasourceInstance);

		// handle options
		if (Utils.isNotEmpty(dto.getOptions())) {
			for (MergefieldoptionDTO thisDTO : dto.getOptions()) {
				if (Utils.isNotEmpty(mergefieldtemplateInstance.getOptions())) {
					Boolean foundExistingDTO = false;
					for (Mergefieldoption thisMFO : mergefieldtemplateInstance.getOptions()) {
						if (thisDTO.getUuid().equals(thisMFO.getUuid())) {
							// found existing MFO - update it
							Mergefieldoption updatedMergefieldoption = optionMapper.getMergefieldoptionFromDTO(thisDTO, thisMFO, new CycleAvoidingMappingContext(user));
							updatedMergefieldoption = dbFacade.merge(updatedMergefieldoption);
							foundExistingDTO = true;
						}
					}
					if (!foundExistingDTO) {
						// No existing MFO found - add it
						Mergefieldoption newMergefieldoption = optionMapper.getMergefieldoptionFromDTO(thisDTO, new Mergefieldoption(), new CycleAvoidingMappingContext(user));
						newMergefieldoption.setMergefieldtemplate(mergefieldtemplateInstance);
						newMergefieldoption.setSortorder(Utils.getMaxSortorder(mergefieldtemplateInstance));
						dbFacade.persist(newMergefieldoption);
					}
				}
			}
		}

		// iterate existing MFOs and see if any need to be deleted
		if (Utils.isNotEmpty(mergefieldtemplateInstance.getOptions())) {
			for (Mergefieldoption thisMFO : mergefieldtemplateInstance.getOptions()) {
				Boolean foundExistingMFO = false;
				if (Utils.isNotEmpty(dto.getOptions())) {
					for (MergefieldoptionDTO thisDTO : dto.getOptions()) {
						if (thisDTO.getUuid().equals(thisMFO.getUuid())) {
							foundExistingMFO = true;
						}
					}
				}
				if (!foundExistingMFO) {
					// existing MFO not found in DTO - delete it
					dbFacade.delete(thisMFO);
				}
			}
		}

		updatedMergefieldtemplate = dbFacade.merge(updatedMergefieldtemplate);

		MergefieldtemplateDTO responseDTO = mapper.getDTOFromMergefieldtemplate(updatedMergefieldtemplate, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("updateMergefieldtemplate: End");
		return response;

	}

	@DELETE
	@Path("/{uuid}")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Delete Mergefieldtemplate", description = "Delete an existing Mergefieldtemplate", tags = {
			"Mergefieldtemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "200", description = "Mergefieldtemplate deleted"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Mergefieldtemplate not modified (updated)") })
	public Response deleteMergefieldtemplate(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) throws ConflictException {
		log.info("deleteMergefieldtemplate: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("deleteMergefieldtemplate: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		Mergefieldtemplate mergefieldtemplateInstance = Mergefieldtemplate.getByUuid(dbFacade, uuid);
		if (mergefieldtemplateInstance == null) {
			log.warn("updateMergefieldtemplate: No Mergefieldtemplate found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).entity("No Mergefieldtemplate found for UUID : " + uuid).build();
		}

		// datasource instance must exist
		Datasource datasourceInstance = mergefieldtemplateInstance.getDatasource();
		if (datasourceInstance == null) {
			log.warn("updateMergefieldtemplate: No Datasource found for Merge Field Template  UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).entity("No Datasource found for Merge Field Template  UUID : " + uuid).build();
		}

		// check authorization
		if (!mergefieldtemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateMergefieldtemplate: Mergefieldtemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Mergefieldtemplate does not belong to this organistation.").build();
		}

		// OK to delete
		mergefieldtemplateInstance.setDeleted(true);
		dbFacade.merge(mergefieldtemplateInstance);

		Response response = Response.ok().build();
		log.info("deleteMergefieldtemplate: End");
		return response;
	}

}
