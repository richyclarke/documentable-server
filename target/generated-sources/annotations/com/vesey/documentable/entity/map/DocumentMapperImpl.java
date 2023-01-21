package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Document;
import com.vesey.documentable.entity.Documenttemplate;
import com.vesey.documentable.entity.Matter;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.DocumentDTO;
import com.vesey.documentable.entity.dto.UserDTO;
import com.vesey.documentable.enums.RoleTypeEnum;
import javax.annotation.processing.Generated;
import javax.enterprise.context.ApplicationScoped;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-21T15:22:47+0000",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 1.4.100.v20220318-0906, environment: Java 11.0.9 (Oracle Corporation)"
)
@ApplicationScoped
public class DocumentMapperImpl extends DocumentMapper {

    @Override
    public DocumentDTO getDTOFromDocument(Document entity, CycleAvoidingMappingContext context) {
        DocumentDTO target = context.getMappedInstance( entity, DocumentDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( entity == null ) {
            return null;
        }

        DocumentDTO documentDTO = new DocumentDTO();

        context.storeMappedInstance( entity, documentDTO );

        documentDTO.setMatterUuid( entityMatterUuid( entity ) );
        documentDTO.setDocumenttemplateUuid( entityDocumenttemplateUuid( entity ) );
        documentDTO.setDocumenttemplatename( entityDocumenttemplateName( entity ) );
        documentDTO.setVersion( entity.getVersion() );
        documentDTO.setUuid( entity.getUuid() );
        documentDTO.setName( entity.getName() );
        documentDTO.setDescription( entity.getDescription() );
        documentDTO.setCreateddate( entity.getCreateddate() );
        documentDTO.setCreatedby( usersToUserDTO( entity.getCreatedby(), context ) );
        documentDTO.setLastmodifiedby( usersToUserDTO( entity.getLastmodifiedby(), context ) );
        documentDTO.setLastmodifieddate( entity.getLastmodifieddate() );
        documentDTO.setNumbered( entity.isNumbered() );

        return documentDTO;
    }

    @Override
    public Document getDocumentFromDTO(DocumentDTO dto, Document entity, CycleAvoidingMappingContext context) {
        Document target = context.getMappedInstance( dto, Document.class );
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
        if ( dto.getLastmodifiedby() != null ) {
            if ( entity.getLastmodifiedby() == null ) {
                entity.setLastmodifiedby( new Users() );
            }
            userDTOToUsers( dto.getLastmodifiedby(), context, entity.getLastmodifiedby() );
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
        if ( dto.getLastmodifieddate() != null ) {
            entity.setLastmodifieddate( dto.getLastmodifieddate() );
        }
        entity.setNumbered( dto.isNumbered() );

        return entity;
    }

    private String entityMatterUuid(Document document) {
        if ( document == null ) {
            return null;
        }
        Matter matter = document.getMatter();
        if ( matter == null ) {
            return null;
        }
        String uuid = matter.getUuid();
        if ( uuid == null ) {
            return null;
        }
        return uuid;
    }

    private String entityDocumenttemplateUuid(Document document) {
        if ( document == null ) {
            return null;
        }
        Documenttemplate documenttemplate = document.getDocumenttemplate();
        if ( documenttemplate == null ) {
            return null;
        }
        String uuid = documenttemplate.getUuid();
        if ( uuid == null ) {
            return null;
        }
        return uuid;
    }

    private String entityDocumenttemplateName(Document document) {
        if ( document == null ) {
            return null;
        }
        Documenttemplate documenttemplate = document.getDocumenttemplate();
        if ( documenttemplate == null ) {
            return null;
        }
        String name = documenttemplate.getName();
        if ( name == null ) {
            return null;
        }
        return name;
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
