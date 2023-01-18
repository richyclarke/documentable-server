package com.vesey.documentable.entity.map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.vesey.documentable.entity.Matter;
import com.vesey.documentable.entity.dto.MatterDTO;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MatterMapper {

	public abstract MatterDTO getDTOFromMatter(Matter entity, @Context CycleAvoidingMappingContext context);

	public abstract Matter getMatterFromDTO(MatterDTO dto, @MappingTarget Matter entity,
			@Context CycleAvoidingMappingContext context);

}
