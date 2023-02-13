package com.vesey.documentable.entity.base;

// Generated 24 Jan 2023, 11:06:04 by Hibernate Tools 5.2.13.Final (Modified by RJC)

import com.vesey.documentable.enums.RuleOperandTypeEnum;
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
 * RuleBase - This file has been auto-generated - do not edit (changes will be overwritten)
 */

@MappedSuperclass
@Table(name = "rule", catalog = "documentable")

public abstract class RuleBase extends BaseEntity implements java.io.Serializable {

	public static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)

	protected Integer id;

	@Version
	@Column(name = "version", nullable = false)
	protected Integer version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source", nullable = false)
	protected Mergefieldtemplate source;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rulegroup", nullable = false)
	protected Rulegroup rulegroup;

	@Column(name = "uuid", nullable = false, length = 36)
	protected String uuid;

	@Enumerated(EnumType.STRING)

	@Column(name = "operand", nullable = false, length = 45)
	protected RuleOperandTypeEnum operand;

	@Column(name = "stringvalue", length = 45)
	protected String stringvalue;

	@Column(name = "booleanvalue")
	protected Boolean booleanvalue;

	@Column(name = "decimalvalue", precision = 15, scale = 4)
	protected BigDecimal decimalvalue;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datevalue", length = 19)
	protected Date datevalue;

	@Column(name = "integervalue")
	protected Integer integervalue;

	public RuleBase() {
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

	public Mergefieldtemplate getSource() {
		return this.source;
	}

	public void setSource(Mergefieldtemplate source) {
		this.source = source;
	}

	public Rulegroup getRulegroup() {
		return this.rulegroup;
	}

	public void setRulegroup(Rulegroup rulegroup) {
		this.rulegroup = rulegroup;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public RuleOperandTypeEnum getOperand() {
		return this.operand;
	}

	public void setOperand(RuleOperandTypeEnum operand) {
		this.operand = operand;
	}

	public String getStringvalue() {
		return this.stringvalue;
	}

	public void setStringvalue(String stringvalue) {
		this.stringvalue = stringvalue;
	}

	public Boolean getBooleanvalue() {
		return this.booleanvalue;
	}

	public void setBooleanvalue(Boolean booleanvalue) {
		this.booleanvalue = booleanvalue;
	}

	public BigDecimal getDecimalvalue() {
		return this.decimalvalue;
	}

	public void setDecimalvalue(BigDecimal decimalvalue) {
		this.decimalvalue = decimalvalue;
	}

	public Date getDatevalue() {
		return this.datevalue;
	}

	public void setDatevalue(Date datevalue) {
		this.datevalue = datevalue;
	}

	public Integer getIntegervalue() {
		return this.integervalue;
	}

	public void setIntegervalue(Integer integervalue) {
		this.integervalue = integervalue;
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
		if (!(other instanceof com.vesey.documentable.entity.base.RuleBase)) {
			return false;
		}

		com.vesey.documentable.entity.base.RuleBase castOther = (com.vesey.documentable.entity.base.RuleBase) other;

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
