package com.vesey.documentable.entity.api;

import java.util.ArrayList;
import java.util.Date;
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
import com.vesey.documentable.entity.Matter;
import com.vesey.documentable.entity.Mergefield;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.BaseEntityDTO;
import com.vesey.documentable.entity.dto.MatterDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.MatterMapperImpl;
import com.vesey.documentable.enums.MatterTypeEnum;
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
@Path("/matter")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class MatterAPI {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	@Inject
	AuthFacade authFacade;

	@Inject
	MatterMapperImpl mapper;

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
	@Operation(summary = "Get Matter", description = "Get Matter by id", tags = {
			"Matter" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The Matter", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatterDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "400", description = "Matter not found")
			})
	public Response getMatterByUuid(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) {
		log.info("getMatterByUuid: Start ------ REST : GET MATTER BY ID-----");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("getMatterById: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Matter matterInstance = Matter.getByUuid(dbFacade, uuid);
		if (matterInstance == null) {
			log.warn("getMatterByUuid: No Matter found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		if (!matterInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateMatter: Matter does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		BaseEntityDTO dto = mapper.getDTOFromMatter(matterInstance, new CycleAvoidingMappingContext(user));

		String result = Utils.convertToJSON(dto);
		Response response = Response.ok(result).build();
		// Response response = Response.status(Status.SERVICE_UNAVAILABLE).build();
		log.info("getMatterByUuid: End");
		return response;
	}

	@GET
	@Logged
	@NoCache
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Get Matters", description = "Get a list of Matter", tags = {
			"Matter" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(description = "The list of Matters", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MatterDTO.class)))) })
	public Response listMatters(@Context HttpServletRequest request, @Context HttpHeaders headers) {
		log.info("listMatters: Start");

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
			log.warn("listMatters: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("userId", user.getId());
		requestMap.put("organisationId", user.getOrganisation().getId());

		String sql = Matter.getListSQL(Matter.class, requestMap);
		Map<String, Object> params = Matter.getListParams(requestMap, user);

		Integer startPosition = null;
		Integer maxResults = null;

		Integer page = (Integer) requestMap.get("page");
		Integer rowsPerPage = (Integer) requestMap.get("rowsPerPage");

		if (page != null && rowsPerPage != null) {
			maxResults = rowsPerPage;
			startPosition = page * rowsPerPage;
		}
		String result;
		List<Matter> instances = dbFacade.getEntityList(Matter.class, sql, params, false, startPosition, maxResults);

		List<MatterDTO> dtos = new ArrayList<>();
		if (Utils.isNotEmpty(instances)) {
			for (Matter thisMatter : instances) {
				dtos.add(mapper.getDTOFromMatter(thisMatter, new CycleAvoidingMappingContext(user)));
			}
		}
		result = Utils.convertToJSON(dtos);
		Response response = Response.ok(result).build();
		log.info("listMatters: End");
		return response;
	}

	@POST
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Create Matter", description = "Create a new Matter", tags = {
			"Matter" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The created Matter", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatterDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Matter not modified (created)") })
	public Response createMatter(@Context HttpHeaders headers, final MatterDTO dto) {
		log.info("createMatter: Start");
		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("createMatter: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		if (dto == null) {
			log.warn("createMatter: DTO is null");
			return Response.status(Status.BAD_REQUEST).build();
		}

		// see if this matter already exists
		Matter matterInstance = Matter.getByUuid(dbFacade, dto.getUuid());
		if (matterInstance != null) {
			log.warn("updateMatter: Exisiting Matter found for UUID : " + dto.getUuid());

			// check authorization
			if (!matterInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
				log.warn("updateMatter: Matter does not belong to this organistation.");
				return Response.status(Status.FORBIDDEN).build();
			}

		} else {
			matterInstance = new Matter();
			matterInstance = mapper.getMatterFromDTO(dto, matterInstance, new CycleAvoidingMappingContext(user));
			matterInstance.setCreatedby(user); // set current user as the creator

			matterInstance.setCreateddate(new Date());
			matterInstance.setDescription(dto.getDescription());
			matterInstance.setName(dto.getName());
			matterInstance.setReference(dto.getReference());
			matterInstance.setType(MatterTypeEnum.GENERIC);
			matterInstance.setUuid(dto.getUuid());
			matterInstance.setArchive(false);
			dbFacade.persist(matterInstance);

			matterInstance.setMergefields(new ArrayList<>());

			// add all mergefieldtemplates as mergefields
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("organisationId", user.getOrganisation().getId());

			String sql = Mergefieldtemplate.getListSQL(Mergefieldtemplate.class, requestMap);
			Map<String, Object> params = Mergefieldtemplate.getListParams(requestMap, user);

			Integer startPosition = null;
			Integer maxResults = null;

			Integer page = (Integer) requestMap.get("page");
			Integer rowsPerPage = (Integer) requestMap.get("rowsPerPage");

			if (page != null && rowsPerPage != null) {
				maxResults = rowsPerPage;
				startPosition = page * rowsPerPage;
			}

			List<Mergefieldtemplate> mergefieldtemplates = dbFacade.getEntityList(Mergefieldtemplate.class, sql, params, false, startPosition, maxResults);
			if (Utils.isNotEmpty(mergefieldtemplates)) {
				for (Mergefieldtemplate thisMFT : mergefieldtemplates) {
					Mergefield newMF = new Mergefield(thisMFT, matterInstance);
					dbFacade.persist(newMF);
				}
			}

		}

		MatterDTO responseDTO = mapper.getDTOFromMatter(matterInstance, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("createMatter: End");
		return response;

	}

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@Path("/{uuid}")
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Update Matter", description = "Update an existing Matter", tags = {
			"Matter" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Matter", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatterDTO.class))),
					@ApiResponse(responseCode = "304", description = "Matter not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Matter has been modified by another user") })
	public Response updateMatter(@Context HttpHeaders headers, @PathParam("uuid") final String uuid, final MatterDTO dto) throws ConflictException {
		log.info("updateMatter: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("updateMatter: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Matter matterInstance = Matter.getByUuid(dbFacade, uuid);
		if (matterInstance == null) {
			log.warn("updateMatter: No Matter found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		if (!matterInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateMatter: Matter does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		// OK to return
		Matter updatedMatter = mapper.getMatterFromDTO(dto, matterInstance, new CycleAvoidingMappingContext(user));
		updatedMatter = dbFacade.merge(updatedMatter);

		MatterDTO responseDTO = mapper.getDTOFromMatter(updatedMatter, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("updateMatter: End");
		return response;

	}

	@DELETE
	@Path("/{uuid}")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Delete Matter", description = "Delete an existing Matter", tags = {
			"Matter" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "200", description = "Matter deleted"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Matter not modified (updated)") })
	public Response deleteMatter(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) throws ConflictException {
		log.info("deleteMatter: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("deleteMatter: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Matter matterInstance = Matter.getByUuid(dbFacade, uuid);
		if (matterInstance == null) {
			log.warn("deleteMatter: No Matter found for ID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		if (!matterInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("deleteMatter: Matter does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		// OK to delete
		matterInstance.setArchive(true);
		dbFacade.merge(matterInstance);

		Response response = Response.ok().build();
		log.info("deleteMatter: End");

		return response;
	}

}
