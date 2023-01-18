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
    date = "2023-01-18T20:32:00+0000",
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
        mergefieldtemplateDTO.setVersion( entity.getVersion() );
        mergefieldtemplateDTO.setUuid( entity.getUuid() );
        mergefieldtemplateDTO.setKey( entity.getKey() );
        mergefieldtemplateDTO.setLabel( entity.getLabel() );
        mergefieldtemplateDTO.setType( entity.getType() );
        mergefieldtemplateDTO.setOptions( mergefieldoptionCollectionToMergefieldoptionDTOCollection( entity.getOptions(), context ) );
        mergefieldtemplateDTO.setDescription( entity.getDescription() );
        mergefieldtemplateDTO.setSamplestringvalue( entity.getSamplestringvalue() );
        mergefieldtemplateDTO.setSampleintegervalue( entity.getSampleintegervalue() );
        mergefieldtemplateDTO.setSampledecimalvalue( entity.getSampledecimalvalue() );
        mergefieldtemplateDTO.setSampledatevalue( entity.getSampledatevalue() );
        mergefieldtemplateDTO.setSamplebooleanvalue( entity.getSamplebooleanvalue() );
        mergefieldtemplateDTO.setDatasource( datasourceToDatasourceDTO( entity.getDatasource(), context ) );

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

        if ( dto.getVersion() != null ) {
            entity.setVersion( dto.getVersion() );
        }
        if ( dto.getUuid() != null ) {
            entity.setUuid( dto.getUuid() );
        }
        if ( dto.getKey() != null ) {
            entity.setKey( dto.getKey() );
        }
        if ( dto.getLabel() != null ) {
            entity.setLabel( dto.getLabel() );
        }
        if ( dto.getType() != null ) {
            entity.setType( dto.getType() );
        }
        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getSamplestringvalue() != null ) {
            entity.setSamplestringvalue( dto.getSamplestringvalue() );
        }
        if ( dto.getSampleintegervalue() != null ) {
            entity.setSampleintegervalue( dto.getSampleintegervalue() );
        }
        if ( dto.getSampledecimalvalue() != null ) {
            entity.setSampledecimalvalue( dto.getSampledecimalvalue() );
        }
        if ( dto.getSampledatevalue() != null ) {
            entity.setSampledatevalue( dto.getSampledatevalue() );
        }
        if ( dto.getSamplebooleanvalue() != null ) {
            entity.setSamplebooleanvalue( dto.getSamplebooleanvalue() );
        }
        if ( entity.getOptions() != null ) {
            Collection<Mergefieldoption> collection = mergefieldoptionDTOCollectionToMergefieldoptionCollection( dto.getOptions(), context );
            if ( collection != null ) {
                entity.getOptions().clear();
                entity.getOptions().addAll( collection );
            }
        }
        else {
            Collection<Mergefieldoption> collection = mergefieldoptionDTOCollectionToMergefieldoptionCollection( dto.getOptions(), context );
            if ( collection != null ) {
                entity.setOptions( collection );
            }
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

        datasourceDTO.setVersion( datasource.getVersion() );
        datasourceDTO.setUuid( datasource.getUuid() );
        datasourceDTO.setName( datasource.getName() );
        datasourceDTO.setDescription( datasource.getDescription() );
        datasourceDTO.setSortorder( datasource.getSortorder() );

        return datasourceDTO;
    }

    protected Mergefieldoption mergefieldoptionDTOToMergefieldoption(MergefieldoptionDTO mergefieldoptionDTO, CycleAvoidingMappingContext context) {
        Mergefieldoption target = context.getMappedInstance( mergefieldoptionDTO, Mergefieldoption.class );
        if ( target != null ) {
            return target;
        }

        if ( mergefieldoptionDTO == null ) {
            return null;
        }

        Mergefieldoption mergefieldoption = new Mergefieldoption();

        context.storeMappedInstance( mergefieldoptionDTO, mergefieldoption );

        if ( mergefieldoptionDTO.getId() != null ) {
            mergefieldoption.setId( Integer.parseInt( mergefieldoptionDTO.getId() ) );
        }
        mergefieldoption.setVersion( mergefieldoptionDTO.getVersion() );
        mergefieldoption.setUuid( mergefieldoptionDTO.getUuid() );
        mergefieldoption.setValue( mergefieldoptionDTO.getValue() );
        mergefieldoption.setLabel( mergefieldoptionDTO.getLabel() );

        return mergefieldoption;
    }

    protected Collection<Mergefieldoption> mergefieldoptionDTOCollectionToMergefieldoptionCollection(Collection<MergefieldoptionDTO> collection, CycleAvoidingMappingContext context) {
        Collection<Mergefieldoption> target = context.getMappedInstance( collection, Collection.class );
        if ( target != null ) {
            return target;
        }

        if ( collection == null ) {
            return null;
        }

        Collection<Mergefieldoption> collection1 = new ArrayList<Mergefieldoption>( collection.size() );
        context.storeMappedInstance( collection, collection1 );

        for ( MergefieldoptionDTO mergefieldoptionDTO : collection ) {
            collection1.add( mergefieldoptionDTOToMergefieldoption( mergefieldoptionDTO, context ) );
        }

        return collection1;
    }
}
