<#if pojo.hasIdentifierProperty()>   
	public String toString() {
	StringBuffer buffer = new StringBuffer();

    buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
		<#foreach property in pojo.getAllPropertiesIterator()>
			<#if property.equals(clazz.identifierProperty)>
				buffer.append("${property.getName()}").append("='").append(${pojo.getGetterSignature(property)}()).append("' ");		
			</#if>	
		</#foreach>
    buffer.append("]");
      
     return buffer.toString();
     }
</#if>

      
