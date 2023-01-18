package com.vesey.documentable.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.OrderBy;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.DocumenttemplateBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Documenttemplate extends DocumenttemplateBase {

	public static final long serialVersionUID = 1L;

	public Documenttemplate() {
		this.numbered = true;

	}

	@OneToMany(mappedBy = "documenttemplate")
	@OrderBy(clause = "sortorder ASC")
	private Collection<Snippettemplate> snippettemplates;

	@JsonIgnore
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Documenttemplate user) {
		return null;
	}

	@JsonIgnore
	@Transient
	public static Documenttemplate getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Documenttemplate.class, id);
	}

	@JsonIgnore
	@Transient
	public static Documenttemplate getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Documenttemplate.class, "select Object(x) from Documenttemplate x where uuid = :uuid", params, false);
	}

	@JsonIgnore
	@Transient
	public static String getListSQL(Class<?> type, Map<String, Object> requestMap) {
		Integer userId = (Integer) requestMap.get("userId");
		Integer organisationId = (Integer) requestMap.get("organisationId");

		String sql = "SELECT DISTINCT Object(x) FROM Documenttemplate x "
				+ "JOIN x.createdby u "
				+ "JOIN u.organisation o "
				+ "WHERE x.id IS NOT NULL "
				+ "AND x.active = 1 ";

		if (Utils.isNotEmpty(userId) && Utils.isNotEmpty(organisationId)) {
			sql += "AND u.id = :userId "
					+ "OR o.id = :organisationId";

		}

		return sql;
	}

	@JsonIgnore
	@Transient
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Users user) {

		Integer userId = (Integer) requestMap.get("userId");
		Integer organisationId = (Integer) requestMap.get("organisationId");

		HashMap<String, Object> map = new HashMap<>();

		if (Utils.isNotEmpty(userId) && Utils.isNotEmpty(organisationId)) {
			map.put("userId", userId);
			map.put("organisationId", organisationId);
		}

		return map;
	}

	@JsonIgnore
	@Transient
	public static Collection<Documenttemplate> getAll(DBFacade dbFacade, String orderBy) {
		String sql = "select Object(x) from Documenttemplate x ";
		if (Utils.isNotEmpty(orderBy)) {
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(Documenttemplate.class, sql, null, false);
	}

	public Collection<Snippettemplate> getSnippettemplates() {
		return snippettemplates;
	}

	public void setSnippettemplates(Collection<Snippettemplate> snippettemplates) {
		this.snippettemplates = snippettemplates;
	}

	public static Documenttemplate findForSnippettemplate(DBFacade dbFacade, Snippettemplate snippet) {
		if (snippet.getDocumenttemplate() != null) {
			Documenttemplate fresh = Documenttemplate.getById(dbFacade, snippet.getDocumenttemplate().getId());

			return fresh;
		} else if (snippet.getParent() != null) {
			Snippettemplate parent = Snippettemplate.getById(dbFacade, snippet.getParent().getId());
			return findForSnippettemplate(dbFacade, parent);
		} else
			return null;
	}

}
