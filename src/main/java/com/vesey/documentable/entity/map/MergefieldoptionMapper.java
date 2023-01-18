package com.vesey.documentable.entity.map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.vesey.documentable.entity.Mergefieldoption;
import com.vesey.documentable.entity.dto.MergefieldoptionDTO;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MergefieldoptionMapper {

	@Mapping(source = "value", target = "id")
	public abstract MergefieldoptionDTO getDTOFromMergefieldoption(Mergefieldoption entity, @Context CycleAvoidingMappingContext context);

	public abstract Mergefieldoption getMergefieldoptionFromDTO(MergefieldoptionDTO dto, @MappingTarget Mergefieldoption entity, @Context CycleAvoidingMappingContext context);

}
