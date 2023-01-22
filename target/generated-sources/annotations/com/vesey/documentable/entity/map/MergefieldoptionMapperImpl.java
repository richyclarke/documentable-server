package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Mergefieldoption;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.dto.MergefieldoptionDTO;
import javax.annotation.processing.Generated;
import javax.enterprise.context.ApplicationScoped;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-22T17:49:27+0000",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 1.4.100.v20220318-0906, environment: Java 11.0.9 (Oracle Corporation)"
)
@ApplicationScoped
public class MergefieldoptionMapperImpl extends MergefieldoptionMapper {

    @Override
    public MergefieldoptionDTO getDTOFromMergefieldoption(Mergefieldoption entity, CycleAvoidingMappingContext context) {
        MergefieldoptionDTO target = context.getMappedInstance( entity, MergefieldoptionDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( entity == null ) {
            return null;
        }

        MergefieldoptionDTO mergefieldoptionDTO = new MergefieldoptionDTO();

        context.storeMappedInstance( entity, mergefieldoptionDTO );

        mergefieldoptionDTO.setId( entity.getUuid() );
        mergefieldoptionDTO.setMergefieldtemplateUuid( entityMergefieldtemplateUuid( entity ) );
        mergefieldoptionDTO.setVersion( entity.getVersion() );
        mergefieldoptionDTO.setUuid( entity.getUuid() );
        mergefieldoptionDTO.setLabel( entity.getLabel() );
        mergefieldoptionDTO.setSortorder( entity.getSortorder() );

        return mergefieldoptionDTO;
    }

    @Override
    public Mergefieldoption getMergefieldoptionFromDTO(MergefieldoptionDTO dto, Mergefieldoption entity, CycleAvoidingMappingContext context) {
        Mergefieldoption target = context.getMappedInstance( dto, Mergefieldoption.class );
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
        if ( dto.getUuid() != null ) {
            entity.setUuid( dto.getUuid() );
        }
        if ( dto.getLabel() != null ) {
            entity.setLabel( dto.getLabel() );
        }
        if ( dto.getSortorder() != null ) {
            entity.setSortorder( dto.getSortorder() );
        }

        return entity;
    }

    private String entityMergefieldtemplateUuid(Mergefieldoption mergefieldoption) {
        if ( mergefieldoption == null ) {
            return null;
        }
        Mergefieldtemplate mergefieldtemplate = mergefieldoption.getMergefieldtemplate();
        if ( mergefieldtemplate == null ) {
            return null;
        }
        String uuid = mergefieldtemplate.getUuid();
        if ( uuid == null ) {
            return null;
        }
        return uuid;
    }
}
