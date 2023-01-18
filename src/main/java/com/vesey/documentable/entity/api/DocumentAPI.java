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
import com.vesey.documentable.entity.Document;
import com.vesey.documentable.entity.Documenttemplate;
import com.vesey.documentable.entity.Matter;
import com.vesey.documentable.entity.Snippet;
import com.vesey.documentable.entity.Snippettemplate;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.BaseEntityDTO;
import com.vesey.documentable.entity.dto.DocumentDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.DocumentMapperImpl;
import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.security.Logged;
import com.vesey.documentable.security.NoCache;
import com.vesey.documentable.security.Secured;
import com.vesey.documentable.session.AuthFacade;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.session.DocumentBean;
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
@Path("/document")
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class DocumentAPI {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	@Inject
	AuthFacade authFacade;
	@Inject
	RestFacade restFacade;
	@Inject
	DocumentBean documentBean;

	@Inject
	DocumentMapperImpl mapper;

	@GET
	@Path("/list/{matterUuid}")
	@Logged
	@NoCache
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Get Documents", description = "Get a list of Documents", tags = {
			"Document" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(description = "The list of Documents", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DocumentDTO.class)))) })
	public Response listDocuments(@Context HttpServletRequest request, @Context HttpHeaders headers, @PathParam("matterUuid") final String matterUuid) {
		log.info("listDocuments: Start. MATTER UUID : " + matterUuid);

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
			log.warn("listDocuments: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		// check authorization
		Matter matterInstance = Matter.getByUuid(dbFacade, matterUuid);
		if (matterInstance == null) {
			log.warn("listDocuments: No Matter found for Matter UUID : " + matterUuid);
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!matterInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("listDocuments: Matter does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("matterUuid", matterUuid);

		String sql = Document.getListSQL(Document.class, requestMap);
		Map<String, Object> params = Document.getListParams(requestMap, user);

		Integer startPosition = null;
		Integer maxResults = null;

		Integer page = (Integer) requestMap.get("page");
		Integer rowsPerPage = (Integer) requestMap.get("rowsPerPage");

		if (page != null && rowsPerPage != null) {
			maxResults = rowsPerPage;
			startPosition = page * rowsPerPage;
		}
		String result;
		List<Document> instances = dbFacade.getEntityList(Document.class, sql, params, false, startPosition, maxResults);

		List<DocumentDTO> dtos = new ArrayList<>();
		if (Utils.isNotEmpty(instances)) {
			for (Document thisDocument : instances) {
				dtos.add(mapper.getDTOFromDocument(thisDocument, new CycleAvoidingMappingContext(user)));
			}
		}
		result = Utils.convertToJSON(dtos);
		Response response = Response.ok(result).build();
		log.info("listDocuments: End");
		return response;
	}

	@GET
	@Path("/{uuid}")
	@Logged
	@Secured
	@NoCache
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get Document", description = "Get Document by id", tags = {
			"Document" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The Document", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "400", description = "Document not found")
			})
	public Response getDocumentByUuid(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) {
		log.info("getDocumentByUuid: Start ------ REST : GET DOCUMENT BY ID-----");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("getDocumentById: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Document documentInstance = Document.getByUuid(dbFacade, uuid);
		if (documentInstance == null) {
			log.warn("getDocumentByUuid: No Document found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		if (!documentInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateDocument: Document does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		BaseEntityDTO dto = mapper.getDTOFromDocument(documentInstance, new CycleAvoidingMappingContext(user));

		String result = Utils.convertToJSON(dto);
		Response response = Response.ok(result).build();
		// Response response = Response.status(Status.SERVICE_UNAVAILABLE).build();
		log.info("getDocumentByUuid: End");
		return response;
	}

	@GET
	@Path("/html/{documentUuid}")
	@Logged
	@Secured
	@NoCache
	@Produces(MediaType.TEXT_HTML)
	@Operation(summary = "Get Document HTML", description = "Get Document HTML by uuid", tags = {
			"Document" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The Document HTML", content = @Content(mediaType = "text/html", schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "400", description = "Document not found")
			})
	public Response getDocumentHTMLByUuid(@Context HttpHeaders headers, @PathParam("documentUuid") final String documentUuid) {
		log.info("getDocumentHTMLByUuid: Start ------ REST : GET DOCUMENT HTML BY UUID-----");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("getDocumentHTMLByUuid: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		Document documentInstance = Document.getByUuid(dbFacade, documentUuid);
		if (documentInstance == null) {
			log.warn("getDocumentHTMLByUuid: No Document found for UUID : " + documentUuid);
			return Response.status(Status.BAD_REQUEST).entity("No Document found for UUID : " + documentUuid).build();
		}

		// check authorization
		if (!documentInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("getDocumentHTMLByUuid: Document does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Document does not belong to this organistation.").build();
		}

		String html = documentBean.generateHTMLPreview(documentUuid);

		Response response = Response.ok(html).build();
		log.info("getDocumentHTMLByUuid: End");
		return response;
	}

	@POST
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Create Document", description = "Create a new Document", tags = {
			"Document" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The created Document", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentDTO.class))),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Document not modified (created)") })
	public Response createDocument(@Context HttpHeaders headers, final DocumentDTO dto) {
		log.info("createDocument: Start");
		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("createDocument: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found.").build();
		}

		if (dto == null) {
			log.warn("createDocument: DTO is null");
			return Response.status(Status.BAD_REQUEST).entity("DTO is null.").build();
		}

		// check document template exists
		Documenttemplate documenttemplateInstance = Documenttemplate.getByUuid(dbFacade, dto.getDocumenttemplateUuid());
		if (documenttemplateInstance == null) {
			log.warn("createDocument: No Documenttemplate found for Documenttemplate UUID : " + dto.getDocumenttemplateUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Documenttemplate found for Documenttemplate UUID : " + dto.getDocumenttemplateUuid()).build();
		}

		// check matter template exists
		Matter matterInstance = Matter.getByUuid(dbFacade, dto.getMatterUuid());
		if (matterInstance == null) {
			log.warn("createDocument: No Matter found for Matter UUID : " + dto.getMatterUuid());
			return Response.status(Status.BAD_REQUEST).entity("No Matter found for Matter UUID : " + dto.getMatterUuid()).build();
		}

		// check authorization

		if (!matterInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("createDocument: Matter does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Matter does not belong to this organistation.").build();
		}
		if (!documenttemplateInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("createDocument: Document template does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Document template does not belong to this organistation.").build();
		}

		// see if this document already exists
		Document documentInstance = Document.getByUuid(dbFacade, dto.getUuid());
		if (documentInstance != null) {
			log.warn("updateDocument: Exisiting Document found for UUID : " + dto.getUuid());
			return Response.status(Status.NOT_MODIFIED).entity("Existing document found.").build();
		} else {
			documentInstance = new Document();
			documentInstance = mapper.getDocumentFromDTO(dto, documentInstance, new CycleAvoidingMappingContext(user));
			documentInstance.setCreatedby(user);
			documentInstance.setCreateddate(new Date());
			documentInstance.setLastmodifiedby(user);
			documentInstance.setLastmodifieddate(new Date());
			documentInstance.setMatter(matterInstance);
			documentInstance.setDocumenttemplate(documenttemplateInstance);
			documentInstance.setNumbered(documenttemplateInstance.isNumbered());

			documentInstance.setSnippets(new ArrayList<>());
			dbFacade.persist(documentInstance);
			if (Utils.isNotEmpty(documenttemplateInstance.getSnippettemplates())) {
				for (Snippettemplate thisST : documenttemplateInstance.getSnippettemplates()) {
					if (thisST.isActive() && thisST.getParent() == null) {
						addSnippets(thisST, null, documentInstance, 1);
					}
				}
			}

		}
		DocumentDTO responseDTO = mapper.getDTOFromDocument(documentInstance, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();

		log.info("createDocument: End");
		return response;
	}

	public int addSnippets(Snippettemplate snippettemplate, Snippet parent, Document doc, int initialsortorder) {
		int sortorder = initialsortorder;
		if (snippettemplate.isActive()) {
			Snippet newSnippet = new Snippet(snippettemplate, parent, doc, sortorder);
			dbFacade.persist(newSnippet);

			log.info("addSnippets: added snippet : " + newSnippet.getId());

			sortorder++;
			// // now add children
			if (Utils.isNotEmpty(snippettemplate.getSnippettemplates())) {
				for (Snippettemplate thisST : snippettemplate.getSnippettemplates()) {
					sortorder = addSnippets(thisST, newSnippet, doc, sortorder);
				}
			}
		}
		return sortorder;
	}

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Update Document", description = "Update an existing Snippet ", tags = {
			"Document" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Document", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentDTO.class))),
					@ApiResponse(responseCode = "304", description = "Snippet  not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Snippet  has been modified by another user") })
	public Response updateDocument(@Context HttpHeaders headers, final DocumentDTO dto) throws ConflictException {
		log.info("updateDocument: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("updateDocument: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Document documentInstance = Document.getByUuid(dbFacade, dto.getUuid());
		if (documentInstance == null) {
			log.warn("updateDocument: No Document found for UUID : " + dto.getUuid());
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		Matter matterInstance = Matter.getByUuid(dbFacade, dto.getMatterUuid());
		if (matterInstance == null) {
			log.warn("updateDocument: No Matter found for Matter UUID : " + dto.getMatterUuid());
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!matterInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("updateDocument: Matter does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		// OK to return
		Document updatedDocument = mapper.getDocumentFromDTO(dto, documentInstance, new CycleAvoidingMappingContext(user));

		documentInstance.setLastmodifiedby(user);
		documentInstance.setLastmodifieddate(new Date());

		updatedDocument = dbFacade.merge(updatedDocument);

		DocumentDTO responseDTO = mapper.getDTOFromDocument(updatedDocument, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("updateDocument: End");
		return response;

	}

	@PUT
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@Path("/refresh/{uuid}")
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Refresh Document", description = "Refresh an existing Document ", tags = {
			"Document" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(description = "The updated Document", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentDTO.class))),
					@ApiResponse(responseCode = "304", description = "Document  not modified (updated)"),
					@ApiResponse(responseCode = "401", description = "Unauthorized (Token Expired)"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "409", description = "Document  has been modified by another user") })
	public Response refreshDocument(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) throws ConflictException {
		log.info("	");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("refreshDocument: User not found");
			return Response.status(Status.FORBIDDEN).entity("User not found").build();
		}

		Document documentInstance = Document.getByUuid(dbFacade, uuid);
		if (documentInstance == null) {
			log.warn("refreshDocument: No Document found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).entity("No Document found for UUID : " + uuid).build();
		}

		// check authorization
		if (!documentInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("refreshDocument: Document does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).entity("Document does not belong to this organistation.").build();
		}

		// OK to refresh
		if (Utils.isNotEmpty(documentInstance.getSnippets())) {
			for (Snippet thisSnippet : documentInstance.getSnippets()) {
				log.info("refreshDocument: deleting snippet : " + thisSnippet.getId());
				dbFacade.delete(thisSnippet);
			}
		}

		documentInstance.setSnippets(new ArrayList<>());

		if (Utils.isNotEmpty(documentInstance.getDocumenttemplate().getSnippettemplates())) {
			for (Snippettemplate thisST : documentInstance.getDocumenttemplate().getSnippettemplates()) {
				if (thisST.isActive() && thisST.getParent() == null) {
					addSnippets(thisST, null, documentInstance, 1);
				}
			}
		}

		documentInstance.setLastmodifiedby(user);
		documentInstance.setLastmodifieddate(new Date());

		documentInstance = dbFacade.merge(documentInstance);

		DocumentDTO responseDTO = mapper.getDTOFromDocument(documentInstance, new CycleAvoidingMappingContext(user));
		String result = Utils.convertToJSON(responseDTO);
		Response response = Response.ok(result).build();
		log.info("refreshDocument: End");
		return response;
	}

	@DELETE
	@Path("/{uuid}")
	@Logged
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@SecurityRequirement(name = "jwt")
	@Operation(summary = "Delete Document", description = "Delete an existing Document", tags = {
			"Document" }, security = @SecurityRequirement(name = "jwt"), responses = {
					@ApiResponse(responseCode = "200", description = "Document deleted"),
					@ApiResponse(responseCode = "403", description = "Forbidden"),
					@ApiResponse(responseCode = "304", description = "Document not modified (updated)") })
	public Response deleteDocument(@Context HttpHeaders headers, @PathParam("uuid") final String uuid) throws ConflictException {
		log.info("deleteDocument: Start");

		Users user = null;
		if (Utils.isNotEmpty(headers.getRequestHeader(HttpHeaders.AUTHORIZATION))) {
			String authorizationHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
			user = authFacade.getUserFromToken(authorizationHeader, dbFacade);
		}
		if (user == null) {
			log.warn("deleteDocument: User not found");
			return Response.status(Status.FORBIDDEN).build();
		}

		Document documentInstance = Document.getByUuid(dbFacade, uuid);
		if (documentInstance == null) {
			log.warn("deleteDocument: No Document found for UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}

		// check authorization
		Matter matterInstance = documentInstance.getMatter();
		if (matterInstance == null) {
			log.warn("deleteDocument: No Matter  found for Document with UUID : " + uuid);
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!matterInstance.getCreatedby().getOrganisation().equals(user.getOrganisation())) {
			log.warn("deleteDocument: Matter does not belong to this organistation.");
			return Response.status(Status.FORBIDDEN).build();
		}

		// OK to delete
		documentInstance.setDeleted(true);
		documentInstance.setArchived(true);
		dbFacade.merge(documentInstance);

		Response response = Response.ok().build();
		log.info("deleteDocument: End");
		return response;
	}

}
