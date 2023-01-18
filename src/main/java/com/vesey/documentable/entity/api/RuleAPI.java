package com.vesey.documentable.entity.api;

import java.util.UUID;

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

import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.Rule;
import com.vesey.documentable.entity.Rulegroup;
import com.vesey.documentable.entity.Ruleset;
import com.vesey.documentable.entity.Snippettemplate;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.RuleDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.RuleMapperImpl;
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
@Path("/rule")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class RuleAPI {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	@Inject
	AuthFacade authFacade;

	@Inject
	RuleMapperImpl mapper;

	@POST
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Create Rule", description = "Create a new Rule", tags = {
			"Rule" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The created Rule", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RuleDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Rule not modified (created)") })
	public Response createRule(@Context HttpHeaders headers, final RuleDTO dto) throws ConflictException {
		log.info("createRule: Start");
		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("createRule: User not found");
			return Response.status(Status.FORBIDDEN).entity(" User not found").build();
		}

		if (dto == null) {
			log.warn("createRule: DTO is null");
			return Response.status(Status.BAD_REQUEST).entity("DTO is null").build();
		}

		// snippet template must exist
		Snippettemplate snippettemplateInstance = Snippettemplate.getByUuid(dbFacade, dto.getSnippettemplateUuid());
		if (snippettemplateInstance == null) {
			log.warn("createRule: No Snippettemplate found for UUID : " + dto.getSnippettemplateUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Snippettemplate found for UUID :" + dto.getSnippettemplateUuid()).build();
		}

		// see if rule already exists - it shhouldn't if we are creating one
		Rule ruleInstance = Rule.getByUuid(dbFacade, dto.getUuid());
		if (ruleInstance != null) {
			log.warn("createRule: Rule already exists for UUID : " + dto.getUuid());
			return Response.status(Status.BAD_REQUEST).entity("Rule already exists for UUID : " + dto.getUuid()).build();
		}

		// if rulegroup is specified, it must exist
		Rulegroup rulegroupInstance = null;
		if (Utils.isNotEmpty(dto.getRulegroupUuid())) {
			rulegroupInstance = Rulegroup.getByUuid(dbFacade, dto.getRulegroupUuid());
			if (rulegroupInstance == null) {
				log.warn("createRule: No Rulegroup found for UUID : " + dto.getRulegroupUuid());
				return Response.status(Status.BAD_REQUEST).entity("No Rulegroup found for UUID : " + dto.getRulegroupUuid()).build();
			}
		}

		// source Mergefieldtemplate must exist
		Mergefieldtemplate mergefieldtemplateInstance = Mergefieldtemplate.getByUuid(dbFacade, dto.getSourceUuid());
		if (mergefieldtemplateInstance == null) {
			log.warn("createRule: No Mergefieldtemplate found for UUID : " + dto.getSourceUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Mergefieldtemplate found for UUID : " + dto.getSourceUuid()).build();
		}

		// check authorization

		if (!snippettemplateInstance.getDocumenttemplate().getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("createRule: Documenttemplate does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Documenttemplate does not belong to this organistation.").build();
		}

		// all good - create the new rule
		Rule newRule = mapper.getRuleFromDTO(dto, new Rule(), new CycleAvoidingMappingContext(user));
		newRule.setSource(mergefieldtemplateInstance);

		if (snippettemplateInstance.getRuleset() == null) {
			// no existing ruleset - better create one first
			Ruleset newRuleset = new Ruleset();
			dbFacade.persist(newRuleset);

			// add inital rulegroup
			Rulegroup newRulegroup = new Rulegroup();
			newRulegroup.setUuid(UUID.randomUUID().toString());
			newRulegroup.setRuleset(newRuleset);
			dbFacade.persist(newRulegroup);

			newRule.setRulegroup(newRulegroup);
			dbFacade.persist(newRule);

			snippettemplateInstance.setRuleset(newRuleset);
			snippettemplateInstance = dbFacade.merge(snippettemplateInstance);

		} else if (rulegroupInstance != null) {
			// ruleset and rulegroup exists - add rule to rulegroup
			newRule.setRulegroup(rulegroupInstance);
			dbFacade.persist(newRule);
		} else {
			// must be brand new or existing rulegroup - error!
			log.error("createRule: Must be brand new ruleset or quote existing rulegroup : " + dto.getRulegroupUuid());
			return Response.status(Status.BAD_REQUEST).entity("Must be brand new ruleset or quote existing rulegroup").build();
		}

		RuleDTO responseDTO = mapper.getDTOFromRule(newRule, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();

		log.info("createRule: End");
		return response;
	}

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@Path("/{uuid}")
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Update Rule", description = "Update an existing Snippet template", tags = {
			"Rule" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Rule", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RuleDTO.class))),
					@ApiResponse(responseCode = "304", description = "Snippet template not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Snippet template has been modified by another user") })
	public Response updateRule(@Context HttpHeaders headers, @PathParam("uuid") final String uuid, final RuleDTO dto) throws ConflictException {
		log.info("updateRule: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("updateRule: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		Rule ruleInstance = Rule.getByUuid(dbFacade, uuid);
		if (ruleInstance == null) {
			log.warn("updateRule: No Rule found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).entity("No Rule found for UUID : " + uuid).build();
		}

		// check authorization

		if (!ruleInstance.getSource().getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateRule: Rule does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Rule does not belong to this organistation.").build();
		}

		// source Mergefieldtemplate must exist
		Mergefieldtemplate mergefieldtemplateInstance = Mergefieldtemplate.getByUuid(dbFacade, dto.getSourceUuid());
		if (mergefieldtemplateInstance == null) {
			log.warn("updateRule: No Mergefieldtemplate found for UUID : " + dto.getSourceUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Mergefieldtemplate found for UUID : " + dto.getSourceUuid()).build();
		}

		// OK to return
		Rule updatedRule = mapper.getRuleFromDTO(dto, ruleInstance, new CycleAvoidingMappingContext(user));
		updatedRule.setSource(mergefieldtemplateInstance);
		updatedRule = dbFacade.merge(updatedRule);

		RuleDTO responseDTO = mapper.getDTOFromRule(updatedRule, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("updateRule: End");
		return response;

	}

	@DELETE
	@Path("/{uuid}")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Delete Rule", description = "Delete an existing Rule", tags = {
			"Rule" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "200", description = "Rule deleted"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Rule not modified (updated)") })
	public Response deleteRule(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) throws ConflictException {
		log.info("deleteRule: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("deleteRule: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Rule ruleInstance = Rule.getByUuid(dbFacade, uuid);
		if (ruleInstance == null) {
			log.warn("deleteRule: No Rule found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization

		if (!ruleInstance.getSource().getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("deleteRule: Rule does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		// OK to delete
		dbFacade.delete(ruleInstance);

		Response response = Response.ok().build();
		log.info("deleteRule: End");
		return response;
	}

}
