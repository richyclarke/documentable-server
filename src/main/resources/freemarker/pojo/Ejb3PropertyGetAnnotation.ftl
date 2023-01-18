<#if ejb3>
	<#if pojo.hasIdentifierProperty()>
		<#if property.equals(clazz.identifierProperty)>
 			${pojo.generateAnnIdGenerator()}
 			<#--  if this is the id property (getter)-->
			<#-- explicitly set the column name for this property-->
			@Column(name = "id", unique = true, nullable = false)</#if>			
	</#if>
	<#if c2h.isOneToOne(property)>		
		${pojo.generateOneToOneAnnotation(property, md)}
	<#elseif c2h.isManyToOne(property)>		
        ${pojo.generateManyToOneAnnotation(property)}   
		${pojo.generateJoinColumnsAnnotation(property, md)}
	<#elseif c2h.isOneToMany(property)>	
			<#--  ADD SPECIFIC @JSONIgnore STATEMENTS HERE -->
		<#switch pojo.getDeclarationName()>		
			<#default>
				@JsonIgnore
			<#break>
		</#switch>
		${pojo.generateCollectionAnnotation(property, md)}
	<#elseif c2h.isManyToMany(property)>	
			<#--  ADD SPECIFIC @JSONIgnore STATEMENTS HERE -->
		<#switch pojo.getDeclarationName()>
			<#default>
				@JsonIgnore
			<#break>
		</#switch>
		${pojo.generateCollectionAnnotation(property, md)}
	<#else>
			<#--  ADD SPECIFIC @JSONIgnore OR @Enumerated STATEMENTS HERE -->
		<#switch pojo.getDeclarationName()>
			<#case "FilerefBase">
				<#switch property.name>
					<#case "type">
					@Enumerated(EnumType.STRING)
					<#break>						
				</#switch>
			<#break>
			<#case "UsersBase">
				<#switch property.name>
					<#case "role">
					@Enumerated(EnumType.STRING)
					<#break>						
				</#switch>
			<#break>
			<#case "MergefieldtemplateBase">
				<#switch property.name>
					<#case "type">
					@Enumerated(EnumType.STRING)
					<#break>						
				</#switch>
			<#break>
			<#case "RuleBase">
				<#switch property.name>
					<#case "operand">
					@Enumerated(EnumType.STRING)
					<#break>						
				</#switch>
			<#break>
			<#case "DatasourceBase">
				<#switch property.name>
					<#case "type">
					@Enumerated(EnumType.STRING)
					<#break>						
				</#switch>
			<#break>
			<#case "MatterBase">
				<#switch property.name>
					<#case "type">
					@Enumerated(EnumType.STRING)
					<#break>						
				</#switch>
			<#break>
		</#switch>
		${pojo.generateBasicAnnotation(property)}
		<#if !property.equals(clazz.identifierProperty)>
			${pojo.generateAnnColumnAnnotation(property)}			
		</#if>			
	</#if>
</#if>