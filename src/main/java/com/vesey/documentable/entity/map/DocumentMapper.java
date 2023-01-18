package com.vesey.documentable.entity.map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.vesey.documentable.entity.Document;
import com.vesey.documentable.entity.dto.DocumentDTO;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DocumentMapper {

	@Mapping(source = "matter.uuid", target = "matterUuid")
	@Mapping(source = "documenttemplate.uuid", target = "documenttemplateUuid")
	@Mapping(source = "documenttemplate.name", target = "documenttemplatename")
	public abstract DocumentDTO getDTOFromDocument(Document entity, @Context CycleAvoidingMappingContext context);

	@Mapping(target = "matter", ignore = true)
	@Mapping(target = "documenttemplate", ignore = true)
	public abstract Document getDocumentFromDTO(DocumentDTO dto, @MappingTarget Document entity, @Context CycleAvoidingMappingContext context);

}
