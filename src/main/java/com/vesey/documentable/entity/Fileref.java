package com.vesey.documentable.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.FilerefBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity

public class Fileref extends FilerefBase {

	public static final long serialVersionUID = 1L;

	public Fileref() {

	}

	@JsonIgnore
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Users user) {
		return null;
	}

	@JsonIgnore
	@Transient
	public static Fileref getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Fileref.class, id);
	}

	@JsonIgnore
	@Transient
	public static Fileref getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Fileref.class, "select Object(x) from Fileref x where uuid = :uuid ", params, false);
	}

	public static Fileref getByFilename(DBFacade dbFacade, String fullpath) {
		Map<String, Object> params = new HashMap<>();

		return dbFacade.getEntityQuery(Fileref.class, "SELECT Object(f) FROM Fileref f "
				+ "WHERE f.filename like '%" + fullpath + "%'", params, false);

	}

	@JsonIgnore
	@Transient
	public static Collection<Fileref> getAll(DBFacade dbFacade, String orderBy) {
		String sql = "select Object(x) from Fileref x";
		if (Utils.isNotEmpty(orderBy)) {
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(Fileref.class, sql, null, false);
	}

	// public String getFileURL() {
	// if (trip != null && Utils.isNotEmpty(filename)) {
	// return Utils.generateAttachmentsImageURL(trip.getUuid(), filename);
	// }
	// return null;
	// }
	//
	// public String getThumbnailURL() {
	// if (trip != null && Utils.isNotEmpty(thumbnailfilename)) {
	// return Utils.generateAttachmentsImageURL(trip.getUuid(), getThumbnailfilename());
	// }
	// return null;
	// }

}
