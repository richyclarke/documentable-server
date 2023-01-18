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
import com.vesey.documentable.entity.base.DocumentBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Document extends DocumentBase {

	public static final long serialVersionUID = 1L;

	public Document() {
		this.numbered = true;

	}

	@OneToMany(mappedBy = "document")
	@OrderBy(clause = "sortorder ASC, id ASC")
	private Collection<Snippet> snippets;

	@JsonIgnore
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Document user) {
		return null;
	}

	@JsonIgnore
	@Transient
	public static Document getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Document.class, id);
	}

	@JsonIgnore
	@Transient
	public static Document getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Document.class, "select Object(x) from Document x where uuid = :uuid", params, false);
	}

	@JsonIgnore
	@Transient
	public static String getListSQL(Class<?> type, Map<String, Object> requestMap) {
		String matterUuid = (String) requestMap.get("matterUuid");

		String sql = "SELECT DISTINCT Object(x) FROM Document x "
				+ "WHERE x.archived = 0 AND x.deleted = 0 ";

		if (Utils.isNotEmpty(matterUuid)) {
			sql += "AND x.matter.uuid = :matterUuid ";

		}

		return sql;
	}

	@JsonIgnore
	@Transient
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Users user) {

		String matterUuid = (String) requestMap.get("matterUuid");

		HashMap<String, Object> map = new HashMap<>();

		if (Utils.isNotEmpty(matterUuid)) {
			map.put("matterUuid", matterUuid);
		}

		return map;
	}

	public Collection<Snippet> getSnippets() {
		return snippets;
	}

	public void setSnippets(Collection<Snippet> snippets) {
		this.snippets = snippets;
	}

}
