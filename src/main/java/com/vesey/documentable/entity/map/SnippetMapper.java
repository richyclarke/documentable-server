package com.vesey.documentable.entity.map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.vesey.documentable.entity.Snippet;
import com.vesey.documentable.entity.dto.SnippetDTO;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class SnippetMapper {

	@Mapping(source = "snippettemplate.uuid", target = "snippettemplateUuid")
	@Mapping(source = "document.uuid", target = "documentUuid")
	public abstract SnippetDTO getDTOFromSnippet(Snippet entity, @Context CycleAvoidingMappingContext context);

	@Mapping(target = "document", ignore = true)
	@Mapping(target = "snippettemplate", ignore = true)
	public abstract Snippet getSnippetFromDTO(SnippetDTO dto, @MappingTarget Snippet entity,
			@Context CycleAvoidingMappingContext context);

	public abstract Snippet cloneSnippet(Snippet original, @Context CycleAvoidingMappingContext context);

}
