package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Documenttemplate;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.DocumenttemplateDTO;
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
public class DocumenttemplateMapperImpl extends DocumenttemplateMapper {

    @Override
    public DocumenttemplateDTO getDTOFromDocumenttemplate(Documenttemplate entity, CycleAvoidingMappingContext context) {
        DocumenttemplateDTO target = context.getMappedInstance( entity, DocumenttemplateDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( entity == null ) {
            return null;
        }

        DocumenttemplateDTO documenttemplateDTO = new DocumenttemplateDTO();

        context.storeMappedInstance( entity, documenttemplateDTO );

        documenttemplateDTO.setVersion( entity.getVersion() );
        documenttemplateDTO.setUuid( entity.getUuid() );
        documenttemplateDTO.setName( entity.getName() );
        documenttemplateDTO.setDescription( entity.getDescription() );
        documenttemplateDTO.setCreateddate( entity.getCreateddate() );
        documenttemplateDTO.setCreatedby( usersToUserDTO( entity.getCreatedby(), context ) );
        documenttemplateDTO.setNumbered( entity.isNumbered() );
        documenttemplateDTO.setHeaderleftcontent( entity.getHeaderleftcontent() );
        documenttemplateDTO.setHeadercentercontent( entity.getHeadercentercontent() );
        documenttemplateDTO.setHeaderrightcontent( entity.getHeaderrightcontent() );
        documenttemplateDTO.setFooterleftcontent( entity.getFooterleftcontent() );
        documenttemplateDTO.setFootercentercontent( entity.getFootercentercontent() );
        documenttemplateDTO.setFooterrightcontent( entity.getFooterrightcontent() );
        documenttemplateDTO.setCovercontent( entity.getCovercontent() );
        documenttemplateDTO.setIndexcontent( entity.getIndexcontent() );
        documenttemplateDTO.setHeaderoncoverpage( entity.isHeaderoncoverpage() );
        documenttemplateDTO.setHeaderoncontentspage( entity.isHeaderoncontentspage() );
        documenttemplateDTO.setFooteroncoverpage( entity.isFooteroncoverpage() );
        documenttemplateDTO.setFooteroncontentspage( entity.isFooteroncontentspage() );

        return documenttemplateDTO;
    }

    @Override
    public Documenttemplate getDocumenttemplateFromDTO(DocumenttemplateDTO dto, Documenttemplate entity, CycleAvoidingMappingContext context) {
        Documenttemplate target = context.getMappedInstance( dto, Documenttemplate.class );
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
        if ( dto.getCreateddate() != null ) {
            entity.setCreateddate( dto.getCreateddate() );
        }
        entity.setNumbered( dto.isNumbered() );
        if ( dto.getHeaderleftcontent() != null ) {
            entity.setHeaderleftcontent( dto.getHeaderleftcontent() );
        }
        if ( dto.getHeadercentercontent() != null ) {
            entity.setHeadercentercontent( dto.getHeadercentercontent() );
        }
        if ( dto.getHeaderrightcontent() != null ) {
            entity.setHeaderrightcontent( dto.getHeaderrightcontent() );
        }
        if ( dto.getFooterleftcontent() != null ) {
            entity.setFooterleftcontent( dto.getFooterleftcontent() );
        }
        if ( dto.getFootercentercontent() != null ) {
            entity.setFootercentercontent( dto.getFootercentercontent() );
        }
        if ( dto.getFooterrightcontent() != null ) {
            entity.setFooterrightcontent( dto.getFooterrightcontent() );
        }
        if ( dto.getCovercontent() != null ) {
            entity.setCovercontent( dto.getCovercontent() );
        }
        if ( dto.getIndexcontent() != null ) {
            entity.setIndexcontent( dto.getIndexcontent() );
        }
        entity.setHeaderoncoverpage( dto.isHeaderoncoverpage() );
        entity.setHeaderoncontentspage( dto.isHeaderoncontentspage() );
        entity.setFooteroncoverpage( dto.isFooteroncoverpage() );
        entity.setFooteroncontentspage( dto.isFooteroncontentspage() );

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
