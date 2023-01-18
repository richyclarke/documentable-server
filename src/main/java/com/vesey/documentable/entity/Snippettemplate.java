package com.vesey.documentable.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.SnippettemplateBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Snippettemplate extends SnippettemplateBase {

	public static final long serialVersionUID = 1L;

	public Snippettemplate() {
		active = true;
		numbered = true;
	}

	@OneToMany(mappedBy = "parent")
	@OrderBy(clause = "sortorder ASC, id ASC")
	@Where(clause = "active = true")
	private Collection<Snippettemplate> snippettemplates;

	@JsonIgnore
	@Transient
	public static String getListSQL(Class<?> type, Map<String, Object> requestMap) {
		String documenttemplateUuid = (String) requestMap.get("documenttemplateUuid");

		String sql = "SELECT DISTINCT Object(x) FROM Snippettemplate x "
				+ "WHERE x.parent IS NULL "
				+ "AND x.active = 1 ";

		if (Utils.isNotEmpty(documenttemplateUuid)) {
			sql += "AND x.documenttemplate.uuid = :documenttemplateUuid "
					+ "ORDER by x.sortorder ASC ";

		}

		return sql;
	}

	@JsonIgnore
	@Transient
	public static String getAllListSQL(Class<?> type, Map<String, Object> requestMap) {
		String documenttemplateUuid = (String) requestMap.get("documenttemplateUuid");

		String sql = "SELECT DISTINCT Object(x) FROM Snippettemplate x "
				+ "WHERE x.active = 1 ";

		if (Utils.isNotEmpty(documenttemplateUuid)) {
			sql += "AND x.documenttemplate.uuid = :documenttemplateUuid "
					+ "ORDER by x.sortorder ASC ";
		}

		return sql;
	}

	@JsonIgnore
	@Transient
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Users user) {

		String documenttemplateUuid = (String) requestMap.get("documenttemplateUuid");

		HashMap<String, Object> map = new HashMap<>();

		if (Utils.isNotEmpty(documenttemplateUuid)) {
			map.put("documenttemplateUuid", documenttemplateUuid);
		}

		return map;
	}

	@JsonIgnore
	@Transient
	public static Snippettemplate getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Snippettemplate.class, id);
	}

	@JsonIgnore
	@Transient
	public static Snippettemplate getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Snippettemplate.class, "select Object(x) from Snippettemplate x where uuid = :uuid", params, false);
	}

	@JsonIgnore
	@Transient
	public static Collection<Snippettemplate> getAll(DBFacade dbFacade, String orderBy) {
		String sql = "select Object(x) from Documenttemplate x";
		if (Utils.isNotEmpty(orderBy)) {
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(Snippettemplate.class, sql, null, false);
	}

	public Collection<Snippettemplate> getSnippettemplates() {
		return snippettemplates;
	}

	public void setSnippettemplates(Collection<Snippettemplate> snippettemplates) {
		this.snippettemplates = snippettemplates;
	}

	public static Snippettemplate findForRuleset(DBFacade dbFacade, Ruleset ruleset) {
		Map<String, Object> params = new HashMap<>();
		params.put("ruleset", ruleset);
		return dbFacade.getEntityQuery(Snippettemplate.class, "select Object(s) from Snippettemplate s "
				+ "WHERE s.ruleset = :ruleset", params, false);
	}

}
