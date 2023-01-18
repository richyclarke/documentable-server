package com.vesey.documentable.entity.map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.vesey.documentable.entity.Rule;
import com.vesey.documentable.entity.dto.RuleDTO;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RuleMapper {

	public abstract RuleDTO getDTOFromRule(Rule entity, @Context CycleAvoidingMappingContext context);

	@Mapping(target = "rulegroup", ignore = true)
	@Mapping(target = "source", ignore = true)
	public abstract Rule getRuleFromDTO(RuleDTO dto, @MappingTarget Rule entity, @Context CycleAvoidingMappingContext context);

}
