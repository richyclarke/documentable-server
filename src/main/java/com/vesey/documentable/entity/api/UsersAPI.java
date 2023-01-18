package com.vesey.documentable.entity.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.BaseEntityDTO;
import com.vesey.documentable.entity.dto.UserDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.UserMapperImpl;
import com.vesey.documentable.security.Logged;
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
@Path("/users")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class UsersAPI {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	@Inject
	AuthFacade authFacade;

	@Inject
	RestFacade restFacade;

	@Inject
	UserMapperImpl mapper;

	/**
	 * @param id
	 * @return
	 */
	@GET
	@Path("/{uuid}")
	@Logged
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)

	@Operation(summary = "Get User by ID", description = "Get User by ID", tags = { "User" }, security = @SecurityRequirement(name = "jwt"), responses = {
			@ApiResponse(description = "The User", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "400", description = "Users not found")
	})
	public Response getUsersByUuid(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) {
		log.info("getUsersByUuid: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("getUsersByUuid: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Users dbInstance = Users.getByUuid(dbFacade, uuid);
		if (dbInstance == null) {
			log.warn("getUsersByUuid: No Users found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		String result;
		BaseEntityDTO dto = mapper.getDTOFromUser(dbInstance, new CycleAvoidingMappingContext(user));

		result = Utils.convertToJSON(dto);
		Response response = Response.ok(result).build();
		log.info("getUsersById: End");
		return response;
	}

	@POST
	@Path("/list")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Get Users", description = "Get a list of Users", tags = { "User" }, security = @SecurityRequirement(name = "jwt"), responses = {
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(description = "The list of Users", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))) })
	public Response listUsers(@Context HttpServletRequest request, @Context HttpHeaders headers, Map<String, Object> requestMap) {
		log.info("listUsers: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("listUsers: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		String sql = Users.getListSQL(requestMap);
		Map<String, Object> params = Users.getListParams(requestMap);

		Integer startPosition = null;
		Integer maxResults = null;

		Integer page = (Integer) requestMap.get("page");
		Integer rowsPerPage = (Integer) requestMap.get("rowsPerPage");

		if (page != null && rowsPerPage != null) {
			maxResults = rowsPerPage;
			startPosition = page * rowsPerPage;
		}
		String result;
		log.info("listUsers: Getting entity list...");
		List<Users> instances = dbFacade.getEntityList(Users.class, sql, params, false, startPosition, maxResults);
		log.info("listUsers: Building DTO response...");

		List<UserDTO> dtos = new ArrayList<>();
		if (Utils.isNotEmpty(instances)) {
			for (Users thisUsers : instances) {
				dtos.add(mapper.getDTOFromUser(thisUsers, new CycleAvoidingMappingContext(user)));
			}
		}
		result = Utils.convertToJSON(dtos);
		Response response = Response.ok(result).build();
		log.info("listUsers: End");
		return response;
	}

}
