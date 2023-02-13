package com.vesey.documentable.entity.base;

// Generated 24 Jan 2023, 11:06:04 by Hibernate Tools 5.2.13.Final (Modified by RJC)

import com.vesey.documentable.enums.RoleTypeEnum;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.*;
import javax.persistence.Column;
import com.vesey.documentable.session.DBFacade;
import javax.persistence.Transient;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Collection;
import com.vesey.documentable.utils.Utils;

@SuppressWarnings("unused")

/**
 * UsersBase - This file has been auto-generated - do not edit (changes will be overwritten)
 */

@MappedSuperclass
@Table(name = "users", catalog = "documentable")

public abstract class UsersBase extends BaseEntity implements java.io.Serializable {

	public static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)

	protected Integer id;

	@Version
	@Column(name = "version", nullable = false)
	protected Integer version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organisation")
	protected Organisation organisation;

	@Column(name = "uuid", nullable = false, length = 36)
	protected String uuid;

	@Column(name = "username", nullable = false, length = 45)
	protected String username;

	@Column(name = "hashedpassword", length = 200)
	protected String hashedpassword;

	@Column(name = "password", length = 45)
	protected String password;

	@Column(name = "email", length = 200)
	protected String email;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "registrationdate", length = 19)
	protected Date registrationdate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lastloggedindate", length = 19)
	protected Date lastloggedindate;

	@Column(name = "token", length = 45)
	protected String token;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lastupdatedate", length = 19)
	protected Date lastupdatedate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "tokenvalidfrom", length = 19)
	protected Date tokenvalidfrom;

	@Column(name = "passwordresetuuid", length = 45)
	protected String passwordresetuuid;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "passwordresetexpiry", length = 19)
	protected Date passwordresetexpiry;

	@Column(name = "loginattempts")
	protected Integer loginattempts;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lastloginattempt", length = 19)
	protected Date lastloginattempt;

	@Column(name = "profilepicturefilename", length = 200)
	protected String profilepicturefilename;

	@Column(name = "name", length = 200)
	protected String name;

	@Column(name = "activated", nullable = false)
	protected boolean activated;

	@Column(name = "superuser", nullable = false)
	protected boolean superuser;

	@Enumerated(EnumType.STRING)

	@Column(name = "role", length = 45)
	protected RoleTypeEnum role;

	public UsersBase() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Organisation getOrganisation() {
		return this.organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHashedpassword() {
		return this.hashedpassword;
	}

	public void setHashedpassword(String hashedpassword) {
		this.hashedpassword = hashedpassword;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getRegistrationdate() {
		return this.registrationdate;
	}

	public void setRegistrationdate(Date registrationdate) {
		this.registrationdate = registrationdate;
	}

	public Date getLastloggedindate() {
		return this.lastloggedindate;
	}

	public void setLastloggedindate(Date lastloggedindate) {
		this.lastloggedindate = lastloggedindate;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getLastupdatedate() {
		return this.lastupdatedate;
	}

	public void setLastupdatedate(Date lastupdatedate) {
		this.lastupdatedate = lastupdatedate;
	}

	public Date getTokenvalidfrom() {
		return this.tokenvalidfrom;
	}

	public void setTokenvalidfrom(Date tokenvalidfrom) {
		this.tokenvalidfrom = tokenvalidfrom;
	}

	public String getPasswordresetuuid() {
		return this.passwordresetuuid;
	}

	public void setPasswordresetuuid(String passwordresetuuid) {
		this.passwordresetuuid = passwordresetuuid;
	}

	public Date getPasswordresetexpiry() {
		return this.passwordresetexpiry;
	}

	public void setPasswordresetexpiry(Date passwordresetexpiry) {
		this.passwordresetexpiry = passwordresetexpiry;
	}

	public Integer getLoginattempts() {
		return this.loginattempts;
	}

	public void setLoginattempts(Integer loginattempts) {
		this.loginattempts = loginattempts;
	}

	public Date getLastloginattempt() {
		return this.lastloginattempt;
	}

	public void setLastloginattempt(Date lastloginattempt) {
		this.lastloginattempt = lastloginattempt;
	}

	public String getProfilepicturefilename() {
		return this.profilepicturefilename;
	}

	public void setProfilepicturefilename(String profilepicturefilename) {
		this.profilepicturefilename = profilepicturefilename;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActivated() {
		return this.activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public boolean isSuperuser() {
		return this.superuser;
	}

	public void setSuperuser(boolean superuser) {
		this.superuser = superuser;
	}

	public RoleTypeEnum getRole() {
		return this.role;
	}

	public void setRole(RoleTypeEnum role) {
		this.role = role;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
		buffer.append("id").append("='").append(getId()).append("' ");
		buffer.append("]");

		return buffer.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof com.vesey.documentable.entity.base.UsersBase)) {
			return false;
		}

		com.vesey.documentable.entity.base.UsersBase castOther = (com.vesey.documentable.entity.base.UsersBase) other;

		return ((this.getId() == castOther.getId())
				|| (this.getId() != null && castOther.getId() != null && this.getId().equals(castOther.getId())));

	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + (getId() == null ? 0 : this.getId().hashCode());
		return result;
	}

}
