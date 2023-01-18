package com.vesey.documentable.entity.map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.UserDTO;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

	public abstract UserDTO getDTOFromUser(Users entity, @Context CycleAvoidingMappingContext context);

	public abstract Users getUserFromDTO(UserDTO dto, @MappingTarget Users user, @Context CycleAvoidingMappingContext context);

}
