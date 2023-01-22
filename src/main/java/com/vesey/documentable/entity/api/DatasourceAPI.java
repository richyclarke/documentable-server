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
import com.vesey.documentable.entity.Datasource;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.BaseEntityDTO;
import com.vesey.documentable.entity.dto.DatasourceDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.DatasourceMapperImpl;
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
@Path("/datasource")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class DatasourceAPI {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	@Inject
	AuthFacade authFacade;

	@Inject
	DatasourceMapperImpl mapper;

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
	@Operation(summary = "Get Datasource", description = "Get Datasource by id", tags = {
			"Datasource" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The Datasource", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DatasourceDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "400", description = "Datasource not found")
			})
	public Response getDatasourceByUuid(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) {
		log.info("getDatasourceByUuid: Start ------ REST : GET Datasource BY ID-----");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("getDatasourceById: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Datasource datasourceInstance = Datasource.getByUuid(dbFacade, uuid);
		if (datasourceInstance == null) {
			log.warn("getDatasourceByUuid: No Datasource found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		if (!datasourceInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateDatasource: Datasource does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		BaseEntityDTO dto = mapper.getDTOFromDatasource(datasourceInstance, new CycleAvoidingMappingContext(user));

		String result = Utils.convertToJSON(dto);
		Response response = Response.ok(result).build();
		// Response response = Response.status(Status.SERVICE_UNAVAILABLE).build();
		log.info("getDatasourceByUuid: End");
		return response;
	}

	@GET
	@Logged
	@NoCache
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Get Datasources", description = "Get a list of Datasources for an organisation", tags = {
			"Datasource" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(description = "The list of Datasources", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DatasourceDTO.class)))) })
	public Response listDatasources(@Context HttpServletRequest request, @Context HttpHeaders headers) {
		log.info("listDatasources: Start");

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
			log.warn("listDatasources: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("organisationId", user.getOrganisation().getId());

		String sql = Datasource.getListSQL(requestMap);

		Integer startPosition = null;
		Integer maxResults = null;

		Integer page = (Integer) requestMap.get("page");
		Integer rowsPerPage = (Integer) requestMap.get("rowsPerPage");

		if (page != null && rowsPerPage != null) {
			maxResults = rowsPerPage;
			startPosition = page * rowsPerPage;
		}
		String result;
		// log.info("listDatasources: Getting entity list...");
		List<Datasource> instances = dbFacade.getEntityList(Datasource.class, sql, requestMap, false, startPosition, maxResults);
		// log.info("listDatasources: Building DTO response...");

		List<DatasourceDTO> dtos = new ArrayList<>();
		if (Utils.isNotEmpty(instances)) {
			for (Datasource thisDatasource : instances) {
				dtos.add(mapper.getDTOFromDatasource(thisDatasource, new CycleAvoidingMappingContext(user)));
			}
		}
		result = Utils.convertToJSON(dtos);
		Response response = Response.ok(result).build();
		log.info("listDatasources: End");
		return response;
	}

	@POST
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Create Datasource", description = "Create a new Datasource", tags = {
			"Datasource" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The created Datasource", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DatasourceDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Datasource not modified (created)") })
	public Response createDatasource(@Context HttpHeaders headers, final DatasourceDTO dto) {
		log.info("createDatasource: Start");
		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("createDatasource: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		if (dto == null) {
			log.warn("createDatasource: DTO is null");
			return Response.status(Status.BAD_REQUEST).entity("DTO is null").build();
		}

		// see if this datasource already exists
		Datasource datasourceInstance = Datasource.getByUuid(dbFacade, dto.getUuid());
		if (datasourceInstance != null) {
			log.warn("updateDatasource: Exisiting Datasource found for UUID : " + dto.getUuid());

			// check authorization
			if (!datasourceInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
				log.warn("updateDatasource: Datasource does not belong to this organistation.");
				return Response.status(Status.FORBIDDEN).entity("Datasource does not belong to this organistation.").build();
			}

		} else {
			datasourceInstance = new Datasource();
			datasourceInstance = mapper.getDatasourceFromDTO(dto, datasourceInstance, new CycleAvoidingMappingContext(user));
			datasourceInstance.setCreatedby(user); // set current user as the creator
			dbFacade.persist(datasourceInstance);
		}
		DatasourceDTO responseDTO = mapper.getDTOFromDatasource(datasourceInstance, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("createDatasource: End");
		return response;

	}

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@Path("/{uuid}")
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Update Datasource", description = "Update an existing Datasource", tags = {
			"Datasource" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Datasource", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DatasourceDTO.class))),
					@ApiResponse(responseCode = "304", description = "Datasource not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Datasource has been modified by another user") })
	public Response updateDatasource(@Context HttpHeaders headers, @PathParam("uuid") final String uuid, final DatasourceDTO dto) throws ConflictException {
		log.info("updateDatasource: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("updateDatasource: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Datasource datasourceInstance = Datasource.getByUuid(dbFacade, uuid);
		if (datasourceInstance == null) {
			log.warn("updateDatasource: No Datasource found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		if (!datasourceInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateDatasource: Datasource does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		log.info("updateDatasource: Name = " + datasourceInstance.getName() + " was sortorder = " + datasourceInstance.getSortorder() + " now sortorder = " + dto.getSortorder());
		// OK to return
		Datasource updatedDatasource = mapper.getDatasourceFromDTO(dto, datasourceInstance, new CycleAvoidingMappingContext(user));
		updatedDatasource = dbFacade.merge(updatedDatasource);

		DatasourceDTO responseDTO = mapper.getDTOFromDatasource(updatedDatasource, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("updateDatasource: End");
		return response;

	}

	@DELETE
	@Path("/{uuid}")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Delete Datasource", description = "Delete an existing Datasource", tags = {
			"Datasource" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "200", description = "Datasource deleted"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Datasource not modified (updated)") })
	public Response deleteDatasource(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) throws ConflictException {
		log.info("deleteDatasource: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("deleteDatasource: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Datasource datasourceInstance = Datasource.getByUuid(dbFacade, uuid);
		if (datasourceInstance == null) {
			log.warn("deleteDatasource: No Datasource found for ID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		if (!datasourceInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("deleteDatasource: Datasource does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		// OK to delete
		dbFacade.delete(datasourceInstance);

		Response response = Response.ok().build();
		log.info("deleteDatasource: End");

		return response;
	}

}
