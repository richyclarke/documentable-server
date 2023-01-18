package com.vesey.documentable.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.vesey.documentable.entity.Users;
import com.vesey.documentable.rest.dto.IntegrationMatterDTO;
import com.vesey.documentable.security.Logged;
import com.vesey.documentable.security.NoCache;
import com.vesey.documentable.session.AuthFacade;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.session.IntegrationFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Stateless
@Path("/integration")
public class IntegrationEndpoint {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;
	@Inject
	AuthFacade authFacade;

	@Inject
	IntegrationFacade integrationFacade;

	@POST
	@Logged
	@NoCache
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Create a matter", description = "Create a new matter", tags = {
			"Integration" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "Success.", content = @Content(mediaType = "text/plain")),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "500", description = "Server Error"),
					@ApiResponse(responseCode = "304", description = "Matter not modified (created)") })
	public Response postMatter(@Context HttpHeaders headers, final IntegrationMatterDTO dto) {
		log.info("postMatter: Start");

		if (dto == null) {
			log.warn("processPostMatter:DTO is null.");
			return Response.status(Status.FORBIDDEN).entity("No DTO Found.").build();
		}
		if (dto.getCredentials() == null) {
			log.warn("processPostMatter:DTO is null.");
			return Response.status(Status.FORBIDDEN).entity("No Credentials Found.").build();
		}
		Users userInstance = null;
		try {
			userInstance = authFacade.authenticate(dto.getCredentials().getUsername(), dto.getCredentials().getPassword());
		} catch (Exception e) {
			log.warn("postMatter: Exception: " + e.getLocalizedMessage());
			return Response.status(401).entity(e.getLocalizedMessage()).build();
		}

		if (userInstance == null) {
			log.warn("postMatter: User not found.");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}
		Response response = null;
		try {
			response = integrationFacade.processPostMatter(dto, userInstance);
		} catch (Exception e) {
			log.warn("postMatter: Exception: " + e.getLocalizedMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getLocalizedMessage()).build();
		}
		log.info("postMatter: End");
		return response;
	}
}
