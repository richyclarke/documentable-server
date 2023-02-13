package com.vesey.documentable.entity.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import com.vesey.documentable.entity.Document;
import com.vesey.documentable.entity.Documenttemplate;
import com.vesey.documentable.entity.Mergefield;
import com.vesey.documentable.entity.Snippet;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.SnippetDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.SnippetMapperImpl;
import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.security.Logged;
import com.vesey.documentable.security.NoCache;
import com.vesey.documentable.security.Secured;
import com.vesey.documentable.session.AuthFacade;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.session.DocumentBean;
import com.vesey.documentable.utils.DocumentUtils;
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
@Path("/snippet")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class SnippetAPI {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;
	@Inject
	DocumentBean documentBean;

	@Inject
	AuthFacade authFacade;

	@Inject
	SnippetMapperImpl mapper;

	@GET
	@Path("/{documentUuid}")
	@Logged
	@NoCache
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Get Snippets", description = "Get a list of Snippets", tags = {
			"Snippet" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(description = "The list of Snippets", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SnippetDTO.class)))) })
	public Response listSnippets(@Context HttpServletRequest request, @Context HttpHeaders headers, @PathParam("documentUuid") final String documentUuid) {
		log.info("listSnippets: Start. DOCUMENT UUID : " + documentUuid);

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
			log.warn("listSnippets: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Document documentInstance = Document.getByUuid(dbFacade, documentUuid);
		if (documentInstance == null) {
			log.warn("listSnippets: No Document found for Document UUID : " + documentUuid);
			return Response.status(Status.BAD_REQUEST).entity("No Document found for Document UUID : " + documentUuid).build();
		}

		// check authorization
		Documenttemplate documenttemplateInstance = documentInstance.getDocumenttemplate();
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("listSnippets: Documenttemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Documenttemplate does not belong to this organistation.").build();
		}
		Collection<Mergefield> mergefields = Mergefield.getForMatter(dbFacade, documentInstance.getMatter().getId());
		Collection<Snippet> instances = DocumentUtils.getSnippetsForDocument(documentInstance, mapper, mergefields);

		// log.info("listSnippets: Building DTO response...");
		List<SnippetDTO> dtos = new ArrayList<>();
		if (Utils.isNotEmpty(instances)) {
			for (Snippet thisSnippet : instances) {

				dtos.add(mapper.getDTOFromSnippet(thisSnippet, new CycleAvoidingMappingContext(user)));
			}
		}
		String result = Utils.convertToJSON(dtos);
		Response response = Response.ok(result).build();
		log.info("listSnippets: End");
		return response;
	}

	// @POST
	// @Logged
	// @Produces(MediaType.APPLICATION_JSON)
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Secured
	// @SecurityRequirement(name = "jwt")
	// @Operation(summary = "Create Snippet", description = "Create a new Snippet", tags = {
	// "Snippet" }, security = @SecurityRequirement(name = "jwt"), responses = {
	// @ApiResponse(description = "The created Snippet", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SnippetDTO.class))),
	// @ApiResponse(responseCode = "403", description = "Forbidden"),
	// @ApiResponse(responseCode = "304", description = "Snippet not modified (created)") })
	// public Response createSnippet(@Context HttpHeaders headers, final SnippetDTO dto) {
	// log.info("createSnippet: Start");
	// Users user = null;
	// if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
	// String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
	// user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
	// }
	// if (user == null) {
	// log.warn("createSnippet: User not found");
	// return Response.status(Status.FORBIDDEN).build();
	// }
	//
	// if (dto == null) {
	// log.warn("createSnippet: DTO is null");
	// return Response.status(Status.BAD_REQUEST).build();
	// }
	// Document documentInstance = Document.getByUuid(dbFacade, dto.getDocumentUuid());
	// if (documentInstance == null) {
	// log.warn("createSnippet: No Document found for Document UUID : " + dto.getDocumentUuid());
	// return Response.status(Status.BAD_REQUEST).entity("No Document found for Document UUID : " + dto.getDocumentUuid()).build();
	// }
	// Snippettemplate snippettemplateInstance = Snippettemplate.getByUuid(dbFacade, dto.getSnippettemplateUuid());
	// if (snippettemplateInstance == null) {
	// log.warn("createSnippet: No Snippettemplate found for Snippet Template UUID : " + dto.getSnippettemplateUuid());
	// return Response.status(Status.BAD_REQUEST).entity("No Snippettemplate found for Snippettemplate UUID : " + dto.getSnippettemplateUuid()).build();
	// }
	// // check authorization
	// Documenttemplate documenttemplateInstance = documentInstance.getDocumenttemplate();
	// if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
	// log.warn("listSnippets: Documenttemplate does not belong to this organistation.");
	// return Response.status(Status.FORBIDDEN).entity("Documenttemplate does not belong to this organistation.").build();
	// }
	//
	// // see if this snippet template already exists
	// Snippet snippetInstance = Snippet.getByUuid(dbFacade, dto.getUuid());
	// if (snippetInstance != null) {
	// log.warn("updateSnippet: Exisiting Snippet found for UUID : " + dto.getUuid());
	// return Response.status(Status.NOT_MODIFIED).entity("Exisiting Snippet found.").build();
	// } else {
	// snippetInstance = new Snippet();
	// snippetInstance = mapper.getSnippetFromDTO(dto, snippetInstance, new CycleAvoidingMappingContext(user));
	// if (Utils.isNotEmpty(dto.getParentUuid())) {
	// Snippet parent = Snippet.getByUuid(dbFacade, dto.getParentUuid());
	// if (parent == null) {
	// log.warn("createSnippet: No parent found with UUID : " + dto.getParentUuid());
	// return Response.status(Status.BAD_REQUEST).entity("No parent found with UUID : " + dto.getParentUuid()).build();
	// } else {
	// snippetInstance.setParent(parent); // set current user as the creator
	// }
	// }
	// snippetInstance.setDocument(documentInstance);
	// snippetInstance.setSnippettemplate(snippettemplateInstance);
	//
	// dbFacade.persist(snippetInstance);
	// }
	// SnippetDTO responseDTO = mapper.getDTOFromSnippet(snippetInstance, new CycleAvoidingMappingContext(user));
	// String result = Utils.convertToJSON(responseDTO);
	// Response response = Response.ok(result).build();
	//
	// log.info("createSnippet: End");
	// return response;
	// }

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Update Snippet", description = "Update an existing Snippet template", tags = {
			"Snippet" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Snippet", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SnippetDTO.class))),
					@ApiResponse(responseCode = "304", description = "Snippet template not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Snippet template has been modified by another user") })
	public Response updateSnippetByUuid(@Context HttpHeaders headers, final SnippetDTO dto) throws ConflictException {
		log.info("updateSnippetByUuid: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("updateSnippetByUuid: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Snippet snippetInstance = Snippet.getByUuid(dbFacade, dto.getUuid());
		if (snippetInstance == null) {
			log.warn("updateSnippetByUuid: No Snippet found for UUID : " + dto.getUuid());
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		Documenttemplate documenttemplateInstance = Documenttemplate.findForSnippettemplate(dbFacade, snippetInstance.getSnippettemplate());
		if (documenttemplateInstance == null) {
			log.warn("updateSnippetByUuid: No Document template found for Snippet UUID : " + dto.getUuid());
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateSnippetByUuid: Snippet does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		// OK to return
		Snippet updatedSnippet = mapper.getSnippetFromDTO(dto, snippetInstance, new CycleAvoidingMappingContext(user));
		updatedSnippet = dbFacade.merge(updatedSnippet);

		documentBean.checkDocument(updatedSnippet.getDocument());

		SnippetDTO responseDTO = mapper.getDTOFromSnippet(updatedSnippet, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("updateSnippetByUuid: End");
		return response;

	}

	@POST
	@Path("/revert/{uuid}")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Revert Snippet", description = "Revert a Snippet back to template", tags = {
			"Snippet" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "200", description = "Snippet reverted"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Snippet not modified (updated)") })
	public Response revertSnippet(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) throws ConflictException {
		log.info("revertSnippet: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("revertSnippet: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		Snippet snippetInstance = Snippet.getByUuid(dbFacade, uuid);
		if (snippetInstance == null) {
			log.warn("revertSnippet: No Snippet found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).entity("No Snippet found for UUID : " + uuid).build();
		}

		// check authorization
		Documenttemplate documenttemplateInstance = Documenttemplate.findForSnippettemplate(dbFacade, snippetInstance.getSnippettemplate());
		if (documenttemplateInstance == null) {
			log.warn("revertSnippet: No Document template found for Snippet UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).entity("No Document template found for Snippet UUID : " + uuid).build();
		}
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("revertSnippet: Snippet does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Snippet does not belong to this organistation.").build();
		}

		snippetInstance.setContent(snippetInstance.getSnippettemplate().getContent());
		snippetInstance = dbFacade.merge(snippetInstance);

		SnippetDTO responseDTO = mapper.getDTOFromSnippet(snippetInstance, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("revertSnippet: End");
		return response;
	}

	// @DELETE
	// @Path("/{uuid}")
	// @Logged
	// @Produces(MediaType.APPLICATION_JSON)
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Secured
	// @SecurityRequirement(name = "jwt")
	// @Operation(summary = "Delete Snippet", description = "Delete an existing Snippet", tags = {
	// "Snippet" }, security = @SecurityRequirement(name = "jwt"), responses = {
	// @ApiResponse(responseCode = "200", description = "Snippet deleted"),
	// @ApiResponse(responseCode = "403", description = "Forbidden"),
	// @ApiResponse(responseCode = "304", description = "Snippet not modified (updated)") })
	// public Response deleteSnippet(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) throws ConflictException {
	// log.info("deleteSnippet: Start");
	//
	// Users user = null;
	// if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
	// String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
	// user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
	// }
	// if (user == null) {
	// log.warn("deleteSnippet: User not found");
	// return Response.status(Status.FORBIDDEN).build();
	// }
	//
	// Snippet snippetInstance = Snippet.getByUuid(dbFacade, uuid);
	// if (snippetInstance == null) {
	// log.warn("deleteSnippet: No Snippet found for UUID : " + uuid);
	// return Response.status(Status.BAD_REQUEST).build();
	// }
	//
	// // check authorization
	// Documenttemplate documenttemplateInstance = Documenttemplate.findForSnippettemplate(dbFacade, snippetInstance.getSnippettemplate());
	// if (documenttemplateInstance == null) {
	// log.warn("deleteSnippet: No Document template found for Snippet UUID : " + uuid);
	// return Response.status(Status.BAD_REQUEST).build();
	// }
	// if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
	// log.warn("deleteSnippet: Snippet does not belong to this organistation.");
	// return Response.status(Status.FORBIDDEN).build();
	// }
	//
	// // OK to delete
	//
	// dbFacade.merge(snippetInstance);
	//
	// Response response = Response.ok().build();
	// log.info("deleteSnippet: End");
	// return response;
	// }

}
