package com.vesey.documentable.entity.map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.vesey.documentable.entity.Documenttemplate;
import com.vesey.documentable.entity.dto.DocumenttemplateDTO;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DocumenttemplateMapper {

	public abstract DocumenttemplateDTO getDTOFromDocumenttemplate(Documenttemplate entity, @Context CycleAvoidingMappingContext context);

	public abstract Documenttemplate getDocumenttemplateFromDTO(DocumenttemplateDTO dto, @MappingTarget Documenttemplate entity,
			@Context CycleAvoidingMappingContext context);

}
