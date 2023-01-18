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
import com.vesey.documentable.entity.base.MergefieldtemplateBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Mergefieldtemplate extends MergefieldtemplateBase {

	public static final long serialVersionUID = 1L;

	public Mergefieldtemplate() {

	}

	@OneToMany(mappedBy = "mergefieldtemplate")
	@OrderBy(clause = "sortorder DESC")
	private Collection<Mergefieldoption> options;

	@JsonIgnore
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Mergefieldtemplate user) {
		return null;
	}

	@JsonIgnore
	@Transient
	public static Mergefieldtemplate getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Mergefieldtemplate.class, id);
	}

	@JsonIgnore
	@Transient
	public static Mergefieldtemplate getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Mergefieldtemplate.class, "select Object(x) from Mergefieldtemplate x where uuid = :uuid", params, false);
	}

	public static Mergefieldtemplate getByKey(DBFacade dbFacade, String key, Integer organistationId) {
		Map<String, Object> params = new HashMap<>();
		params.put("key", key);
		params.put("organistationId", organistationId);

		String sql = "SELECT DISTINCT Object(x) FROM Mergefieldtemplate x "
				+ "JOIN x.createdby u "
				+ "WHERE x.uuid IS NOT NULL ";

		if (organistationId != null) {
			sql += "AND u.organisation.id = :organistationId ";
		}

		if (key != null) {
			sql += "AND x.key = :key ";
		}

		return dbFacade.getEntityQuery(Mergefieldtemplate.class, sql, params, false);
	}

	@JsonIgnore
	@Transient
	public static String getListSQL(Class<?> type, Map<String, Object> requestMap) {
		Integer userId = (Integer) requestMap.get("userId");
		Integer organisationId = (Integer) requestMap.get("organisationId");

		String sql = "SELECT DISTINCT Object(x) FROM Mergefieldtemplate x "
				+ "JOIN x.createdby u "
				+ "WHERE x.uuid IS NOT NULL ";

		if (Utils.isNotEmpty(userId)) {
			sql += "AND u.id = :userId ";
		}
		if (Utils.isNotEmpty(organisationId)) {
			sql += "AND u.organisation.id = :organisationId ";
		}

		return sql;
	}

	@JsonIgnore
	@Transient
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Users user) {

		Integer userId = (Integer) requestMap.get("userId");
		Integer organisationId = (Integer) requestMap.get("organisationId");

		HashMap<String, Object> map = new HashMap<>();
		if (Utils.isNotEmpty(userId)) {
			map.put("userId", userId);
		}
		if (Utils.isNotEmpty(organisationId)) {
			map.put("organisationId", organisationId);
		}

		return map;
	}

	public Collection<Mergefieldoption> getOptions() {
		return options;
	}

	public void setOptions(Collection<Mergefieldoption> options) {
		this.options = options;
	}

}