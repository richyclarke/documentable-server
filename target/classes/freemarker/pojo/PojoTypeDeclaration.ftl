
<#if pojo.getDeclarationName()?ends_with("Base")>
/**
${pojo.getClassJavaDoc(pojo.getDeclarationName() + " - This file has been auto-generated - do not edit (changes will be overwritten)", 0)}
 */
 
 </#if>
<#include "Ejb3TypeDeclaration.ftl"/>
${pojo.getClassModifiers()} ${pojo.getDeclarationType()} ${pojo.getDeclarationName()}  ${pojo.getExtendsDeclaration()} <#if pojo.getDeclarationName()?ends_with("Base")>${pojo.getImplementsDeclaration()}</#if>