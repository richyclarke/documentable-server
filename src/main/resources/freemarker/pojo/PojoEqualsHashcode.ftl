<#if pojo.hasIdentifierProperty() && !clazz.superclass?exists>
	<#assign classNameToCastTo>
		<#if clazz.getProxyInterfaceName?exists>
			${clazz.getProxyInterfaceName()}Base
		<#else>
			${pojo.getDeclarationName()}Base
		</#if>
	</#assign>

	@Override
   	public boolean equals(Object other) {
         if (this == other) {
         	return true;
         }
		 if(other == null){
		 	return false;
		 }
		 if (!(other instanceof ${classNameToCastTo})){
		 	return false;
		 }
		 
		 ${classNameToCastTo} castOther = (${classNameToCastTo}) other; 
         
         <#foreach property in pojo.getAllPropertiesIterator()>    
	    	<#if property.equals(clazz.identifierProperty)>
	    		
	    		return ((this.${pojo.getGetterSignature(property)}() == castOther.${pojo.getGetterSignature(property)}()) 
	    			|| (this.${pojo.getGetterSignature(property)}() != null	&& castOther.${pojo.getGetterSignature(property)}() != null && this.${pojo.getGetterSignature(property)}().equals(castOther.${pojo.getGetterSignature(property)}())));
	      	
	      	</#if>
		</#foreach> 
   	}
   	
	@Override   
	public int hashCode() {
		int result = 17;
	    <#foreach property in pojo.getAllPropertiesIterator()>    
	    	<#if property.equals(clazz.identifierProperty)>
	    		result = 37 * result + (${pojo.getGetterSignature(property)}() == null? 0 : this.${pojo.getGetterSignature(property)}().hashCode()); 
	      	</#if>
		</#foreach>         
		return result;
	}   
</#if>