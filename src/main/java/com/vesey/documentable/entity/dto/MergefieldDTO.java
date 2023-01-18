package com.vesey.documentable.entity.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Mergefield")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MergefieldDTO extends BaseEntityDTO {

	private String stringvalue;
	private Integer integervalue;
	private BigDecimal decimalvalue;
	private Date datevalue;
	private Boolean booleanvalue;
	@NotNull
	private MergefieldtemplateDTO mergefieldtemplate;

	public String getStringvalue() {
		return stringvalue;
	}

	public void setStringvalue(String stringvalue) {
		this.stringvalue = stringvalue;
	}

	public Integer getIntegervalue() {
		return integervalue;
	}

	public void setIntegervalue(Integer integervalue) {
		this.integervalue = integervalue;
	}

	public BigDecimal getDecimalvalue() {
		return decimalvalue;
	}

	public void setDecimalvalue(BigDecimal decimalvalue) {
		this.decimalvalue = decimalvalue;
	}

	public Date getDatevalue() {
		return datevalue;
	}

	public void setDatevalue(Date datevalue) {
		this.datevalue = datevalue;
	}

	public MergefieldtemplateDTO getMergefieldtemplate() {
		return mergefieldtemplate;
	}

	public void setMergefieldtemplate(MergefieldtemplateDTO mergefieldtemplate) {
		this.mergefieldtemplate = mergefieldtemplate;
	}

	public Boolean getBooleanvalue() {
		return booleanvalue;
	}

	public void setBooleanvalue(Boolean booleanvalue) {
		this.booleanvalue = booleanvalue;
	}

}
