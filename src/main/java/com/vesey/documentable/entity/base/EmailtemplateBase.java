package com.vesey.documentable.entity.base;

// Generated 24 Jan 2023, 11:06:04 by Hibernate Tools 5.2.13.Final (Modified by RJC)

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

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
 * EmailtemplateBase - This file has been auto-generated - do not edit (changes will be overwritten)
 */

@MappedSuperclass
@Table(name = "emailtemplate", catalog = "documentable")

public abstract class EmailtemplateBase extends BaseEntity implements java.io.Serializable {

	public static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)

	protected Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emailsection")
	protected Emailsection emailsection;

	@Column(name = "uuid", nullable = false, length = 36)
	protected String uuid;

	@Column(name = "description", nullable = false, length = 200)
	protected String description;

	@Column(name = "content", length = 16777215)
	protected String content;

	public EmailtemplateBase() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Emailsection getEmailsection() {
		return this.emailsection;
	}

	public void setEmailsection(Emailsection emailsection) {
		this.emailsection = emailsection;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
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
		if (!(other instanceof com.vesey.documentable.entity.base.EmailtemplateBase)) {
			return false;
		}

		com.vesey.documentable.entity.base.EmailtemplateBase castOther = (com.vesey.documentable.entity.base.EmailtemplateBase) other;

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
