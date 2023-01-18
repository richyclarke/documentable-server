<#if pojo.hasMetaAttribute("class-code")>  

// The following is extra code specified in the hbm.xml files
${pojo.getExtraClassCode()}
 // end of extra code specified in the hbm.xml files
 
</#if>
// The following is extra code specified in the freemarker files

	@JsonIgnore
	@Transient
	public static ${pojo.getDeclarationName()} getById(DBFacade dbFacade, Integer id)
{
	return dbFacade.getEntity(${pojo.getDeclarationName()}.class, id);
}	
	@JsonIgnore
	@Transient
	public static Collection<${pojo.getDeclarationName()}> getAll(DBFacade dbFacade, String orderBy)
	{
		String sql = "select Object(x) from ${pojo.getDeclarationName()} x";
		if(Utils.isNotEmpty(orderBy))
		{
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(${pojo.getDeclarationName()}.class, sql, null, false);
	}	

 // end of extra code specified in the freemarker files
