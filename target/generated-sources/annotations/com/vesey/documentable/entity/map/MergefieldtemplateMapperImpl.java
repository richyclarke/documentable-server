package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Datasource;
import com.vesey.documentable.entity.Mergefieldoption;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.dto.DatasourceDTO;
import com.vesey.documentable.entity.dto.MergefieldoptionDTO;
import com.vesey.documentable.entity.dto.MergefieldtemplateDTO;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.processing.Generated;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-02-13T20:27:22+0000",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 1.4.100.v20220318-0906, environment: Java 11.0.9 (Oracle Corporation)"
)
@ApplicationScoped
public class MergefieldtemplateMapperImpl extends MergefieldtemplateMapper {

    @Inject
    private MergefieldoptionMapper mergefieldoptionMapper;

    @Override
    public MergefieldtemplateDTO getDTOFromMergefieldtemplate(Mergefieldtemplate entity, CycleAvoidingMappingContext context) {
        MergefieldtemplateDTO target = context.getMappedInstance( entity, MergefieldtemplateDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( entity == null ) {
            return null;
        }

        MergefieldtemplateDTO mergefieldtemplateDTO = new MergefieldtemplateDTO();

        context.storeMappedInstance( entity, mergefieldtemplateDTO );

        mergefieldtemplateDTO.setDatasourceUuid( entityDatasourceUuid( entity ) );
        mergefieldtemplateDTO.setUuid( entity.getUuid() );
        mergefieldtemplateDTO.setVersion( entity.getVersion() );
        mergefieldtemplateDTO.setDatasource( datasourceToDatasourceDTO( entity.getDatasource(), context ) );
        mergefieldtemplateDTO.setDescription( entity.getDescription() );
        mergefieldtemplateDTO.setKey( entity.getKey() );
        mergefieldtemplateDTO.setLabel( entity.getLabel() );
        mergefieldtemplateDTO.setOptions( mergefieldoptionCollectionToMergefieldoptionDTOCollection( entity.getOptions(), context ) );
        mergefieldtemplateDTO.setSamplebooleanvalue( entity.getSamplebooleanvalue() );
        mergefieldtemplateDTO.setSampledatevalue( entity.getSampledatevalue() );
        mergefieldtemplateDTO.setSampledecimalvalue( entity.getSampledecimalvalue() );
        mergefieldtemplateDTO.setSampleintegervalue( entity.getSampleintegervalue() );
        mergefieldtemplateDTO.setSamplestringvalue( entity.getSamplestringvalue() );
        mergefieldtemplateDTO.setType( entity.getType() );

        return mergefieldtemplateDTO;
    }

    @Override
    public Mergefieldtemplate getMergefieldtemplateFromDTO(MergefieldtemplateDTO dto, Mergefieldtemplate entity, CycleAvoidingMappingContext context) {
        Mergefieldtemplate target = context.getMappedInstance( dto, Mergefieldtemplate.class );
        if ( target != null ) {
            return target;
        }

        if ( dto == null ) {
            return null;
        }

        context.storeMappedInstance( dto, entity );

        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getKey() != null ) {
            entity.setKey( dto.getKey() );
        }
        if ( dto.getLabel() != null ) {
            entity.setLabel( dto.getLabel() );
        }
        if ( dto.getSamplebooleanvalue() != null ) {
            entity.setSamplebooleanvalue( dto.getSamplebooleanvalue() );
        }
        if ( dto.getSampledatevalue() != null ) {
            entity.setSampledatevalue( dto.getSampledatevalue() );
        }
        if ( dto.getSampledecimalvalue() != null ) {
            entity.setSampledecimalvalue( dto.getSampledecimalvalue() );
        }
        if ( dto.getSampleintegervalue() != null ) {
            entity.setSampleintegervalue( dto.getSampleintegervalue() );
        }
        if ( dto.getSamplestringvalue() != null ) {
            entity.setSamplestringvalue( dto.getSamplestringvalue() );
        }
        if ( dto.getType() != null ) {
            entity.setType( dto.getType() );
        }
        if ( dto.getUuid() != null ) {
            entity.setUuid( dto.getUuid() );
        }
        if ( dto.getVersion() != null ) {
            entity.setVersion( dto.getVersion() );
        }

        return entity;
    }

    private String entityDatasourceUuid(Mergefieldtemplate mergefieldtemplate) {
        if ( mergefieldtemplate == null ) {
            return null;
        }
        Datasource datasource = mergefieldtemplate.getDatasource();
        if ( datasource == null ) {
            return null;
        }
        String uuid = datasource.getUuid();
        if ( uuid == null ) {
            return null;
        }
        return uuid;
    }

    protected DatasourceDTO datasourceToDatasourceDTO(Datasource datasource, CycleAvoidingMappingContext context) {
        DatasourceDTO target = context.getMappedInstance( datasource, DatasourceDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( datasource == null ) {
            return null;
        }

        DatasourceDTO datasourceDTO = new DatasourceDTO();

        context.storeMappedInstance( datasource, datasourceDTO );

        datasourceDTO.setUuid( datasource.getUuid() );
        datasourceDTO.setVersion( datasource.getVersion() );
        datasourceDTO.setDescription( datasource.getDescription() );
        datasourceDTO.setName( datasource.getName() );
        datasourceDTO.setSortorder( datasource.getSortorder() );

        return datasourceDTO;
    }

    protected Collection<MergefieldoptionDTO> mergefieldoptionCollectionToMergefieldoptionDTOCollection(Collection<Mergefieldoption> collection, CycleAvoidingMappingContext context) {
        Collection<MergefieldoptionDTO> target = context.getMappedInstance( collection, Collection.class );
        if ( target != null ) {
            return target;
        }

        if ( collection == null ) {
            return null;
        }

        Collection<MergefieldoptionDTO> collection1 = new ArrayList<MergefieldoptionDTO>( collection.size() );
        context.storeMappedInstance( collection, collection1 );

        for ( Mergefieldoption mergefieldoption : collection ) {
            collection1.add( mergefieldoptionMapper.getDTOFromMergefieldoption( mergefieldoption, context ) );
        }

        return collection1;
    }
}
