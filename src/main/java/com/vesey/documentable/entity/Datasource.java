package com.vesey.documentable.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.DatasourceBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Datasource extends DatasourceBase {

	public static final long serialVersionUID = 1L;

	public Datasource() {

	}

	@JsonIgnore
	@Transient
	public static Datasource getByNameAndOrganisation(DBFacade dbFacade, String name, Integer organisationId) {

		Map<String, Object> map = new HashMap<>();
		map.put("organisationId", organisationId);
		map.put("name", name);

		String sql = getListSQL(map);

		Datasource entity = dbFacade.getEntityQuery(Datasource.class, sql, map, false);
		return entity;
	}

	@JsonIgnore
	@Transient
	public static List<Datasource> getByOrganisation(DBFacade dbFacade, Integer organisationId) {

		Map<String, Object> map = new HashMap<>();
		map.put("organisationId", organisationId);

		String sql = getListSQL(map);

		List<Datasource> entities = dbFacade.doListQuery(Datasource.class, sql, map, false);
		return entities;
	}

	// @JsonIgnore
	// @Transient
	// public static List<Datasource> getByOrganisationAndDocumenttemplate(DBFacade dbFacade, Integer organisationId, Integer documenttemplateId) {
	//
	// Map<String, Object> map = new HashMap<>();
	// map.put("organisationId", organisationId);
	// map.put("documenttemplateId", documenttemplateId);
	//
	// String sql = getListSQL(map);
	//
	// List<Datasource> entities = dbFacade.doListQuery(Datasource.class, sql, map, false);
	// return entities;
	// }

	@JsonIgnore
	@Transient
	public static String getListSQL(Map<String, Object> requestMap) {
		String name = (String) requestMap.get("name");
		Integer organisationId = (Integer) requestMap.get("organisationId");

		String sql = "SELECT DISTINCT Object(x) FROM Datasource x "
				+ "JOIN x.createdby u "
				+ "JOIN u.organisation o "
				+ "WHERE x.id IS NOT NULL ";

		if (Utils.isNotEmpty(organisationId)) {
			sql += "AND o.id = :organisationId ";
		}
		if (Utils.isNotEmpty(name)) {
			sql += "AND x.name LIKE :name ";
		}

		sql += "ORDER BY sortorder ASC ";

		return sql;
	}

	@JsonIgnore
	@Transient
	public static Datasource getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Datasource.class, id);
	}

	@JsonIgnore
	@Transient
	public static Datasource getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Datasource.class, "select Object(x) from Datasource x where uuid = :uuid", params, false);
	}

}
