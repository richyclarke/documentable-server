package com.vesey.documentable.entity.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Documenttemplate")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumenttemplateDTO extends BaseEntityDTO {

	private UserDTO createdby;
	private String name;
	private String description;
	private Date createddate;
	private boolean numbered;

	protected String headerleftcontent;
	protected String headercentercontent;
	protected String headerrightcontent;
	protected String footerleftcontent;
	protected String footercentercontent;
	protected String footerrightcontent;
	protected String covercontent;
	protected String indexcontent;

	protected boolean headeroncoverpage;
	protected boolean headeroncontentspage;
	protected boolean footeroncoverpage;
	protected boolean footeroncontentspage;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateddate() {
		return createddate;
	}

	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}

	public UserDTO getCreatedby() {
		return createdby;
	}

	public void setCreatedby(UserDTO createdby) {
		this.createdby = createdby;
	}

	public boolean isNumbered() {
		return numbered;
	}

	public void setNumbered(boolean numbered) {
		this.numbered = numbered;
	}

	public String getHeaderleftcontent() {
		return headerleftcontent;
	}

	public void setHeaderleftcontent(String headerleftcontent) {
		this.headerleftcontent = headerleftcontent;
	}

	public String getHeadercentercontent() {
		return headercentercontent;
	}

	public void setHeadercentercontent(String headercentercontent) {
		this.headercentercontent = headercentercontent;
	}

	public String getHeaderrightcontent() {
		return headerrightcontent;
	}

	public void setHeaderrightcontent(String headerrightcontent) {
		this.headerrightcontent = headerrightcontent;
	}

	public String getFooterleftcontent() {
		return footerleftcontent;
	}

	public void setFooterleftcontent(String footerleftcontent) {
		this.footerleftcontent = footerleftcontent;
	}

	public String getFootercentercontent() {
		return footercentercontent;
	}

	public void setFootercentercontent(String footercentercontent) {
		this.footercentercontent = footercentercontent;
	}

	public String getFooterrightcontent() {
		return footerrightcontent;
	}

	public void setFooterrightcontent(String footerrightcontent) {
		this.footerrightcontent = footerrightcontent;
	}

	public String getCovercontent() {
		return covercontent;
	}

	public void setCovercontent(String covercontent) {
		this.covercontent = covercontent;
	}

	public String getIndexcontent() {
		return indexcontent;
	}

	public void setIndexcontent(String indexcontent) {
		this.indexcontent = indexcontent;
	}

	public boolean isHeaderoncoverpage() {
		return headeroncoverpage;
	}

	public void setHeaderoncoverpage(boolean headeroncoverpage) {
		this.headeroncoverpage = headeroncoverpage;
	}

	public boolean isHeaderoncontentspage() {
		return headeroncontentspage;
	}

	public void setHeaderoncontentspage(boolean headeroncontentspage) {
		this.headeroncontentspage = headeroncontentspage;
	}

	public boolean isFooteroncoverpage() {
		return footeroncoverpage;
	}

	public void setFooteroncoverpage(boolean footeroncoverpage) {
		this.footeroncoverpage = footeroncoverpage;
	}

	public boolean isFooteroncontentspage() {
		return footeroncontentspage;
	}

	public void setFooteroncontentspage(boolean footeroncontentspage) {
		this.footeroncontentspage = footeroncontentspage;
	}

}
