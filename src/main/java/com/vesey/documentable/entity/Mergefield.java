package com.vesey.documentable.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.MergefieldBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Mergefield extends MergefieldBase {

	public static final long serialVersionUID = 1L;

	public Mergefield() {

	}

	public Mergefield(Mergefieldtemplate thisMFT, Matter matter) {
		this.matter = matter;
		this.mergefieldtemplate = thisMFT;
		this.uuid = UUID.randomUUID().toString();
	}

	@JsonIgnore
	@Transient
	public static Mergefield getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Mergefield.class, id);
	}

	@JsonIgnore
	@Transient
	public static Mergefield getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Mergefield.class, "select Object(x) from Mergefield x where uuid = :uuid", params, false);
	}

	@JsonIgnore
	@Transient
	public static Mergefield getByKeyAndMatter(DBFacade dbFacade, String key, Integer matterId) {

		Map<String, Object> map = new HashMap<>();
		map.put("matterId", matterId);
		map.put("key", key);

		String sql = getListSQL(map);

		Mergefield entity = dbFacade.getEntityQuery(Mergefield.class, sql, map, false);
		return entity;
	}

	public static Collection<Mergefield> getForMatter(DBFacade dbFacade, Integer matterId) {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("matterId", matterId);

		String sql = getListSQL(requestMap);

		Integer startPosition = null;
		Integer maxResults = null;

		Integer page = (Integer) requestMap.get("page");
		Integer rowsPerPage = (Integer) requestMap.get("rowsPerPage");

		if (page != null && rowsPerPage != null) {
			maxResults = rowsPerPage;
			startPosition = page * rowsPerPage;
		}

		List<Mergefield> instances = dbFacade.getEntityList(Mergefield.class, sql, requestMap, false, startPosition, maxResults);
		return instances;

	}

	@JsonIgnore
	@Transient
	public static String getListSQL(Map<String, Object> requestMap) {
		Integer matterId = (Integer) requestMap.get("matterId");

		String key = (String) requestMap.get("key");

		String sql = "SELECT DISTINCT Object(mf) FROM Mergefield mf "
				+ "JOIN mf.matter m "
				+ "JOIN mf.mergefieldtemplate mft "
				+ "WHERE mf.id IS NOT NULL ";

		if (Utils.isNotEmpty(matterId)) {
			sql += "AND m.id = :matterId ";
		}
		if (Utils.isNotEmpty(key)) {
			sql += "AND mft.key = :key ";
		}

		return sql;
	}

}
