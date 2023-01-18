package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Datasource;
import com.vesey.documentable.entity.Mergefield;
import com.vesey.documentable.entity.Mergefieldoption;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.dto.DatasourceDTO;
import com.vesey.documentable.entity.dto.MergefieldDTO;
import com.vesey.documentable.entity.dto.MergefieldoptionDTO;
import com.vesey.documentable.entity.dto.MergefieldtemplateDTO;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.processing.Generated;
import javax.enterprise.context.ApplicationScoped;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-18T20:32:00+0000",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 1.4.100.v20220318-0906, environment: Java 11.0.9 (Oracle Corporation)"
)
@ApplicationScoped
public class MergefieldMapperImpl extends MergefieldMapper {

    @Override
    public MergefieldDTO getDTOFromMergefield(Mergefield entity, CycleAvoidingMappingContext context) {
        MergefieldDTO target = context.getMappedInstance( entity, MergefieldDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( entity == null ) {
            return null;
        }

        MergefieldDTO mergefieldDTO = new MergefieldDTO();

        context.storeMappedInstance( entity, mergefieldDTO );

        mergefieldDTO.setVersion( entity.getVersion() );
        mergefieldDTO.setUuid( entity.getUuid() );
        mergefieldDTO.setStringvalue( entity.getStringvalue() );
        mergefieldDTO.setIntegervalue( entity.getIntegervalue() );
        mergefieldDTO.setDecimalvalue( entity.getDecimalvalue() );
        mergefieldDTO.setDatevalue( entity.getDatevalue() );
        mergefieldDTO.setMergefieldtemplate( mergefieldtemplateToMergefieldtemplateDTO( entity.getMergefieldtemplate(), context ) );
        mergefieldDTO.setBooleanvalue( entity.getBooleanvalue() );

        return mergefieldDTO;
    }

    @Override
    public Mergefield getMergefieldFromDTO(MergefieldDTO dto, Mergefield entity, CycleAvoidingMappingContext context) {
        Mergefield target = context.getMappedInstance( dto, Mergefield.class );
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
        if ( dto.getMergefieldtemplate() != null ) {
            if ( entity.getMergefieldtemplate() == null ) {
                entity.setMergefieldtemplate( new Mergefieldtemplate() );
            }
            mergefieldtemplateDTOToMergefieldtemplate( dto.getMergefieldtemplate(), context, entity.getMergefieldtemplate() );
        }
        if ( dto.getUuid() != null ) {
            entity.setUuid( dto.getUuid() );
        }
        if ( dto.getStringvalue() != null ) {
            entity.setStringvalue( dto.getStringvalue() );
        }
        if ( dto.getIntegervalue() != null ) {
            entity.setIntegervalue( dto.getIntegervalue() );
        }
        if ( dto.getDecimalvalue() != null ) {
            entity.setDecimalvalue( dto.getDecimalvalue() );
        }
        if ( dto.getDatevalue() != null ) {
            entity.setDatevalue( dto.getDatevalue() );
        }
        if ( dto.getBooleanvalue() != null ) {
            entity.setBooleanvalue( dto.getBooleanvalue() );
        }

        return entity;
    }

    protected MergefieldoptionDTO mergefieldoptionToMergefieldoptionDTO(Mergefieldoption mergefieldoption, CycleAvoidingMappingContext context) {
        MergefieldoptionDTO target = context.getMappedInstance( mergefieldoption, MergefieldoptionDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( mergefieldoption == null ) {
            return null;
        }

        MergefieldoptionDTO mergefieldoptionDTO = new MergefieldoptionDTO();

        context.storeMappedInstance( mergefieldoption, mergefieldoptionDTO );

        mergefieldoptionDTO.setVersion( mergefieldoption.getVersion() );
        mergefieldoptionDTO.setUuid( mergefieldoption.getUuid() );
        mergefieldoptionDTO.setLabel( mergefieldoption.getLabel() );
        if ( mergefieldoption.getId() != null ) {
            mergefieldoptionDTO.setId( String.valueOf( mergefieldoption.getId() ) );
        }
        mergefieldoptionDTO.setValue( mergefieldoption.getValue() );

        return mergefieldoptionDTO;
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
            collection1.add( mergefieldoptionToMergefieldoptionDTO( mergefieldoption, context ) );
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

    protected MergefieldtemplateDTO mergefieldtemplateToMergefieldtemplateDTO(Mergefieldtemplate mergefieldtemplate, CycleAvoidingMappingContext context) {
        MergefieldtemplateDTO target = context.getMappedInstance( mergefieldtemplate, MergefieldtemplateDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( mergefieldtemplate == null ) {
            return null;
        }

        MergefieldtemplateDTO mergefieldtemplateDTO = new MergefieldtemplateDTO();

        context.storeMappedInstance( mergefieldtemplate, mergefieldtemplateDTO );

        mergefieldtemplateDTO.setVersion( mergefieldtemplate.getVersion() );
        mergefieldtemplateDTO.setUuid( mergefieldtemplate.getUuid() );
        mergefieldtemplateDTO.setKey( mergefieldtemplate.getKey() );
        mergefieldtemplateDTO.setLabel( mergefieldtemplate.getLabel() );
        mergefieldtemplateDTO.setType( mergefieldtemplate.getType() );
        mergefieldtemplateDTO.setOptions( mergefieldoptionCollectionToMergefieldoptionDTOCollection( mergefieldtemplate.getOptions(), context ) );
        mergefieldtemplateDTO.setDescription( mergefieldtemplate.getDescription() );
        mergefieldtemplateDTO.setSamplestringvalue( mergefieldtemplate.getSamplestringvalue() );
        mergefieldtemplateDTO.setSampleintegervalue( mergefieldtemplate.getSampleintegervalue() );
        mergefieldtemplateDTO.setSampledecimalvalue( mergefieldtemplate.getSampledecimalvalue() );
        mergefieldtemplateDTO.setSampledatevalue( mergefieldtemplate.getSampledatevalue() );
        mergefieldtemplateDTO.setSamplebooleanvalue( mergefieldtemplate.getSamplebooleanvalue() );
        mergefieldtemplateDTO.setDatasource( datasourceToDatasourceDTO( mergefieldtemplate.getDatasource(), context ) );

        return mergefieldtemplateDTO;
    }

    protected void datasourceDTOToDatasource(DatasourceDTO datasourceDTO, CycleAvoidingMappingContext context, Datasource mappingTarget) {
        Datasource target = context.getMappedInstance( datasourceDTO, Datasource.class );
        if ( target != null ) {
            return;
        }

        if ( datasourceDTO == null ) {
            return;
        }

        context.storeMappedInstance( datasourceDTO, mappingTarget );

        if ( datasourceDTO.getVersion() != null ) {
            mappingTarget.setVersion( datasourceDTO.getVersion() );
        }
        if ( datasourceDTO.getUuid() != null ) {
            mappingTarget.setUuid( datasourceDTO.getUuid() );
        }
        if ( datasourceDTO.getName() != null ) {
            mappingTarget.setName( datasourceDTO.getName() );
        }
        if ( datasourceDTO.getDescription() != null ) {
            mappingTarget.setDescription( datasourceDTO.getDescription() );
        }
        if ( datasourceDTO.getSortorder() != null ) {
            mappingTarget.setSortorder( datasourceDTO.getSortorder() );
        }
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

    protected void mergefieldtemplateDTOToMergefieldtemplate(MergefieldtemplateDTO mergefieldtemplateDTO, CycleAvoidingMappingContext context, Mergefieldtemplate mappingTarget) {
        Mergefieldtemplate target = context.getMappedInstance( mergefieldtemplateDTO, Mergefieldtemplate.class );
        if ( target != null ) {
            return;
        }

        if ( mergefieldtemplateDTO == null ) {
            return;
        }

        context.storeMappedInstance( mergefieldtemplateDTO, mappingTarget );

        if ( mergefieldtemplateDTO.getVersion() != null ) {
            mappingTarget.setVersion( mergefieldtemplateDTO.getVersion() );
        }
        if ( mergefieldtemplateDTO.getDatasource() != null ) {
            if ( mappingTarget.getDatasource() == null ) {
                mappingTarget.setDatasource( new Datasource() );
            }
            datasourceDTOToDatasource( mergefieldtemplateDTO.getDatasource(), context, mappingTarget.getDatasource() );
        }
        if ( mergefieldtemplateDTO.getUuid() != null ) {
            mappingTarget.setUuid( mergefieldtemplateDTO.getUuid() );
        }
        if ( mergefieldtemplateDTO.getKey() != null ) {
            mappingTarget.setKey( mergefieldtemplateDTO.getKey() );
        }
        if ( mergefieldtemplateDTO.getLabel() != null ) {
            mappingTarget.setLabel( mergefieldtemplateDTO.getLabel() );
        }
        if ( mergefieldtemplateDTO.getType() != null ) {
            mappingTarget.setType( mergefieldtemplateDTO.getType() );
        }
        if ( mergefieldtemplateDTO.getDescription() != null ) {
            mappingTarget.setDescription( mergefieldtemplateDTO.getDescription() );
        }
        if ( mergefieldtemplateDTO.getSamplestringvalue() != null ) {
            mappingTarget.setSamplestringvalue( mergefieldtemplateDTO.getSamplestringvalue() );
        }
        if ( mergefieldtemplateDTO.getSampleintegervalue() != null ) {
            mappingTarget.setSampleintegervalue( mergefieldtemplateDTO.getSampleintegervalue() );
        }
        if ( mergefieldtemplateDTO.getSampledecimalvalue() != null ) {
            mappingTarget.setSampledecimalvalue( mergefieldtemplateDTO.getSampledecimalvalue() );
        }
        if ( mergefieldtemplateDTO.getSampledatevalue() != null ) {
            mappingTarget.setSampledatevalue( mergefieldtemplateDTO.getSampledatevalue() );
        }
        if ( mergefieldtemplateDTO.getSamplebooleanvalue() != null ) {
            mappingTarget.setSamplebooleanvalue( mergefieldtemplateDTO.getSamplebooleanvalue() );
        }
        if ( mappingTarget.getOptions() != null ) {
            Collection<Mergefieldoption> collection = mergefieldoptionDTOCollectionToMergefieldoptionCollection( mergefieldtemplateDTO.getOptions(), context );
            if ( collection != null ) {
                mappingTarget.getOptions().clear();
                mappingTarget.getOptions().addAll( collection );
            }
        }
        else {
            Collection<Mergefieldoption> collection = mergefieldoptionDTOCollectionToMergefieldoptionCollection( mergefieldtemplateDTO.getOptions(), context );
            if ( collection != null ) {
                mappingTarget.setOptions( collection );
            }
        }
    }
}
