package com.vesey.documentable.entity.base;

// Generated 24 Jan 2023, 11:06:04 by Hibernate Tools 5.2.13.Final (Modified by RJC)

import com.vesey.documentable.enums.FormDataTypeEnum;
import java.math.BigDecimal;
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
 * MergefieldtemplateBase - This file has been auto-generated - do not edit (changes will be overwritten)
 */

@MappedSuperclass
@Table(name = "mergefieldtemplate", catalog = "documentable")

public abstract class MergefieldtemplateBase extends BaseEntity implements java.io.Serializable {

	public static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)

	protected Integer id;

	@Version
	@Column(name = "version", nullable = false)
	protected Integer version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "datasource", nullable = false)
	protected Datasource datasource;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "documenttemplate")
	protected Documenttemplate documenttemplate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "createdby", nullable = false)
	protected Users createdby;

	@Column(name = "uuid", nullable = false, length = 36)
	protected String uuid;

	@Column(name = "key", nullable = false, length = 100)
	protected String key;

	@Column(name = "label", nullable = false, length = 100)
	protected String label;

	@Enumerated(EnumType.STRING)

	@Column(name = "type", nullable = false, length = 45)
	protected FormDataTypeEnum type;

	@Column(name = "description", length = 65535)
	protected String description;

	@Column(name = "source", length = 45)
	protected String source;

	@Column(name = "samplestringvalue", length = 65535)
	protected String samplestringvalue;

	@Column(name = "sampleintegervalue")
	protected Integer sampleintegervalue;

	@Column(name = "sampledecimalvalue", precision = 15, scale = 4)
	protected BigDecimal sampledecimalvalue;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sampledatevalue", length = 19)
	protected Date sampledatevalue;

	@Column(name = "samplebooleanvalue")
	protected Boolean samplebooleanvalue;

	@Column(name = "deleted", nullable = false)
	protected boolean deleted;

	public MergefieldtemplateBase() {
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

	public Datasource getDatasource() {
		return this.datasource;
	}

	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}

	public Documenttemplate getDocumenttemplate() {
		return this.documenttemplate;
	}

	public void setDocumenttemplate(Documenttemplate documenttemplate) {
		this.documenttemplate = documenttemplate;
	}

	public Users getCreatedby() {
		return this.createdby;
	}

	public void setCreatedby(Users createdby) {
		this.createdby = createdby;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public FormDataTypeEnum getType() {
		return this.type;
	}

	public void setType(FormDataTypeEnum type) {
		this.type = type;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSamplestringvalue() {
		return this.samplestringvalue;
	}

	public void setSamplestringvalue(String samplestringvalue) {
		this.samplestringvalue = samplestringvalue;
	}

	public Integer getSampleintegervalue() {
		return this.sampleintegervalue;
	}

	public void setSampleintegervalue(Integer sampleintegervalue) {
		this.sampleintegervalue = sampleintegervalue;
	}

	public BigDecimal getSampledecimalvalue() {
		return this.sampledecimalvalue;
	}

	public void setSampledecimalvalue(BigDecimal sampledecimalvalue) {
		this.sampledecimalvalue = sampledecimalvalue;
	}

	public Date getSampledatevalue() {
		return this.sampledatevalue;
	}

	public void setSampledatevalue(Date sampledatevalue) {
		this.sampledatevalue = sampledatevalue;
	}

	public Boolean getSamplebooleanvalue() {
		return this.samplebooleanvalue;
	}

	public void setSamplebooleanvalue(Boolean samplebooleanvalue) {
		this.samplebooleanvalue = samplebooleanvalue;
	}

	public boolean isDeleted() {
		return this.deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
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
		if (!(other instanceof com.vesey.documentable.entity.base.MergefieldtemplateBase)) {
			return false;
		}

		com.vesey.documentable.entity.base.MergefieldtemplateBase castOther = (com.vesey.documentable.entity.base.MergefieldtemplateBase) other;

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
