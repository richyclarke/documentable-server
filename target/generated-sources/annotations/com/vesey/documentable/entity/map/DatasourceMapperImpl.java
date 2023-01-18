package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Datasource;
import com.vesey.documentable.entity.dto.DatasourceDTO;
import javax.annotation.processing.Generated;
import javax.enterprise.context.ApplicationScoped;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-18T18:22:14+0000",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 3.33.0.v20221215-1352, environment: Java 17.0.5 (Eclipse Adoptium)"
)
@ApplicationScoped
public class DatasourceMapperImpl extends DatasourceMapper {

    @Override
    public DatasourceDTO getDTOFromDatasource(Datasource entity, CycleAvoidingMappingContext context) {
        DatasourceDTO target = context.getMappedInstance( entity, DatasourceDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( entity == null ) {
            return null;
        }

        DatasourceDTO datasourceDTO = new DatasourceDTO();

        context.storeMappedInstance( entity, datasourceDTO );

        datasourceDTO.setVersion( entity.getVersion() );
        datasourceDTO.setUuid( entity.getUuid() );
        datasourceDTO.setName( entity.getName() );
        datasourceDTO.setDescription( entity.getDescription() );
        datasourceDTO.setSortorder( entity.getSortorder() );

        return datasourceDTO;
    }

    @Override
    public Datasource getDatasourceFromDTO(DatasourceDTO dto, Datasource entity, CycleAvoidingMappingContext context) {
        Datasource target = context.getMappedInstance( dto, Datasource.class );
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
        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getSortorder() != null ) {
            entity.setSortorder( dto.getSortorder() );
        }

        return entity;
    }
}