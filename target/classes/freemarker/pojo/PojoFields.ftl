<#-- // Fields -->

<#foreach property in pojo.getAllPropertiesIterator()>
	<#if pojo.getMetaAttribAsBool(property, "gen-property", true)> 
		<#if pojo.hasMetaAttribute(property, "field-description")>    
			/**
     		${pojo.getFieldJavaDoc(property, 0)}
     		*/
 		</#if>
 	
	  <#include "GetPropertyAnnotation.ftl"/>
 		protected ${pojo.getJavaTypeName(property, jdk5)} ${c2j.keyWordCheck(property.name)}
 		<#if pojo.hasFieldInitializor(property, jdk5)>
 	 		= ${pojo.getFieldInitialization(property, jdk5)}
 	 	</#if>;
	</#if>


</#foreach>
