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
 * SnippettemplateBase - This file has been auto-generated - do not edit (changes will be overwritten)
 */

@MappedSuperclass
@Table(name = "snippettemplate", catalog = "documentable")

public abstract class SnippettemplateBase extends BaseEntity implements java.io.Serializable {

	public static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)

	protected Integer id;

	@Version
	@Column(name = "version", nullable = false)
	protected Integer version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "documenttemplate", nullable = false)
	protected Documenttemplate documenttemplate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ruleset")
	protected Ruleset ruleset;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	protected Snippettemplate parent;

	@Column(name = "uuid", nullable = false, length = 36)
	protected String uuid;

	@Column(name = "name", nullable = false, length = 100)
	protected String name;

	@Column(name = "description", length = 500)
	protected String description;

	@Column(name = "content", nullable = false, length = 16777215)
	protected String content;

	@Column(name = "sortorder", nullable = false)
	protected Integer sortorder;

	@Column(name = "active", nullable = false)
	protected boolean active;

	@Column(name = "numbered", nullable = false)
	protected boolean numbered;

	public SnippettemplateBase() {
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

	public Documenttemplate getDocumenttemplate() {
		return this.documenttemplate;
	}

	public void setDocumenttemplate(Documenttemplate documenttemplate) {
		this.documenttemplate = documenttemplate;
	}

	public Ruleset getRuleset() {
		return this.ruleset;
	}

	public void setRuleset(Ruleset ruleset) {
		this.ruleset = ruleset;
	}

	public Snippettemplate getParent() {
		return this.parent;
	}

	public void setParent(Snippettemplate parent) {
		this.parent = parent;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Integer getSortorder() {
		return this.sortorder;
	}

	public void setSortorder(Integer sortorder) {
		this.sortorder = sortorder;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isNumbered() {
		return this.numbered;
	}

	public void setNumbered(boolean numbered) {
		this.numbered = numbered;
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
		if (!(other instanceof com.vesey.documentable.entity.base.SnippettemplateBase)) {
			return false;
		}

		com.vesey.documentable.entity.base.SnippettemplateBase castOther = (com.vesey.documentable.entity.base.SnippettemplateBase) other;

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
