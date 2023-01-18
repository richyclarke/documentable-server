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
import com.vesey.documentable.entity.Snippettemplate;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.SnippettemplateDTO;
import com.vesey.documentable.entity.dto.SnippettemplateSwapDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.SnippettemplateMapperImpl;
import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.security.Logged;
import com.vesey.documentable.security.NoCache;
import com.vesey.documentable.security.Secured;
import com.vesey.documentable.session.AuthFacade;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.session.RestFacade;
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
@Path("/snippettemplate")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class SnippettemplateAPI {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	@Inject
	AuthFacade authFacade;
	@Inject
	RestFacade restFacade;

	@Inject
	SnippettemplateMapperImpl mapper;

	@GET
	@Path("/{documenttemplateUuid}")
	@Logged
	@NoCache
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Get Snippettemplates", description = "Get a list of Snippettemplates", tags = {
			"Snippettemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(description = "The list of Snippettemplates", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SnippettemplateDTO.class)))) })
	public Response listSnippettemplates(@Context HttpServletRequest request, @Context HttpHeaders headers, @PathParam("documenttemplateUuid") final String documenttemplateUuid) {
		log.info("listSnippettemplates: Start. DOCUMENTTEMPLATE UUID : " + documenttemplateUuid);

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
			log.warn("listSnippettemplates: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		// check authorization
		Documenttemplate documenttemplateInstance = Documenttemplate.getByUuid(dbFacade, documenttemplateUuid);
		if (documenttemplateInstance == null) {
			log.warn("listSnippettemplates: No Documenttemplate found for Documenttemplate UUID : " + documenttemplateUuid);
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("listSnippettemplates: Documenttemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("documenttemplateUuid", documenttemplateUuid);

		String sql = Snippettemplate.getListSQL(Snippettemplate.class, requestMap);
		Map<String, Object> params = Snippettemplate.getListParams(requestMap, user);

		Integer startPosition = null;
		Integer maxResults = null;

		Integer page = (Integer) requestMap.get("page");
		Integer rowsPerPage = (Integer) requestMap.get("rowsPerPage");

		if (page != null && rowsPerPage != null) {
			maxResults = rowsPerPage;
			startPosition = page * rowsPerPage;
		}
		String result;
		// log.info("listSnippettemplates: Getting entity list...");
		List<Snippettemplate> instances = dbFacade.getEntityList(Snippettemplate.class, sql, params, false, startPosition, maxResults);
		// log.info("listSnippettemplates: Building DTO response...");

		List<SnippettemplateDTO> dtos = new ArrayList<>();
		if (Utils.isNotEmpty(instances)) {
			for (Snippettemplate thisSnippettemplate : instances) {
				dtos.add(mapper.getDTOFromSnippettemplate(thisSnippettemplate, new CycleAvoidingMappingContext(user)));
			}
		}
		result = Utils.convertToJSON(dtos);
		Response response = Response.ok(result).build();
		log.info("listSnippettemplates: End");
		return response;
	}

	@GET
	@Path("/documenttemplate/{documenttemplateUuid}")
	@Logged
	@NoCache
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Get Snippettemplates for a documenttemplate", description = "Get a list of Snippettemplates for a document template", tags = {
			"Snippettemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(description = "The list of Snippettemplates", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SnippettemplateDTO.class)))) })
	public Response listSnippettemplatesForDocumenttemplate(@Context HttpServletRequest request, @Context HttpHeaders headers,
			@PathParam("documenttemplateUuid") final String documenttemplateUuid) {
		log.info("listSnippettemplatesForDocumenttemplate: Start. DOCUMENTTEMPLATE UUID : " + documenttemplateUuid);

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
			log.warn("listSnippettemplatesForDocumenttemplate: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		// check authorization
		Documenttemplate documenttemplateInstance = Documenttemplate.getByUuid(dbFacade, documenttemplateUuid);
		if (documenttemplateInstance == null) {
			log.warn("listSnippettemplatesForDocumenttemplate: No Documenttemplate found for Documenttemplate UUID : " + documenttemplateUuid);
			return Response.status(Status.BAD_REQUEST).entity("No Documenttemplate found for Documenttemplate UUID : " + documenttemplateUuid).build();
		}
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("listSnippettemplatesForDocumenttemplate: Documenttemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Documenttemplate does not belong to this organistation.").build();
		}

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("listSnippettemplatesForDocumenttemplate", documenttemplateUuid);

		String sql = Snippettemplate.getAllListSQL(Snippettemplate.class, requestMap);
		Map<String, Object> params = Snippettemplate.getListParams(requestMap, user);

		Integer startPosition = null;
		Integer maxResults = null;

		Integer page = (Integer) requestMap.get("page");
		Integer rowsPerPage = (Integer) requestMap.get("rowsPerPage");

		if (page != null && rowsPerPage != null) {
			maxResults = rowsPerPage;
			startPosition = page * rowsPerPage;
		}
		String result;
		// log.info("listSnippettemplates: Getting entity list...");
		List<Snippettemplate> instances = dbFacade.getEntityList(Snippettemplate.class, sql, params, false, startPosition, maxResults);
		// log.info("listSnippettemplates: Building DTO response...");

		List<SnippettemplateDTO> dtos = new ArrayList<>();
		if (Utils.isNotEmpty(instances)) {
			for (Snippettemplate thisSnippettemplate : instances) {
				dtos.add(mapper.getDTOFromSnippettemplate(thisSnippettemplate, new CycleAvoidingMappingContext(user)));
			}
		}
		result = Utils.convertToJSON(dtos);
		Response response = Response.ok(result).build();
		log.info("listSnippettemplates: End");
		return response;
	}

	@POST
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Create Snippettemplate", description = "Create a new Snippettemplate", tags = {
			"Snippettemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The created Snippettemplate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SnippettemplateDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Snippettemplate not modified (created)") })
	public Response createSnippettemplate(@Context HttpHeaders headers, final SnippettemplateDTO dto) {
		log.info("createSnippettemplate: Start");
		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("createSnippettemplate: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		if (dto == null) {
			log.warn("createSnippettemplate: DTO is null");
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		Documenttemplate documenttemplateInstance = Documenttemplate.getByUuid(dbFacade, dto.getDocumenttemplateUuid());
		if (documenttemplateInstance == null) {
			log.warn("listSnippettemplates: No Documenttemplate found for UUID : " + dto.getDocumenttemplateUuid());
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("listSnippettemplates: Documenttemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		// see if this snippet template already exists
		Snippettemplate snippettemplateInstance = Snippettemplate.getByUuid(dbFacade, dto.getUuid());
		if (snippettemplateInstance != null) {
			log.warn("updateSnippettemplate: Exisiting Snippettemplate found for UUID : " + dto.getUuid());
		} else {
			snippettemplateInstance = new Snippettemplate();
			snippettemplateInstance = mapper.getSnippettemplateFromDTO(dto, snippettemplateInstance, new CycleAvoidingMappingContext(user));
			if (Utils.isNotEmpty(dto.getParentUuid())) {
				Snippettemplate parent = Snippettemplate.getByUuid(dbFacade, dto.getParentUuid());
				if (parent == null) {
					log.warn("createSnippettemplate: No parent found with UUID : " + dto.getParentUuid());
					return Response.status(Status.BAD_REQUEST).build();
				} else {
					snippettemplateInstance.setParent(parent); // set current user as the creator
				}
			}
			snippettemplateInstance.setDocumenttemplate(documenttemplateInstance);
			// clear the ruleset (if we're cloning)
			snippettemplateInstance.setRuleset(null);
			snippettemplateInstance.setNumbered(true);
			dbFacade.persist(snippettemplateInstance);
		}
		SnippettemplateDTO responseDTO = mapper.getDTOFromSnippettemplate(snippettemplateInstance, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();

		log.info("createSnippettemplate: End");
		return response;
	}

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@Path("/{uuid}")
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Update Snippettemplate", description = "Update an existing Snippet template", tags = {
			"Snippettemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Snippettemplate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SnippettemplateDTO.class))),
					@ApiResponse(responseCode = "304", description = "Snippet template not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Snippet template has been modified by another user") })
	public Response updateSnippettemplateByUuid(@Context HttpHeaders headers, @PathParam("uuid") final String uuid, final SnippettemplateDTO dto) throws ConflictException {
		log.info("updateSnippettemplateByUuid: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("updateSnippettemplateByUuid: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Snippettemplate snippettemplateInstance = Snippettemplate.getByUuid(dbFacade, uuid);
		if (snippettemplateInstance == null) {
			log.warn("updateSnippettemplateByUuid: No Snippettemplate found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		Documenttemplate documenttemplateInstance = Documenttemplate.findForSnippettemplate(dbFacade, snippettemplateInstance);
		if (documenttemplateInstance == null) {
			log.warn("updateSnippettemplateByUuid: No Document template found for Snippettemplate UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateSnippettemplateByUuid: Snippettemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		// OK to return
		Snippettemplate updatedSnippettemplate = mapper.getSnippettemplateFromDTO(dto, snippettemplateInstance, new CycleAvoidingMappingContext(user));
		updatedSnippettemplate = dbFacade.merge(updatedSnippettemplate);

		SnippettemplateDTO responseDTO = mapper.getDTOFromSnippettemplate(updatedSnippettemplate, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("updateSnippettemplateByUuid: End");
		return response;

	}

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@Path("/swap")
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Swap Snippettemplate", description = "Swap Snippet templates", tags = {
			"Snippettemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Snippettemplate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SnippettemplateDTO.class))),
					@ApiResponse(responseCode = "304", description = "Snippet templates not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Snippet templates have been modified by another user") })
	public Response swapSnippettemplates(@Context HttpHeaders headers, final SnippettemplateSwapDTO dto) throws ConflictException {
		log.info("swapSnippettemplates: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("swapSnippettemplates: User not found");
			return Response.status(Status.FORBIDDEN).entity(" User not found").build();
		}

		Snippettemplate sourceSnippettemplateInstance = Snippettemplate.getByUuid(dbFacade, dto.getSourceUuid());
		if (sourceSnippettemplateInstance == null) {
			log.warn("swapSnippettemplates: No Snippettemplate found for UUID : " + dto.getSourceUuid());
			return Response.status(Status.BAD_REQUEST).entity(" No Snippettemplate found for UUID : " + dto.getSourceUuid()).build();
		}

		Snippettemplate destinationSnippettemplateInstance = Snippettemplate.getByUuid(dbFacade, dto.getDestinationUuid());
		if (destinationSnippettemplateInstance == null) {
			log.warn("swapSnippettemplates: No Snippettemplate found for UUID : " + dto.getDestinationUuid());
			return Response.status(Status.BAD_REQUEST).entity(" No Snippettemplate found for UUID : " + dto.getDestinationUuid()).build();
		}

		// check authorization
		Documenttemplate documenttemplateInstance = Documenttemplate.findForSnippettemplate(dbFacade, sourceSnippettemplateInstance);
		if (documenttemplateInstance == null) {
			log.warn("swapSnippettemplates: No Document template found for Snippettemplate UUID : " + dto.getSourceUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Document template found for Snippettemplate UUID : " + dto.getSourceUuid()).build();
		}

		documenttemplateInstance = Documenttemplate.findForSnippettemplate(dbFacade, destinationSnippettemplateInstance);
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("swapSnippettemplates: Snippettemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("No Document template found for Snippettemplate UUID : " + dto.getDestinationUuid()).build();
		}

		// OK to swap

		Response response = restFacade.moveSnippettemplate(sourceSnippettemplateInstance, destinationSnippettemplateInstance, dto.getIndex());
		log.info("swapSnippettemplates: End");
		return response;

	}

	@DELETE
	@Path("/{uuid}")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Delete Snippettemplate", description = "Delete an existing Snippettemplate", tags = {
			"Snippettemplate" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "200", description = "Snippettemplate deleted"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Snippettemplate not modified (updated)") })
	public Response deleteSnippettemplate(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) throws ConflictException {
		log.info("deleteSnippettemplate: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("deleteSnippettemplate: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Snippettemplate snippettemplateInstance = Snippettemplate.getByUuid(dbFacade, uuid);
		if (snippettemplateInstance == null) {
			log.warn("deleteSnippettemplate: No Snippettemplate found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		Documenttemplate documenttemplateInstance = Documenttemplate.findForSnippettemplate(dbFacade, snippettemplateInstance);
		if (documenttemplateInstance == null) {
			log.warn("deleteSnippettemplate: No Document template found for Snippettemplate UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("deleteSnippettemplate: Snippettemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		// OK to delete
		snippettemplateInstance.setActive(false);
		dbFacade.merge(snippettemplateInstance);

		Response response = Response.ok().build();
		log.info("deleteSnippettemplate: End");
		return response;
	}

}
