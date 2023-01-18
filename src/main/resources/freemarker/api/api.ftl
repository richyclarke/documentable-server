package com.vesey.documentable.entity.api;
// Generated ${date} by Hibernate Tools ${version} (Modified by RJC)



<#assign classbody>
<#assign declarationName = pojo.importType(pojo.getDeclarationName())>/**
 * API object for domain model class ${declarationName}.
 * This file has been auto-generated - do not edit (changes will be overwritten)", 0)}
 * @see ${pojo.getQualifiedDeclarationName()}
 * @author Hibernate Tools
 */
<#if ejb3>
@${pojo.importType("javax.ejb.Stateless")}
</#if>
@SuppressWarnings("unused")
@Path("/${declarationName}")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class ${declarationName}API {

    @Inject
    Logger log;   

	@Inject
	DBFacade dbFacade;

	@Inject
	AuthFacade authFacade;

	@Inject
	${declarationName}MapperImpl mapper;

	public ${declarationName} get${declarationName}FromDTO(${declarationName}DTO dto, Users loggedInUser, MethodEnum method, ${declarationName} entity) throws ForbiddenException {

		// Only internal users can directly access entities
		switch (loggedInUser.getOrganisation().getOrgtype().getId()) {
		case Organisation.TYPE_INTERNAL:
			return mapper.get${declarationName}FromDTO(dto, entity);
		
		return null;
	}

	public ${declarationName}DTO getDTOFrom${declarationName}(Users loggedInUser, MethodEnum method, ${declarationName} entity) throws ForbiddenException {

		// Only internal users can directly access entities
		switch (loggedInUser.getOrganisation().getOrgtype().getId()) {
		case Organisation.TYPE_INTERNAL:
			return mapper.getDTOFrom${declarationName}(entity);
		return null;
	}




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
	@Operation(summary = "Get ${declarationName}", description = "Get ${declarationName} by id", 
    tags = {"${declarationName}"},
    extensions = {
    @Extension(properties = {
            @ExtensionProperty(name = "uc", value = "${declarationName?upper_case}"), @ExtensionProperty(name = "ucm", value = "GET_${declarationName?upper_case}")})},
    security = @SecurityRequirement(name = "jwt"),
    responses = {@ApiResponse(description = "The ${declarationName}", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ${declarationName}DTO.class))),
                 @ApiResponse(responseCode = "403", description = "Forbidden"),
				 @ApiResponse(responseCode = "404", description = "${declarationName} not found")})
	public Response get${declarationName}ById(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) {
	log.info("get${declarationName}ById: Start");
	
	String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
	com.vesey.documentable.entity.Users user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
	if (user == null) {
			log.warn("get${declarationName}ById: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		${declarationName} dbInstance = dbFacade.getEntity(${declarationName}.class, id);
		if (dbInstance == null) {
		log.warn("get${declarationName}ById: No ${declarationName} found for ID : " + id);
			return Response.status(Status.NOT_FOUND).build();
		}

		BaseEntityDTO dto = getDTOFrom${declarationName}(user, null, dbInstance);

		String result = Utils.convertToJSON(dto);
		Response response = Response.ok(result).build();
		log.info("get${declarationName}ById: End");
		return response;
	}
    
	@POST
	@Path("/list")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Get ${declarationName}s", description = "Get a list of ${declarationName}",
    tags = {"${declarationName}"},
    extensions = {
    @Extension(properties = {
            @ExtensionProperty(name = "uc", value = "${declarationName?upper_case}"), @ExtensionProperty(name = "ucm", value = "LIST_${declarationName?upper_case}S")})},
    security = @SecurityRequirement(name = "jwt"),
    responses = {@ApiResponse(responseCode = "403", description = "Forbidden"),
				@ApiResponse(description = "The ${declarationName} list",content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ${declarationName}DTO.class))))})
	public Response list${declarationName}s(@Context HttpServletRequest request, @Context HttpHeaders headers, Map<String, Object> requestMap)
		{
		log.info("list${declarationName}s: Start");
		
		String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
		com.vesey.documentable.entity.Users user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		if (user == null) {
			log.warn("list${declarationName}s: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}
	
		
		String sql = ${declarationName}.getListSQL(${declarationName}.class, requestMap);
		Map<String, Object> params = ${declarationName}.getListParams(requestMap, user);

		Integer startPosition = null;
		Integer maxResults = null;

		Integer page = (Integer) requestMap.get("page");
		Integer rowsPerPage = (Integer) requestMap.get("rowsPerPage");
		
		if(page!=null && rowsPerPage!=null)
		{
			maxResults = rowsPerPage;
			startPosition = page * rowsPerPage;
		}

		List<${declarationName}> instances = dbFacade.getEntityList(${declarationName}.class, sql, params, false, startPosition, maxResults);
		String result = Utils.convertToJSON(instances);
		Response response = Response.ok(result).build();
		log.info("list${declarationName}s: End");
		return response;
	}

	
	
    @POST
    @Logged
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Create ${declarationName}", description = "Create a new ${declarationName}",
    tags = {"${declarationName}"},
    extensions = {
    @Extension(properties = {
            @ExtensionProperty(name = "uc", value = "${declarationName?upper_case}"), @ExtensionProperty(name = "ucm", value = "CREATE_${declarationName?upper_case}")})},
    security = @SecurityRequirement(name = "jwt"),
    responses = {@ApiResponse(description = "The created ${declarationName}",content = @Content(mediaType = "application/json", schema = @Schema(implementation = ${declarationName}DTO.class))),
				@ApiResponse(responseCode = "403", description = "Forbidden"),
                 @ApiResponse(responseCode = "304", description = "${declarationName} not modified (created)")})
    public Response create${declarationName}(@Context HttpHeaders headers, final ${declarationName}DTO dto) {
	    log.info("create${declarationName}: Start");

		String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
		com.vesey.documentable.entity.Users user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		if (user == null) {
			log.warn("create${declarationName}: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

	
	    try
	    {
		    ${declarationName} instance = new ${declarationName}();
	    
   	    	instance = get${declarationName}FromDTO(dto, user, MethodEnum.CREATE, instance);

		   instance.preCreate(dto, dbFacade);
	    	    	    
		   dbFacade.persist(instance);
		    
	   	   instance.postCreate(dbFacade);
		   
		     BaseEntityDTO responseDTO = getDTOFrom${declarationName}(user, null, instance);

			String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		    log.info("create${declarationName}: End. Saved : " + instance);
		    return response;
	    }
	    catch(Exception e)
	    {
			log.error("create${declarationName}: Exception: ", e);
	        return Response.notModified(e.getLocalizedMessage()).build();
	    }
	}

	
    @PUT
    @Logged
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Update ${declarationName}", description = "Update an existing ${declarationName}",
    tags = {"${declarationName}"},
    extensions = {
    @Extension(properties = {
            @ExtensionProperty(name = "uc", value = "${declarationName?upper_case}"), @ExtensionProperty(name = "ucm", value = "UPDATE_${declarationName?upper_case}")})},
    security = @SecurityRequirement(name = "jwt"),
    responses = {@ApiResponse(description = "The updated ${declarationName}",content = @Content(mediaType = "application/json", schema = @Schema(implementation = ${declarationName}DTO.class))),
                 @ApiResponse(responseCode = "304", description = "${declarationName} not modified (updated)"),
				 @ApiResponse(responseCode = "403", description = "Forbidden"),
				 @ApiResponse(responseCode = "409", description = "${declarationName} has been modified by another user")})
    public Response update${declarationName}(@Context HttpHeaders headers, final ${declarationName}DTO dto) {
	    log.info("update${declarationName}: Start");

		String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
		com.vesey.documentable.entity.Users user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		if (user == null) {
			log.warn("update${declarationName}: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}
		
		${declarationName} dbInstance = dbFacade.getEntity(${declarationName}.class, dto.getId());
		if (dbInstance == null) {
		log.warn("get${declarationName}ById: No ${declarationName} found for ID : " + dto.getId());
			return Response.status(Status.NOT_FOUND).build();
		}

		
	    try
	    {
			dbInstance = get${declarationName}FromDTO(dto, user, MethodEnum.UPDATE, dbInstance);
	    
	     	dbInstance.preUpdate(dto, dbFacade);
	     
		    ${declarationName} refreshedInstance = dbFacade.merge(dbInstance);
		    //need to flush here to get latest version number
			dbFacade.flush();
			
			refreshedInstance.postUpdate(dto, dbFacade);
			
			BaseEntityDTO responseDTO = getDTOFrom${declarationName}(user, null, refreshedInstance);

			String result = Utils.convertToJSON(responseDTO);
			
			Response response = Response.ok(result).build();

		    log.info("update${declarationName}: End. Saved : " + refreshedInstance);
		    return response;
	    }
	    	catch(OptimisticLockException e)
	    {
			log.error("update${declarationName}: Exception: ", e);
	        return Response.status(Response.Status.CONFLICT).build();
	    }
 		catch(Exception e)
	    {
			log.error("update${declarationName}: Exception: ", e);
	        return Response.notModified(e.getLocalizedMessage()).build();
	    }		    
	}
	

	@DELETE
	@Path("/{uuid}")
    @Logged
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Delete ${declarationName}", description = "Delete an existing ${declarationName}",
    tags = {"${declarationName}"},
    extensions = {
    @Extension(properties = {
            @ExtensionProperty(name = "uc", value = "${declarationName?upper_case}"), @ExtensionProperty(name = "ucm", value = "DELETE_${declarationName?upper_case}")})},
    security = @SecurityRequirement(name = "jwt"),
    responses = {@ApiResponse(responseCode = "200", description = "${declarationName} deleted"), 
				 @ApiResponse(responseCode = "403", description = "Forbidden"),
                 @ApiResponse(responseCode = "304", description = "${declarationName} not modified (updated)")})
    public Response delete${declarationName}(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) {
	    log.info("delete${declarationName}: Start");

		String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
		com.vesey.documentable.entity.Users user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		if (user == null) {
			log.warn("delete${declarationName}: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}
		
		${declarationName} dbInstance = dbFacade.getEntity(${declarationName}.class, id);
		if (dbInstance == null) {
		log.warn("get${declarationName}ById: No ${declarationName} found for ID : " + id);
			return Response.status(Status.NOT_FOUND).build();
		}
		
	    try
	    {
	    
	    	dbInstance = get${declarationName}FromDTO(null, user, MethodEnum.DELETE, dbInstance);
	    
	     	dbInstance.preDelete(dbFacade);
	     	
		    dbFacade.delete(dbInstance);
		    
		    dbInstance.postDelete(dbFacade);
		    
		    Response response = Response.ok().build();
		    
		    log.info("delete${declarationName}: End. Deleted instance with id : " + id);
		    return response;
	    }
	    catch(Exception e)
	    {
			log.error("delete${declarationName}: Exception: ", e);
	        return Response.notModified(e.getLocalizedMessage()).build();
	    }	    
	}
	
	@POST
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Path("/count")
	@Operation(summary = "Get ${declarationName}s", description = "Get total count of ${declarationName}",
    tags = {"${declarationName}"},
    extensions = {
    @Extension(properties = {
            @ExtensionProperty(name = "uc", value = "${declarationName?upper_case}"), @ExtensionProperty(name = "ucm", value = "LIST_${declarationName?upper_case}S")})},
    security = @SecurityRequirement(name = "jwt"),
    responses = {@ApiResponse(responseCode = "403", description = "Forbidden"),
				@ApiResponse(description = "The ${declarationName} list count",content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class)))})
	public Response count${declarationName}s(@Context HttpServletRequest request, @Context HttpHeaders headers, Map<String, Object> requestMap)
		{
		log.info("count${declarationName}s: Start");


		String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
		com.vesey.documentable.entity.Users user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		if (user == null) {
			log.warn("count${declarationName}s: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

			String sql = ${declarationName}.getCountSQL(${declarationName}.class, requestMap);
		Map<String, Object> params = ${declarationName}.getListParams(requestMap, user);
		Integer count = dbFacade.getEntityListCount(sql, params, false);
		Response response = Response.ok(count).build();
		log.info("count${declarationName}s: End");
		return response;
	}
	
	@POST
    @Logged
    @Path("/create-file")
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Create ${declarationName} File", description = "Create a new ${declarationName} File",
    tags = {"${declarationName}"},
    extensions = {
    @Extension(properties = {
            @ExtensionProperty(name = "uc", value = "${declarationName?upper_case}"), @ExtensionProperty(name = "ucm", value = "UPLOAD_${declarationName?upper_case}_FILE")})},
    security = @SecurityRequirement(name = "jwt"),
    responses = {@ApiResponse(description = "The created ${declarationName} file",content = @Content(mediaType = "application/json", schema = @Schema(implementation = ${declarationName}DTO.class))),
				@ApiResponse(responseCode = "403", description = "Forbidden"),
                 @ApiResponse(responseCode = "304", description = "${declarationName} not modified (created)")})
    public Response upload${declarationName}File(@Context HttpServletRequest request, @Context HttpHeaders headers, @MultipartForm FileUploadDTO dto) {
	    log.info("upload${declarationName}File: Start");

		String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
		com.vesey.documentable.entity.Users user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		if (user == null) {
			log.warn("upload${declarationName}File: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}
		
		${declarationName} dbInstance = dbFacade.getEntity(${declarationName}.class, dto.getId());
		if (dbInstance == null) {
		log.warn("get${declarationName}ById: No ${declarationName} found for ID : " + dto.getId());
			return Response.status(Status.NOT_FOUND).build();
		}

	    try
	    {
	    return ${declarationName}.createFile(dto, user, dbFacade);
	    }
	    catch(Exception e)
	    {
			log.error("upload${declarationName}: Exception: ", e);
	        return Response.notModified(e.getLocalizedMessage()).build();
	    }
	}
	
	@GET
	@Path("/counts")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@Operation(
			summary = "Get Counts DTO for ${declarationName}s",
			description = "Get Counts DTO for ${declarationName}",
			tags = { "${declarationName}" },
			security = @SecurityRequirement(name = "jwt"),
			responses = { @ApiResponse(description = "The ${declarationName} Counts DTO", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountsDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "404", description = "${declarationName} counts not found") })					
	public Response get${declarationName}Counts(@Context HttpHeaders headers) {
		log.info("get${declarationName}Counts: Start");

		String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
		com.vesey.documentable.entity.Users user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		if (user == null) {
			log.warn("get${declarationName}Counts: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		

		CountsDTO counts = ${declarationName}.getCounts(dbFacade);

		if (counts == null) {
			log.warn("get${declarationName}Counts: No Counts found");
			return Response.status(Status.NO_CONTENT).build();
		}

		Response response = null;
		try {
			String result = Utils.convertToJSON(counts);
			response = Response.ok(result).build();
		} catch (Exception e) {
			log.error("get${declarationName}Counts: Exception: ", e);
			response = Response.notModified(e.getLocalizedMessage()).build();
		}
		log.info("get${declarationName}Counts: End");
		return response;
	}

    

}


</#assign>

${pojo.generateImports()}

import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ForbiddenException;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import com.vesey.documentable.entity.base.BaseEntity;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.Organisation;
import com.vesey.documentable.enums.*;
import com.vesey.documentable.entity.map.*;

import javax.persistence.OptimisticLockException;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.servlet.http.HttpServletRequest;
import org.jboss.logging.Logger;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Date;
import com.vesey.documentable.rest.dto.FileUploadDTO;
import com.vesey.documentable.security.Logged;
import com.vesey.documentable.security.Secured;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.session.AuthFacade;
import com.vesey.documentable.utils.Utils;
import com.vesey.documentable.entity.${declarationName};
import com.vesey.documentable.entity.dto.${declarationName}DTO;
import com.vesey.documentable.entity.dto.BaseEntityDTO;
import com.vesey.documentable.dto.CountsDTO;


${classbody}
