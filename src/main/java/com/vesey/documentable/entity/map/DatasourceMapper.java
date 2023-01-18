package com.vesey.documentable.entity.map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.vesey.documentable.entity.Datasource;
import com.vesey.documentable.entity.dto.DatasourceDTO;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DatasourceMapper {

	public abstract DatasourceDTO getDTOFromDatasource(Datasource entity, @Context CycleAvoidingMappingContext context);

	public abstract Datasource getDatasourceFromDTO(DatasourceDTO dto, @MappingTarget Datasource entity,
			@Context CycleAvoidingMappingContext context);

}
