package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Datasource;
import com.vesey.documentable.entity.Mergefieldoption;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.Rule;
import com.vesey.documentable.entity.dto.DatasourceDTO;
import com.vesey.documentable.entity.dto.MergefieldoptionDTO;
import com.vesey.documentable.entity.dto.MergefieldtemplateDTO;
import com.vesey.documentable.entity.dto.RuleDTO;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.processing.Generated;
import javax.enterprise.context.ApplicationScoped;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-18T18:22:14+0000",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 3.33.0.v20221215-1352, environment: Java 17.0.5 (Eclipse Adoptium)"
)
@ApplicationScoped
public class RuleMapperImpl extends RuleMapper {

    @Override
    public RuleDTO getDTOFromRule(Rule entity, CycleAvoidingMappingContext context) {
        RuleDTO target = context.getMappedInstance( entity, RuleDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( entity == null ) {
            return null;
        }

        RuleDTO ruleDTO = new RuleDTO();

        context.storeMappedInstance( entity, ruleDTO );

        ruleDTO.setVersion( entity.getVersion() );
        ruleDTO.setUuid( entity.getUuid() );
        ruleDTO.setSource( mergefieldtemplateToMergefieldtemplateDTO( entity.getSource(), context ) );
        ruleDTO.setOperand( entity.getOperand() );
        ruleDTO.setStringvalue( entity.getStringvalue() );
        ruleDTO.setBooleanvalue( entity.getBooleanvalue() );
        ruleDTO.setDecimalvalue( entity.getDecimalvalue() );
        ruleDTO.setDatevalue( entity.getDatevalue() );
        ruleDTO.setIntegervalue( entity.getIntegervalue() );

        return ruleDTO;
    }

    @Override
    public Rule getRuleFromDTO(RuleDTO dto, Rule entity, CycleAvoidingMappingContext context) {
        Rule target = context.getMappedInstance( dto, Rule.class );
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
        if ( dto.getOperand() != null ) {
            entity.setOperand( dto.getOperand() );
        }
        if ( dto.getStringvalue() != null ) {
            entity.setStringvalue( dto.getStringvalue() );
        }
        if ( dto.getBooleanvalue() != null ) {
            entity.setBooleanvalue( dto.getBooleanvalue() );
        }
        if ( dto.getDecimalvalue() != null ) {
            entity.setDecimalvalue( dto.getDecimalvalue() );
        }
        if ( dto.getDatevalue() != null ) {
            entity.setDatevalue( dto.getDatevalue() );
        }
        if ( dto.getIntegervalue() != null ) {
            entity.setIntegervalue( dto.getIntegervalue() );
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
}
