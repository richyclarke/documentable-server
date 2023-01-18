${pojo.getPackageDeclaration()}
<#if pojo.getDeclarationName()?ends_with("Base")>

// Generated ${date} by Hibernate Tools ${version} (Modified by RJC)
</#if>
<#assign classbody>	
@SuppressWarnings("unused")
<#include "PojoTypeDeclaration.ftl"/>  {

<#if !pojo.isInterface()>
public static final long serialVersionUID = 1L;
		<#if pojo.getDeclarationName()?ends_with("Base")>
		<#include "PojoFields.ftl"/>
	</#if>
	<#if pojo.getDeclarationName()?ends_with("Base")>
		<#include "PojoConstructors.ftl"/>
		<#include "PojoPropertyAccessors.ftl"/>
		<#include "PojoToString.ftl"/>
		<#include "PojoEqualsHashcode.ftl"/>
	<#else>
			<#include "PojoExtraClassCode.ftl"/>
	</#if>

<#else>
		<#include "PojoInterfacePropertyAccessors.ftl"/>
</#if>
}
</#assign>

${pojo.generateImports()}
<#if pojo.getDeclarationName()?ends_with("Base")>
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.*;
import javax.persistence.Column;
import com.vesey.documentable.session.DBFacade;
import javax.persistence.Transient;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Collection;
import com.vesey.documentable.utils.Utils;

<#else>
import com.vesey.documentable.entity.base.${pojo.getDeclarationName()}Base;
</#if>

${classbody}

