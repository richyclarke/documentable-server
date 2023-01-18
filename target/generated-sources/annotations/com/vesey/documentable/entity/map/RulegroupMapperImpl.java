package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Datasource;
import com.vesey.documentable.entity.Mergefieldoption;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.Rule;
import com.vesey.documentable.entity.Rulegroup;
import com.vesey.documentable.entity.dto.DatasourceDTO;
import com.vesey.documentable.entity.dto.MergefieldoptionDTO;
import com.vesey.documentable.entity.dto.MergefieldtemplateDTO;
import com.vesey.documentable.entity.dto.RuleDTO;
import com.vesey.documentable.entity.dto.RulegroupDTO;
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
public class RulegroupMapperImpl extends RulegroupMapper {

    @Override
    public RulegroupDTO getDTOFromRulegroup(Rulegroup entity, CycleAvoidingMappingContext context) {
        RulegroupDTO target = context.getMappedInstance( entity, RulegroupDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( entity == null ) {
            return null;
        }

        RulegroupDTO rulegroupDTO = new RulegroupDTO();

        context.storeMappedInstance( entity, rulegroupDTO );

        rulegroupDTO.setVersion( entity.getVersion() );
        rulegroupDTO.setUuid( entity.getUuid() );
        rulegroupDTO.setRules( ruleCollectionToRuleDTOCollection( entity.getRules(), context ) );

        return rulegroupDTO;
    }

    @Override
    public Rulegroup getRulegroupFromDTO(RulegroupDTO dto, Rulegroup entity, CycleAvoidingMappingContext context) {
        Rulegroup target = context.getMappedInstance( dto, Rulegroup.class );
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

    protected RuleDTO ruleToRuleDTO(Rule rule, CycleAvoidingMappingContext context) {
        RuleDTO target = context.getMappedInstance( rule, RuleDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( rule == null ) {
            return null;
        }

        RuleDTO ruleDTO = new RuleDTO();

        context.storeMappedInstance( rule, ruleDTO );

        ruleDTO.setVersion( rule.getVersion() );
        ruleDTO.setUuid( rule.getUuid() );
        ruleDTO.setSource( mergefieldtemplateToMergefieldtemplateDTO( rule.getSource(), context ) );
        ruleDTO.setOperand( rule.getOperand() );
        ruleDTO.setStringvalue( rule.getStringvalue() );
        ruleDTO.setBooleanvalue( rule.getBooleanvalue() );
        ruleDTO.setDecimalvalue( rule.getDecimalvalue() );
        ruleDTO.setDatevalue( rule.getDatevalue() );
        ruleDTO.setIntegervalue( rule.getIntegervalue() );

        return ruleDTO;
    }

    protected Collection<RuleDTO> ruleCollectionToRuleDTOCollection(Collection<Rule> collection, CycleAvoidingMappingContext context) {
        Collection<RuleDTO> target = context.getMappedInstance( collection, Collection.class );
        if ( target != null ) {
            return target;
        }

        if ( collection == null ) {
            return null;
        }

        Collection<RuleDTO> collection1 = new ArrayList<RuleDTO>( collection.size() );
        context.storeMappedInstance( collection, collection1 );

        for ( Rule rule : collection ) {
            collection1.add( ruleToRuleDTO( rule, context ) );
        }

        return collection1;
    }
}
