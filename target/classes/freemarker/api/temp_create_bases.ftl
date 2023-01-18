package com.vesey.searchpoint.entity;
// Generated ${date} by Hibernate Tools ${version} (Modified by RJC)



<#assign classbody>
<#assign declarationName = pojo.importType(pojo.getDeclarationName())>

/**
 * Base object for domain model class ${declarationName}.
 * @see ${pojo.getQualifiedDeclarationName()}
 * @author Hibernate Tools
 */

public class ${declarationName} extends ${declarationName}Base{



}


</#assign>
import com.vesey.searchpoint.entity.base.${pojo.getDeclarationName()}Base;
${classbody}
