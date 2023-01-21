package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Mergefieldoption;
import com.vesey.documentable.entity.dto.MergefieldoptionDTO;
import javax.annotation.processing.Generated;
import javax.enterprise.context.ApplicationScoped;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-21T15:22:47+0000",
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

        mergefieldoptionDTO.setId( entity.getValue() );
        mergefieldoptionDTO.setVersion( entity.getVersion() );
        mergefieldoptionDTO.setUuid( entity.getUuid() );
        mergefieldoptionDTO.setLabel( entity.getLabel() );
        mergefieldoptionDTO.setValue( entity.getValue() );

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

        if ( dto.getId() != null ) {
            entity.setId( Integer.parseInt( dto.getId() ) );
        }
        if ( dto.getVersion() != null ) {
            entity.setVersion( dto.getVersion() );
        }
        if ( dto.getUuid() != null ) {
            entity.setUuid( dto.getUuid() );
        }
        if ( dto.getValue() != null ) {
            entity.setValue( dto.getValue() );
        }
        if ( dto.getLabel() != null ) {
            entity.setLabel( dto.getLabel() );
        }

        return entity;
    }
}
