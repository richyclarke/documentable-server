package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.UserDTO;
import com.vesey.documentable.enums.RoleTypeEnum;
import javax.annotation.processing.Generated;
import javax.enterprise.context.ApplicationScoped;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-18T18:22:14+0000",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 3.33.0.v20221215-1352, environment: Java 17.0.5 (Eclipse Adoptium)"
)
@ApplicationScoped
public class UserMapperImpl extends UserMapper {

    @Override
    public UserDTO getDTOFromUser(Users entity, CycleAvoidingMappingContext context) {
        UserDTO target = context.getMappedInstance( entity, UserDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( entity == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        context.storeMappedInstance( entity, userDTO );

        userDTO.setVersion( entity.getVersion() );
        userDTO.setUuid( entity.getUuid() );
        userDTO.setEmail( entity.getEmail() );
        userDTO.setProfilepicturefilename( entity.getProfilepicturefilename() );
        userDTO.setName( entity.getName() );
        if ( entity.getRole() != null ) {
            userDTO.setRole( entity.getRole().name() );
        }

        return userDTO;
    }

    @Override
    public Users getUserFromDTO(UserDTO dto, Users user, CycleAvoidingMappingContext context) {
        Users target = context.getMappedInstance( dto, Users.class );
        if ( target != null ) {
            return target;
        }

        if ( dto == null ) {
            return null;
        }

        context.storeMappedInstance( dto, user );

        if ( dto.getVersion() != null ) {
            user.setVersion( dto.getVersion() );
        }
        if ( dto.getUuid() != null ) {
            user.setUuid( dto.getUuid() );
        }
        if ( dto.getEmail() != null ) {
            user.setEmail( dto.getEmail() );
        }
        if ( dto.getProfilepicturefilename() != null ) {
            user.setProfilepicturefilename( dto.getProfilepicturefilename() );
        }
        if ( dto.getName() != null ) {
            user.setName( dto.getName() );
        }
        if ( dto.getRole() != null ) {
            user.setRole( Enum.valueOf( RoleTypeEnum.class, dto.getRole() ) );
        }

        return user;
    }
}