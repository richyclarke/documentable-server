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

import com.vesey.documentable.entity.Documenttemplate;
import com.vesey.documentable.entity.Rulegroup;
import com.vesey.documentable.entity.Ruleset;
import com.vesey.documentable.entity.Snippettemplate;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.RulegroupDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.RulegroupMapperImpl;
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
@Path("/rulegroup")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class RulegroupAPI {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	@Inject
	AuthFacade authFacade;

	@Inject
	RulegroupMapperImpl mapper;

	@POST
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Create Rulegroup", description = "Create a new Rulegroup", tags = {
			"Rulegroup" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The created Rulegroup", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RulegroupDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Rulegroup not modified (created)") })
	public Response createRulegroup(@Context HttpHeaders headers, final RulegroupDTO dto) throws ConflictException {
		log.info("createRulegroup: Start");
		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("createRulegroup: User not found");
			return Response.status(Status.FORBIDDEN).entity(" User not found").build();
		}

		if (dto == null) {
			log.warn("createRulegroup: DTO is null");
			return Response.status(Status.BAD_REQUEST).entity("DTO is null").build();
		}

		// snippet template must exist
		Snippettemplate snippettemplateInstance = Snippettemplate.getByUuid(dbFacade, dto.getSnippettemplateUuid());
		if (snippettemplateInstance == null) {
			log.warn("createRulegroup: No Snippettemplate found for UUID : " + dto.getSnippettemplateUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Snippettemplate found for UUID :" + dto.getSnippettemplateUuid()).build();
		}

		// see if rulegroup already exists - it shouldn't if we are creating one
		Rulegroup rulegroupInstance = Rulegroup.getByUuid(dbFacade, dto.getUuid());
		if (rulegroupInstance != null) {
			log.warn("createRulegroup: Rulegroup already exists for UUID : " + dto.getUuid());
			return Response.status(Status.BAD_REQUEST).entity("Rulegroup already exists for UUID : " + dto.getUuid()).build();
		}

		// check authorization
		if (!snippettemplateInstance.getDocumenttemplate().getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("createRulegroup: Documenttemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Documenttemplate does not belong to this organistation.").build();
		}

		// all good - create the new rulegroup
		// ruleset must exist - if not create it
		if (snippettemplateInstance.getRuleset() == null) {
			Ruleset newRuleset = new Ruleset();
			dbFacade.persist(newRuleset);

			snippettemplateInstance.setRuleset(newRuleset);
			snippettemplateInstance = dbFacade.merge(snippettemplateInstance);
		}

		Rulegroup newRulegroup = mapper.getRulegroupFromDTO(dto, new Rulegroup(), new CycleAvoidingMappingContext(user));
		newRulegroup.setRuleset(snippettemplateInstance.getRuleset());
		dbFacade.persist(newRulegroup);

		RulegroupDTO responseDTO = mapper.getDTOFromRulegroup(newRulegroup, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();

		log.info("createRulegroup: End");
		return response;
	}

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@Path("/{uuid}")
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Update Rulegroup", description = "Update an existing Snippet template", tags = {
			"Rulegroup" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Rulegroup", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RulegroupDTO.class))),
					@ApiResponse(responseCode = "304", description = "Snippet template not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Snippet template has been modified by another user") })
	public Response updateRulegroup(@Context HttpHeaders headers, @PathParam("uuid") final String uuid, final RulegroupDTO dto) throws ConflictException {
		log.info("updateRulegroup: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("updateRulegroup: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		Rulegroup rulegroupInstance = Rulegroup.getByUuid(dbFacade, uuid);
		if (rulegroupInstance == null) {
			log.warn("updateRulegroup: No Rulegroup found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).entity("No Rulegroup found for UUID : " + uuid).build();
		}

		// snippet template must exist
		Snippettemplate snippettemplateInstance = Snippettemplate.getByUuid(dbFacade, dto.getSnippettemplateUuid());
		if (snippettemplateInstance == null) {
			log.warn("updateRulegroup: No Snippettemplate found for UUID : " + dto.getSnippettemplateUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Snippettemplate found for UUID :" + dto.getSnippettemplateUuid()).build();
		}

		// check authorization
		Documenttemplate documenttemplateInstance = Documenttemplate.findForSnippettemplate(dbFacade, snippettemplateInstance);
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateRulegroup: Rulegroup does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Documenttemplate does not belong to this organistation.").build();
		}

		// OK to return
		Rulegroup updatedRulegroup = mapper.getRulegroupFromDTO(dto, rulegroupInstance, new CycleAvoidingMappingContext(user));
		updatedRulegroup = dbFacade.merge(updatedRulegroup);

		RulegroupDTO responseDTO = mapper.getDTOFromRulegroup(updatedRulegroup, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("updateRulegroup: End");
		return response;

	}

	@DELETE
	@Path("/{uuid}")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Delete Rulegroup", description = "Delete an existing Rulegroup", tags = {
			"Rulegroup" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "200", description = "Rulegroup deleted"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Rulegroup not modified (updated)") })
	public Response deleteRulegroup(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) throws ConflictException {
		log.info("deleteRulegroup: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("deleteRulegroup: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		Rulegroup rulegroupInstance = Rulegroup.getByUuid(dbFacade, uuid);
		if (rulegroupInstance == null) {
			log.warn("deleteRulegroup: No Rulegroup found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).entity("No Rulegroup found for UUID : " + uuid).build();
		}

		// snippet template must exist
		Snippettemplate snippettemplateInstance = Snippettemplate.findForRuleset(dbFacade, rulegroupInstance.getRuleset());
		if (snippettemplateInstance == null) {
			log.warn("deleteRulegroup: No Snippettemplate found for rulegroup with UUID : " + rulegroupInstance.getUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Snippettemplate found for rulegroup with UUID :" + rulegroupInstance.getUuid()).build();
		}

		// check authorization
		Documenttemplate documenttemplateInstance = Documenttemplate.findForSnippettemplate(dbFacade, snippettemplateInstance);
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("deleteRulegroup: Rulegroup does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Rulegroup does not belong to this organistation.").build();
		}

		// Must be empty to delete
		if (Utils.isNotEmpty(rulegroupInstance.getRules())) {
			log.warn("deleteRulegroup: Rulegroup contains rules.");
			return Response.status(Status.FORBIDDEN).entity("Rulegroup contains rules.").build();
		}

		dbFacade.delete(rulegroupInstance);

		Response response = Response.ok().build();
		log.info("deleteRulegroup: End");
		return response;
	}

}
