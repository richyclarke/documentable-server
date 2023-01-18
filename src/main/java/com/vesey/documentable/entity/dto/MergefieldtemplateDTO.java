package com.vesey.documentable.entity.dto;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vesey.documentable.enums.FormDataTypeEnum;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Mergefieldtemplate")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MergefieldtemplateDTO extends BaseEntityDTO {

	private String key;
	private String label;
	private String samplestringvalue;
	private Integer sampleintegervalue;
	private BigDecimal sampledecimalvalue;
	private Date sampledatevalue;
	private Boolean samplebooleanvalue;
	private String datasourceUuid;

	private FormDataTypeEnum type;
	private String description;
	private DatasourceDTO datasource;
	private Collection<MergefieldoptionDTO> options;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public FormDataTypeEnum getType() {
		return type;
	}

	public void setType(FormDataTypeEnum type) {
		this.type = type;
	}

	public Collection<MergefieldoptionDTO> getOptions() {
		return options;
	}

	public void setOptions(Collection<MergefieldoptionDTO> options) {
		this.options = options;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSamplestringvalue() {
		return samplestringvalue;
	}

	public void setSamplestringvalue(String samplestringvalue) {
		this.samplestringvalue = samplestringvalue;
	}

	public Integer getSampleintegervalue() {
		return sampleintegervalue;
	}

	public void setSampleintegervalue(Integer sampleintegervalue) {
		this.sampleintegervalue = sampleintegervalue;
	}

	public BigDecimal getSampledecimalvalue() {
		return sampledecimalvalue;
	}

	public void setSampledecimalvalue(BigDecimal sampledecimalvalue) {
		this.sampledecimalvalue = sampledecimalvalue;
	}

	public Date getSampledatevalue() {
		return sampledatevalue;
	}

	public void setSampledatevalue(Date sampledatevalue) {
		this.sampledatevalue = sampledatevalue;
	}

	public Boolean getSamplebooleanvalue() {
		return samplebooleanvalue;
	}

	public void setSamplebooleanvalue(Boolean samplebooleanvalue) {
		this.samplebooleanvalue = samplebooleanvalue;
	}

	public DatasourceDTO getDatasource() {
		return datasource;
	}

	public void setDatasource(DatasourceDTO datasource) {
		this.datasource = datasource;
	}

	public String getDatasourceUuid() {
		return datasourceUuid;
	}

	public void setDatasourceUuid(String datasourceUuid) {
		this.datasourceUuid = datasourceUuid;
	}

}
