package com.vesey.documentable.entity.map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.vesey.documentable.entity.Snippettemplate;
import com.vesey.documentable.entity.dto.SnippettemplateDTO;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class SnippettemplateMapper {

	@Mapping(source = "documenttemplate.uuid", target = "documenttemplateUuid")
	@Mapping(source = "parent.uuid", target = "parentUuid")
	public abstract SnippettemplateDTO getDTOFromSnippettemplate(Snippettemplate entity, @Context CycleAvoidingMappingContext context);

	@Mapping(target = "documenttemplate", ignore = true)
	@Mapping(target = "parent", ignore = true)
	public abstract Snippettemplate getSnippettemplateFromDTO(SnippettemplateDTO dto, @MappingTarget Snippettemplate entity,
			@Context CycleAvoidingMappingContext context);

}
