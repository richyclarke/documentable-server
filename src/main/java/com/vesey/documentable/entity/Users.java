package com.vesey.documentable.entity;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.UsersBase;
import com.vesey.documentable.enums.RoleTypeEnum;
import com.vesey.documentable.rest.dto.RegistrationDTO;
import com.vesey.documentable.session.AuthFacade;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Users extends UsersBase {

	public static final long serialVersionUID = 1L;

	public Users() {

	}

	public Users(RegistrationDTO credentials) throws NoSuchAlgorithmException {
		version = 0;
		hashedpassword = AuthFacade.createHashedPassword(credentials.getPassword());
		email = credentials.getEmail();
		registrationdate = new Date();
		lastupdatedate = new Date();
		loginattempts = 0;
		name = credentials.getUsername();
		uuid = UUID.randomUUID().toString();
		role = RoleTypeEnum.CLIENT;
		activated = false;
	}

	@JsonIgnore
	public static String getListSQL(Map<String, Object> requestMap) {
		return "SELECT Object(x) FROM Users x ";
	}

	@JsonIgnore
	public static Map<String, Object> getListParams(Map<String, Object> requestMap) {
		return requestMap;
	}

	@JsonIgnore
	@Transient
	public static Users getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Users.class, id);
	}

	@JsonIgnore
	@Transient
	public static Users getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Users.class, "select Object(x) from Users x where uuid = :uuid", params, false);
	}

	@JsonIgnore
	@Transient
	public static Users getByPasswordResetUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Users.class, "select Object(x) from Users x where passwordresetuuid = :uuid", params, false);
	}

	@JsonIgnore
	@Transient
	public static Collection<Users> getAll(DBFacade dbFacade, String orderBy) {
		String sql = "select Object(x) from Users x";
		if (Utils.isNotEmpty(orderBy)) {
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(Users.class, sql, null, false);
	}

	public static Users getByUsername(String username, DBFacade dbFacade) {

		String sql = "SELECT Object(u) FROM Users u WHERE u.username = :username";
		Map<String, Object> params = new HashMap<>();
		params.put("username", username);
		return dbFacade.getEntityQuery(Users.class, sql, params, false);
	}

}
