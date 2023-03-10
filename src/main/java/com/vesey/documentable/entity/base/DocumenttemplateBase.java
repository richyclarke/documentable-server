package com.vesey.documentable.entity.base;

// Generated 24 Jan 2023, 11:06:04 by Hibernate Tools 5.2.13.Final (Modified by RJC)

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
 * DocumenttemplateBase - This file has been auto-generated - do not edit (changes will be overwritten)
 */

@MappedSuperclass
@Table(name = "documenttemplate", catalog = "documentable")

public abstract class DocumenttemplateBase extends BaseEntity implements java.io.Serializable {

	public static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)

	protected Integer id;

	@Version
	@Column(name = "version", nullable = false)
	protected Integer version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "createdby", nullable = false)
	protected Users createdby;

	@Column(name = "uuid", nullable = false, length = 36)
	protected String uuid;

	@Column(name = "name", nullable = false, length = 100)
	protected String name;

	@Column(name = "description", length = 500)
	protected String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createddate", nullable = false, length = 19)
	protected Date createddate;

	@Column(name = "active", nullable = false)
	protected boolean active;

	@Column(name = "numbered", nullable = false)
	protected boolean numbered;

	@Column(name = "headerleftcontent", length = 65535)
	protected String headerleftcontent;

	@Column(name = "headercentercontent", length = 65535)
	protected String headercentercontent;

	@Column(name = "headerrightcontent", length = 65535)
	protected String headerrightcontent;

	@Column(name = "footerleftcontent", length = 65535)
	protected String footerleftcontent;

	@Column(name = "footercentercontent", length = 65535)
	protected String footercentercontent;

	@Column(name = "footerrightcontent", length = 65535)
	protected String footerrightcontent;

	@Column(name = "covercontent", length = 65535)
	protected String covercontent;

	@Column(name = "indexcontent", length = 65535)
	protected String indexcontent;

	@Column(name = "headeroncoverpage", nullable = false)
	protected boolean headeroncoverpage;

	@Column(name = "headeroncontentspage", nullable = false)
	protected boolean headeroncontentspage;

	@Column(name = "footeroncoverpage", nullable = false)
	protected boolean footeroncoverpage;

	@Column(name = "footeroncontentspage", nullable = false)
	protected boolean footeroncontentspage;

	public DocumenttemplateBase() {
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

	public Date getCreateddate() {
		return this.createddate;
	}

	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
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

	public String getHeaderleftcontent() {
		return this.headerleftcontent;
	}

	public void setHeaderleftcontent(String headerleftcontent) {
		this.headerleftcontent = headerleftcontent;
	}

	public String getHeadercentercontent() {
		return this.headercentercontent;
	}

	public void setHeadercentercontent(String headercentercontent) {
		this.headercentercontent = headercentercontent;
	}

	public String getHeaderrightcontent() {
		return this.headerrightcontent;
	}

	public void setHeaderrightcontent(String headerrightcontent) {
		this.headerrightcontent = headerrightcontent;
	}

	public String getFooterleftcontent() {
		return this.footerleftcontent;
	}

	public void setFooterleftcontent(String footerleftcontent) {
		this.footerleftcontent = footerleftcontent;
	}

	public String getFootercentercontent() {
		return this.footercentercontent;
	}

	public void setFootercentercontent(String footercentercontent) {
		this.footercentercontent = footercentercontent;
	}

	public String getFooterrightcontent() {
		return this.footerrightcontent;
	}

	public void setFooterrightcontent(String footerrightcontent) {
		this.footerrightcontent = footerrightcontent;
	}

	public String getCovercontent() {
		return this.covercontent;
	}

	public void setCovercontent(String covercontent) {
		this.covercontent = covercontent;
	}

	public String getIndexcontent() {
		return this.indexcontent;
	}

	public void setIndexcontent(String indexcontent) {
		this.indexcontent = indexcontent;
	}

	public boolean isHeaderoncoverpage() {
		return this.headeroncoverpage;
	}

	public void setHeaderoncoverpage(boolean headeroncoverpage) {
		this.headeroncoverpage = headeroncoverpage;
	}

	public boolean isHeaderoncontentspage() {
		return this.headeroncontentspage;
	}

	public void setHeaderoncontentspage(boolean headeroncontentspage) {
		this.headeroncontentspage = headeroncontentspage;
	}

	public boolean isFooteroncoverpage() {
		return this.footeroncoverpage;
	}

	public void setFooteroncoverpage(boolean footeroncoverpage) {
		this.footeroncoverpage = footeroncoverpage;
	}

	public boolean isFooteroncontentspage() {
		return this.footeroncontentspage;
	}

	public void setFooteroncontentspage(boolean footeroncontentspage) {
		this.footeroncontentspage = footeroncontentspage;
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
		if (!(other instanceof com.vesey.documentable.entity.base.DocumenttemplateBase)) {
			return false;
		}

		com.vesey.documentable.entity.base.DocumenttemplateBase castOther = (com.vesey.documentable.entity.base.DocumenttemplateBase) other;

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
