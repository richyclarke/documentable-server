package com.vesey.documentable.entity.map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.vesey.documentable.entity.Rulegroup;
import com.vesey.documentable.entity.dto.RulegroupDTO;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RulegroupMapper {

	public abstract RulegroupDTO getDTOFromRulegroup(Rulegroup entity, @Context CycleAvoidingMappingContext context);

	@Mapping(target = "ruleset", ignore = true)
	@Mapping(target = "rules", ignore = true)
	public abstract Rulegroup getRulegroupFromDTO(RulegroupDTO dto, @MappingTarget Rulegroup entity, @Context CycleAvoidingMappingContext context);

}
