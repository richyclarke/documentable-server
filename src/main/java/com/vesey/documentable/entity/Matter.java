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
import com.vesey.documentable.entity.base.MatterBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Matter extends MatterBase {

	public static final long serialVersionUID = 1L;

	public Matter() {

	}

	@OneToMany(mappedBy = "matter")
	@OrderBy(clause = "id DESC")
	private Collection<Document> documents;

	@OneToMany(mappedBy = "matter")
	@OrderBy(clause = "id DESC")
	private Collection<Mergefield> mergefields;

	@JsonIgnore
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Matter user) {
		return null;
	}

	@JsonIgnore
	@Transient
	public static Matter getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Matter.class, id);
	}

	@JsonIgnore
	@Transient
	public static Matter getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Matter.class, "SELECT Object(x) FROM Matter x WHERE uuid = :uuid", params, false);
	}

	public static Matter getByOrganisationAndSourceId(DBFacade dbFacade, Integer organisationId, String sourceId) {
		Map<String, Object> params = new HashMap<>();
		params.put("sourceId", sourceId);
		params.put("organisationId", organisationId);

		String sql = "SELECT Object(x) FROM Matter x WHERE x.sourceid = :sourceId AND x.createdby.organisation.id = :organisationId";

		return dbFacade.getEntityQuery(Matter.class, sql, params, false);
	}

	@JsonIgnore
	@Transient
	public static String getListSQL(Class<?> type, Map<String, Object> requestMap) {
		Integer userId = (Integer) requestMap.get("userId");
		Integer organisationId = (Integer) requestMap.get("organisationId");

		String sql = "SELECT DISTINCT Object(x) FROM Matter x "
				+ "JOIN x.createdby u "
				+ "JOIN u.organisation o "
				+ "WHERE x.id IS NOT NULL "
				+ "AND x.archive = 0 ";

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
	public static Collection<Matter> getAll(DBFacade dbFacade, String orderBy) {
		String sql = "select Object(x) from Documenttemplate x ";
		if (Utils.isNotEmpty(orderBy)) {
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(Matter.class, sql, null, false);
	}

	public Collection<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(Collection<Document> documents) {
		this.documents = documents;
	}

	public Collection<Mergefield> getMergefields() {
		return mergefields;
	}

	public void setMergefields(Collection<Mergefield> mergefields) {
		this.mergefields = mergefields;
	}

}
