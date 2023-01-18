package com.vesey.documentable.entity.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vesey.documentable.enums.RuleOperandTypeEnum;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Rule")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleDTO extends BaseEntityDTO {

	private MergefieldtemplateDTO source;

	private RuleOperandTypeEnum operand;
	private String stringvalue;
	private Boolean booleanvalue;
	private BigDecimal decimalvalue;
	private Date datevalue;
	private Integer integervalue;

	private String sourceUuid; // the mergefieldtemplate to use as a source when adding new rules
	private String rulegroupUuid; // the rulegroup to add to when adding new rules
	private String snippettemplateUuid; // the snippettemplate to add to when adding first new rule (if no rulegroup exisits)

	public MergefieldtemplateDTO getSource() {
		return source;
	}

	public void setSource(MergefieldtemplateDTO source) {
		this.source = source;
	}

	public RuleOperandTypeEnum getOperand() {
		return operand;
	}

	public void setOperand(RuleOperandTypeEnum operand) {
		this.operand = operand;
	}

	public String getStringvalue() {
		return stringvalue;
	}

	public void setStringvalue(String stringvalue) {
		this.stringvalue = stringvalue;
	}

	public Boolean getBooleanvalue() {
		return booleanvalue;
	}

	public void setBooleanvalue(Boolean booleanvalue) {
		this.booleanvalue = booleanvalue;
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

	public Integer getIntegervalue() {
		return integervalue;
	}

	public void setIntegervalue(Integer integervalue) {
		this.integervalue = integervalue;
	}

	public String getSnippettemplateUuid() {
		return snippettemplateUuid;
	}

	public void setSnippettemplateUuid(String snippettemplateUuid) {
		this.snippettemplateUuid = snippettemplateUuid;
	}

	public String getRulegroupUuid() {
		return rulegroupUuid;
	}

	public void setRulegroupUuid(String rulegroupUuid) {
		this.rulegroupUuid = rulegroupUuid;
	}

	public String getSourceUuid() {
		return sourceUuid;
	}

	public void setSourceUuid(String sourceUuid) {
		this.sourceUuid = sourceUuid;
	}
}
