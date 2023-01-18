package com.vesey.documentable.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.OrderBy;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.SnippetBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Snippet extends SnippetBase {

	public static final long serialVersionUID = 1L;

	public Snippet() {
		this.setNumbered(true);

	}

	public Snippet(Snippettemplate st, Snippet parent, Document document, int sortorder) {
		this.content = st.getContent();
		this.description = st.getDescription();
		this.name = st.getName();
		this.snippettemplate = st;
		this.parent = parent;
		this.sortorder = sortorder;
		this.uuid = UUID.randomUUID().toString();
		this.document = document;
		this.numbered = true;
	}

	@OneToMany(mappedBy = "parent")
	@OrderBy(clause = "sortorder ASC, id ASC")
	private Collection<Snippet> snippets;

	@JsonIgnore
	@Transient
	public static Snippet getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Snippet.class, id);
	}

	@JsonIgnore
	@Transient
	public static Snippet getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Snippet.class, "select Object(x) from Snippet x where uuid = :uuid", params, false);
	}

	@JsonIgnore
	@Transient
	public static Collection<Snippet> getAll(DBFacade dbFacade, String orderBy) {
		String sql = "select Object(x) from Snippet x";
		if (Utils.isNotEmpty(orderBy)) {
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(Snippet.class, sql, null, false);
	}

	@JsonIgnore
	@Transient
	public static String getListSQL(Class<?> type, Map<String, Object> requestMap) {
		Integer documentUuid = (Integer) requestMap.get("documentId");

		String sql = "SELECT DISTINCT Object(x) FROM Snippet x "
				+ "JOIN x.snippettemplate st "
				+ "WHERE x.parent IS NULL ";

		if (Utils.isNotEmpty(documentUuid)) {
			sql += "AND x.document.id = :documentId "
					+ "ORDER by x.sortorder ASC ";

		}

		return sql;
	}

	@JsonIgnore
	@Transient
	public static Map<String, Object> getListParams(Map<String, Object> requestMap) {

		Integer documentId = (Integer) requestMap.get("documentId");

		HashMap<String, Object> map = new HashMap<>();

		if (Utils.isNotEmpty(documentId)) {
			map.put("documentId", documentId);
		}

		return map;
	}

	public Collection<Snippet> getSnippets() {
		return snippets;
	}

	public void setSnippets(Collection<Snippet> snippets) {
		this.snippets = snippets;
	}

	public static Collection<Snippet> getForDocument(DBFacade dbFacade, Integer documentId) {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("documentId", documentId);

		String sql = Snippet.getListSQL(Snippet.class, requestMap);
		Map<String, Object> params = Snippet.getListParams(requestMap);

		Integer startPosition = null;
		Integer maxResults = null;

		Integer page = (Integer) requestMap.get("page");
		Integer rowsPerPage = (Integer) requestMap.get("rowsPerPage");

		if (page != null && rowsPerPage != null) {
			maxResults = rowsPerPage;
			startPosition = page * rowsPerPage;
		}
		List<Snippet> instances = dbFacade.getEntityList(Snippet.class, sql, params, false, startPosition, maxResults);
		return instances;
	}

}
