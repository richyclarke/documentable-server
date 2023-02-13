package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Matter;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.MatterDTO;
import com.vesey.documentable.entity.dto.UserDTO;
import com.vesey.documentable.enums.RoleTypeEnum;
import javax.annotation.processing.Generated;
import javax.enterprise.context.ApplicationScoped;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-02-07T15:58:59+0000",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 1.4.100.v20220318-0906, environment: Java 11.0.9 (Oracle Corporation)"
)
@ApplicationScoped
public class MatterMapperImpl extends MatterMapper {

    @Override
    public MatterDTO getDTOFromMatter(Matter entity, CycleAvoidingMappingContext context) {
        MatterDTO target = context.getMappedInstance( entity, MatterDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( entity == null ) {
            return null;
        }

        MatterDTO matterDTO = new MatterDTO();

        context.storeMappedInstance( entity, matterDTO );

        matterDTO.setVersion( entity.getVersion() );
        matterDTO.setUuid( entity.getUuid() );
        matterDTO.setName( entity.getName() );
        matterDTO.setDescription( entity.getDescription() );
        matterDTO.setCreateddate( entity.getCreateddate() );
        matterDTO.setCreatedby( usersToUserDTO( entity.getCreatedby(), context ) );
        matterDTO.setReference( entity.getReference() );
        matterDTO.setType( entity.getType() );

        return matterDTO;
    }

    @Override
    public Matter getMatterFromDTO(MatterDTO dto, Matter entity, CycleAvoidingMappingContext context) {
        Matter target = context.getMappedInstance( dto, Matter.class );
        if ( target != null ) {
            return target;
        }

        if ( dto == null ) {
            return null;
        }

        context.storeMappedInstance( dto, entity );

        if ( dto.getVersion() != null ) {
            entity.setVersion( dto.getVersion() );
        }
        if ( dto.getCreatedby() != null ) {
            if ( entity.getCreatedby() == null ) {
                entity.setCreatedby( new Users() );
            }
            userDTOToUsers( dto.getCreatedby(), context, entity.getCreatedby() );
        }
        if ( dto.getUuid() != null ) {
            entity.setUuid( dto.getUuid() );
        }
        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getReference() != null ) {
            entity.setReference( dto.getReference() );
        }
        if ( dto.getType() != null ) {
            entity.setType( dto.getType() );
        }
        if ( dto.getCreateddate() != null ) {
            entity.setCreateddate( dto.getCreateddate() );
        }

        return entity;
    }

    protected UserDTO usersToUserDTO(Users users, CycleAvoidingMappingContext context) {
        UserDTO target = context.getMappedInstance( users, UserDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( users == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        context.storeMappedInstance( users, userDTO );

        userDTO.setVersion( users.getVersion() );
        userDTO.setUuid( users.getUuid() );
        userDTO.setEmail( users.getEmail() );
        userDTO.setProfilepicturefilename( users.getProfilepicturefilename() );
        userDTO.setName( users.getName() );
        if ( users.getRole() != null ) {
            userDTO.setRole( users.getRole().name() );
        }

        return userDTO;
    }

    protected void userDTOToUsers(UserDTO userDTO, CycleAvoidingMappingContext context, Users mappingTarget) {
        Users target = context.getMappedInstance( userDTO, Users.class );
        if ( target != null ) {
            return;
        }

        if ( userDTO == null ) {
            return;
        }

        context.storeMappedInstance( userDTO, mappingTarget );

        if ( userDTO.getVersion() != null ) {
            mappingTarget.setVersion( userDTO.getVersion() );
        }
        if ( userDTO.getUuid() != null ) {
            mappingTarget.setUuid( userDTO.getUuid() );
        }
        if ( userDTO.getEmail() != null ) {
            mappingTarget.setEmail( userDTO.getEmail() );
        }
        if ( userDTO.getProfilepicturefilename() != null ) {
            mappingTarget.setProfilepicturefilename( userDTO.getProfilepicturefilename() );
        }
        if ( userDTO.getName() != null ) {
            mappingTarget.setName( userDTO.getName() );
        }
        if ( userDTO.getRole() != null ) {
            mappingTarget.setRole( Enum.valueOf( RoleTypeEnum.class, userDTO.getRole() ) );
        }
    }
}
