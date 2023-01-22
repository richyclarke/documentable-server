package com.vesey.documentable.entity.base;

import static javax.persistence.GenerationType.IDENTITY;

// Generated 10 Jan 2023, 13:56:04 by Hibernate Tools 5.2.13.Final (Modified by RJC)

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Version;

import com.vesey.documentable.entity.Mergefieldtemplate;

@SuppressWarnings("unused")

/**
 * MergefieldoptionBase - This file has been auto-generated - do not edit (changes will be overwritten)
 */

@MappedSuperclass
@Table(name = "mergefieldoption", catalog = "documentable")

public abstract class MergefieldoptionBase extends BaseEntity implements java.io.Serializable {

	public static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)

	protected Integer id;

	@Version
	@Column(name = "version", nullable = false)
	protected Integer version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mergefieldtemplate", nullable = false)
	protected Mergefieldtemplate mergefieldtemplate;

	@Column(name = "uuid", nullable = false, length = 36)
	protected String uuid;

	@Column(name = "label", nullable = false, length = 100)
	protected String label;

	@Column(name = "sortorder", nullable = false)
	protected Integer sortorder;

	public MergefieldoptionBase() {
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

	public Mergefieldtemplate getMergefieldtemplate() {
		return this.mergefieldtemplate;
	}

	public void setMergefieldtemplate(Mergefieldtemplate mergefieldtemplate) {
		this.mergefieldtemplate = mergefieldtemplate;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getSortorder() {
		return this.sortorder;
	}

	public void setSortorder(Integer sortorder) {
		this.sortorder = sortorder;
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
		if (!(other instanceof com.vesey.documentable.entity.base.MergefieldoptionBase)) {
			return false;
		}

		com.vesey.documentable.entity.base.MergefieldoptionBase castOther = (com.vesey.documentable.entity.base.MergefieldoptionBase) other;

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
