package com.vesey.documentable.entity.map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.vesey.documentable.entity.Mergefield;
import com.vesey.documentable.entity.dto.MergefieldDTO;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MergefieldMapper {

	public abstract MergefieldDTO getDTOFromMergefield(Mergefield entity, @Context CycleAvoidingMappingContext context);

	public abstract Mergefield getMergefieldFromDTO(MergefieldDTO dto, @MappingTarget Mergefield entity, @Context CycleAvoidingMappingContext context);

}
