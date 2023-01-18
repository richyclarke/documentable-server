package com.vesey.documentable.entity.map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.dto.MergefieldtemplateDTO;

@Mapper(componentModel = "cdi", uses = {
		MergefieldoptionMapper.class }, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MergefieldtemplateMapper {

	@Mapping(source = "datasource.uuid", target = "datasourceUuid")
	public abstract MergefieldtemplateDTO getDTOFromMergefieldtemplate(Mergefieldtemplate entity, @Context CycleAvoidingMappingContext context);

	@Mapping(target = "datasource", ignore = true)
	public abstract Mergefieldtemplate getMergefieldtemplateFromDTO(MergefieldtemplateDTO dto, @MappingTarget Mergefieldtemplate entity,
			@Context CycleAvoidingMappingContext context);

}
