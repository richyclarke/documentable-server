package com.vesey.documentable.entity.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.vesey.documentable.entity.Documenttemplate;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.BaseEntityDTO;
import com.vesey.documentable.entity.dto.DocumenttemplateDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.DocumenttemplateMapperImpl;
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
@Path("/documenttemplate")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class DocumenttemplateAPI {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	@Inject
	AuthFacade authFacade;

	@Inject
	DocumenttemplateMapperImpl mapper;

	/**
	 * @param id
	 * @return
	 */
	@GET
	@Path("/{uuid}")
	@Logged
	@Secured
	@NoCache
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get Documenttemplate", description = "Get Documenttemplate by id", tags = {
			"Documenttemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The Documenttemplate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumenttemplateDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "400", description = "Documenttemplate not found")
			})
	public Response getDocumenttemplateByUuid(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) {
		log.info("getDocumenttemplateByUuid: Start ------ REST : GET DOCUMENTTEMPLATE BY ID-----");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("getDocumenttemplateById: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Documenttemplate documenttemplateInstance = Documenttemplate.getByUuid(dbFacade, uuid);
		if (documenttemplateInstance == null) {
			log.warn("getDocumenttemplateByUuid: No Documenttemplate found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateDocumenttemplate: Documenttemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		BaseEntityDTO dto = mapper.getDTOFromDocumenttemplate(documenttemplateInstance, new CycleAvoidingMappingContext(user));

		String result = Utils.convertToJSON(dto);
		Response response = Response.ok(result).build();
		// Response response = Response.status(Status.SERVICE_UNAVAILABLE).build();
		log.info("getDocumenttemplateByUuid: End");
		return response;
	}

	@GET
	@Logged
	@NoCache
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Get Documenttemplates", description = "Get a list of Documenttemplate", tags = {
			"Documenttemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(description = "The list of Documenttemplates", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DocumenttemplateDTO.class)))) })
	public Response listDocumenttemplates(@Context HttpServletRequest request, @Context HttpHeaders headers) {
		log.info("listDocumenttemplates: Start------ REST : GET DOCUMENTTEMPLATES LIST-----");

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
			log.warn("listDocumenttemplates: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("userId", user.getId());
		requestMap.put("organisationId", user.getOrganisation().getId());

		String sql = Documenttemplate.getListSQL(Documenttemplate.class, requestMap);
		Map<String, Object> params = Documenttemplate.getListParams(requestMap, user);

		Integer startPosition = null;
		Integer maxResults = null;

		Integer page = (Integer) requestMap.get("page");
		Integer rowsPerPage = (Integer) requestMap.get("rowsPerPage");

		if (page != null && rowsPerPage != null) {
			maxResults = rowsPerPage;
			startPosition = page * rowsPerPage;
		}
		String result;
		// log.info("listDocumenttemplates: Getting entity list...");
		List<Documenttemplate> instances = dbFacade.getEntityList(Documenttemplate.class, sql, params, false, startPosition, maxResults);
		// log.info("listDocumenttemplates: Building DTO response...");

		List<DocumenttemplateDTO> dtos = new ArrayList<>();
		if (Utils.isNotEmpty(instances)) {
			for (Documenttemplate thisDocumenttemplate : instances) {
				dtos.add(mapper.getDTOFromDocumenttemplate(thisDocumenttemplate, new CycleAvoidingMappingContext(user)));
			}
		}
		result = Utils.convertToJSON(dtos);
		Response response = Response.ok(result).build();
		log.info("listDocumenttemplates: End");
		return response;
	}

	@POST
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Create Documenttemplate", description = "Create a new Documenttemplate", tags = {
			"Documenttemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The created Documenttemplate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumenttemplateDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Documenttemplate not modified (created)") })
	public Response createDocumenttemplate(@Context HttpHeaders headers, final DocumenttemplateDTO dto) {
		log.info("createDocumenttemplate: Start");
		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("createDocumenttemplate: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		if (dto == null) {
			log.warn("createDocumenttemplate: DTO is null");
			return Response.status(Status.BAD_REQUEST).build();
		}

		// see if this documenttemplate already exists
		Documenttemplate documenttemplateInstance = Documenttemplate.getByUuid(dbFacade, dto.getUuid());
		if (documenttemplateInstance != null) {
			log.warn("updateDocumenttemplate: Exisiting Documenttemplate found for UUID : " + dto.getUuid());

			// check authorization
			if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
				log.warn("updateDocumenttemplate: Documenttemplate does not belong to this organistation.");
				return Response.status(Status.FORBIDDEN).build();
			}

		} else {
			documenttemplateInstance = new Documenttemplate();
			documenttemplateInstance = mapper.getDocumenttemplateFromDTO(dto, documenttemplateInstance, new CycleAvoidingMappingContext(user));
			documenttemplateInstance.setCreatedby(user); // set current user as the creator
			documenttemplateInstance.setNumbered(true);
			dbFacade.persist(documenttemplateInstance);
		}
		DocumenttemplateDTO responseDTO = mapper.getDTOFromDocumenttemplate(documenttemplateInstance, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("createDocumenttemplate: End");
		return response;

	}

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@Path("/{uuid}")
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Update Documenttemplate", description = "Update an existing Documenttemplate", tags = {
			"Documenttemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Documenttemplate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumenttemplateDTO.class))),
					@ApiResponse(responseCode = "304", description = "Documenttemplate not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Documenttemplate has been modified by another user") })
	public Response updateDocumenttemplate(@Context HttpHeaders headers, @PathParam("uuid") final String uuid, final DocumenttemplateDTO dto) throws ConflictException {
		log.info("updateDocumenttemplate: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("updateDocumenttemplate: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Documenttemplate documenttemplateInstance = Documenttemplate.getByUuid(dbFacade, uuid);
		if (documenttemplateInstance == null) {
			log.warn("updateDocumenttemplate: No Documenttemplate found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateDocumenttemplate: Documenttemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		// OK to return
		Documenttemplate updatedDocumenttemplate = mapper.getDocumenttemplateFromDTO(dto, documenttemplateInstance, new CycleAvoidingMappingContext(user));
		updatedDocumenttemplate = dbFacade.merge(updatedDocumenttemplate);

		DocumenttemplateDTO responseDTO = mapper.getDTOFromDocumenttemplate(updatedDocumenttemplate, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("updateDocumenttemplate: End");
		return response;

	}

	@DELETE
	@Path("/{uuid}")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Delete Documenttemplate", description = "Delete an existing Documenttemplate", tags = {
			"Documenttemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "200", description = "Documenttemplate deleted"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Documenttemplate not modified (updated)") })
	public Response deleteDocumenttemplate(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) throws ConflictException {
		log.info("deleteDocumenttemplate: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("deleteDocumenttemplate: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Documenttemplate documenttemplateInstance = Documenttemplate.getByUuid(dbFacade, uuid);
		if (documenttemplateInstance == null) {
			log.warn("deleteDocumenttemplate: No Documenttemplate found for ID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("deleteDocumenttemplate: Documenttemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		// OK to delete
		documenttemplateInstance.setActive(false);
		dbFacade.merge(documenttemplateInstance);

		Response response = Response.ok().build();
		log.info("deleteDocumenttemplate: End");

		return response;
	}

}
