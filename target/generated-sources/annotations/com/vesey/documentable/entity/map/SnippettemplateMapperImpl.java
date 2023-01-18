package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Datasource;
import com.vesey.documentable.entity.Documenttemplate;
import com.vesey.documentable.entity.Mergefieldoption;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.Rule;
import com.vesey.documentable.entity.Rulegroup;
import com.vesey.documentable.entity.Ruleset;
import com.vesey.documentable.entity.Snippettemplate;
import com.vesey.documentable.entity.dto.DatasourceDTO;
import com.vesey.documentable.entity.dto.MergefieldoptionDTO;
import com.vesey.documentable.entity.dto.MergefieldtemplateDTO;
import com.vesey.documentable.entity.dto.RuleDTO;
import com.vesey.documentable.entity.dto.RulegroupDTO;
import com.vesey.documentable.entity.dto.RulesetDTO;
import com.vesey.documentable.entity.dto.SnippettemplateDTO;
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
public class SnippettemplateMapperImpl extends SnippettemplateMapper {

    @Override
    public SnippettemplateDTO getDTOFromSnippettemplate(Snippettemplate entity, CycleAvoidingMappingContext context) {
        SnippettemplateDTO target = context.getMappedInstance( entity, SnippettemplateDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( entity == null ) {
            return null;
        }

        SnippettemplateDTO snippettemplateDTO = new SnippettemplateDTO();

        context.storeMappedInstance( entity, snippettemplateDTO );

        snippettemplateDTO.setDocumenttemplateUuid( entityDocumenttemplateUuid( entity ) );
        snippettemplateDTO.setParentUuid( entityParentUuid( entity ) );
        snippettemplateDTO.setVersion( entity.getVersion() );
        snippettemplateDTO.setUuid( entity.getUuid() );
        snippettemplateDTO.setName( entity.getName() );
        snippettemplateDTO.setDescription( entity.getDescription() );
        snippettemplateDTO.setContent( entity.getContent() );
        snippettemplateDTO.setSortorder( entity.getSortorder() );
        snippettemplateDTO.setSnippettemplates( snippettemplateCollectionToSnippettemplateDTOCollection( entity.getSnippettemplates(), context ) );
        snippettemplateDTO.setRuleset( rulesetToRulesetDTO( entity.getRuleset(), context ) );
        snippettemplateDTO.setNumbered( entity.isNumbered() );

        return snippettemplateDTO;
    }

    @Override
    public Snippettemplate getSnippettemplateFromDTO(SnippettemplateDTO dto, Snippettemplate entity, CycleAvoidingMappingContext context) {
        Snippettemplate target = context.getMappedInstance( dto, Snippettemplate.class );
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
        if ( dto.getRuleset() != null ) {
            if ( entity.getRuleset() == null ) {
                entity.setRuleset( new Ruleset() );
            }
            rulesetDTOToRuleset( dto.getRuleset(), context, entity.getRuleset() );
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
        if ( dto.getContent() != null ) {
            entity.setContent( dto.getContent() );
        }
        if ( dto.getSortorder() != null ) {
            entity.setSortorder( dto.getSortorder() );
        }
        entity.setNumbered( dto.isNumbered() );
        if ( entity.getSnippettemplates() != null ) {
            Collection<Snippettemplate> collection = snippettemplateDTOCollectionToSnippettemplateCollection( dto.getSnippettemplates(), context );
            if ( collection != null ) {
                entity.getSnippettemplates().clear();
                entity.getSnippettemplates().addAll( collection );
            }
        }
        else {
            Collection<Snippettemplate> collection = snippettemplateDTOCollectionToSnippettemplateCollection( dto.getSnippettemplates(), context );
            if ( collection != null ) {
                entity.setSnippettemplates( collection );
            }
        }

        return entity;
    }

    private String entityDocumenttemplateUuid(Snippettemplate snippettemplate) {
        if ( snippettemplate == null ) {
            return null;
        }
        Documenttemplate documenttemplate = snippettemplate.getDocumenttemplate();
        if ( documenttemplate == null ) {
            return null;
        }
        String uuid = documenttemplate.getUuid();
        if ( uuid == null ) {
            return null;
        }
        return uuid;
    }

    private String entityParentUuid(Snippettemplate snippettemplate) {
        if ( snippettemplate == null ) {
            return null;
        }
        Snippettemplate parent = snippettemplate.getParent();
        if ( parent == null ) {
            return null;
        }
        String uuid = parent.getUuid();
        if ( uuid == null ) {
            return null;
        }
        return uuid;
    }

    protected Collection<SnippettemplateDTO> snippettemplateCollectionToSnippettemplateDTOCollection(Collection<Snippettemplate> collection, CycleAvoidingMappingContext context) {
        Collection<SnippettemplateDTO> target = context.getMappedInstance( collection, Collection.class );
        if ( target != null ) {
            return target;
        }

        if ( collection == null ) {
            return null;
        }

        Collection<SnippettemplateDTO> collection1 = new ArrayList<SnippettemplateDTO>( collection.size() );
        context.storeMappedInstance( collection, collection1 );

        for ( Snippettemplate snippettemplate : collection ) {
            collection1.add( getDTOFromSnippettemplate( snippettemplate, context ) );
        }

        return collection1;
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

    protected RulegroupDTO rulegroupToRulegroupDTO(Rulegroup rulegroup, CycleAvoidingMappingContext context) {
        RulegroupDTO target = context.getMappedInstance( rulegroup, RulegroupDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( rulegroup == null ) {
            return null;
        }

        RulegroupDTO rulegroupDTO = new RulegroupDTO();

        context.storeMappedInstance( rulegroup, rulegroupDTO );

        rulegroupDTO.setVersion( rulegroup.getVersion() );
        rulegroupDTO.setUuid( rulegroup.getUuid() );
        rulegroupDTO.setRules( ruleCollectionToRuleDTOCollection( rulegroup.getRules(), context ) );

        return rulegroupDTO;
    }

    protected Collection<RulegroupDTO> rulegroupCollectionToRulegroupDTOCollection(Collection<Rulegroup> collection, CycleAvoidingMappingContext context) {
        Collection<RulegroupDTO> target = context.getMappedInstance( collection, Collection.class );
        if ( target != null ) {
            return target;
        }

        if ( collection == null ) {
            return null;
        }

        Collection<RulegroupDTO> collection1 = new ArrayList<RulegroupDTO>( collection.size() );
        context.storeMappedInstance( collection, collection1 );

        for ( Rulegroup rulegroup : collection ) {
            collection1.add( rulegroupToRulegroupDTO( rulegroup, context ) );
        }

        return collection1;
    }

    protected RulesetDTO rulesetToRulesetDTO(Ruleset ruleset, CycleAvoidingMappingContext context) {
        RulesetDTO target = context.getMappedInstance( ruleset, RulesetDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( ruleset == null ) {
            return null;
        }

        RulesetDTO rulesetDTO = new RulesetDTO();

        context.storeMappedInstance( ruleset, rulesetDTO );

        rulesetDTO.setVersion( ruleset.getVersion() );
        rulesetDTO.setUuid( ruleset.getUuid() );
        rulesetDTO.setOperand( ruleset.isOperand() );
        rulesetDTO.setRulegroups( rulegroupCollectionToRulegroupDTOCollection( ruleset.getRulegroups(), context ) );

        return rulesetDTO;
    }

    protected Datasource datasourceDTOToDatasource(DatasourceDTO datasourceDTO, CycleAvoidingMappingContext context) {
        Datasource target = context.getMappedInstance( datasourceDTO, Datasource.class );
        if ( target != null ) {
            return target;
        }

        if ( datasourceDTO == null ) {
            return null;
        }

        Datasource datasource = new Datasource();

        context.storeMappedInstance( datasourceDTO, datasource );

        datasource.setVersion( datasourceDTO.getVersion() );
        datasource.setUuid( datasourceDTO.getUuid() );
        datasource.setName( datasourceDTO.getName() );
        datasource.setDescription( datasourceDTO.getDescription() );
        datasource.setSortorder( datasourceDTO.getSortorder() );

        return datasource;
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

    protected Mergefieldtemplate mergefieldtemplateDTOToMergefieldtemplate(MergefieldtemplateDTO mergefieldtemplateDTO, CycleAvoidingMappingContext context) {
        Mergefieldtemplate target = context.getMappedInstance( mergefieldtemplateDTO, Mergefieldtemplate.class );
        if ( target != null ) {
            return target;
        }

        if ( mergefieldtemplateDTO == null ) {
            return null;
        }

        Mergefieldtemplate mergefieldtemplate = new Mergefieldtemplate();

        context.storeMappedInstance( mergefieldtemplateDTO, mergefieldtemplate );

        mergefieldtemplate.setVersion( mergefieldtemplateDTO.getVersion() );
        mergefieldtemplate.setDatasource( datasourceDTOToDatasource( mergefieldtemplateDTO.getDatasource(), context ) );
        mergefieldtemplate.setUuid( mergefieldtemplateDTO.getUuid() );
        mergefieldtemplate.setKey( mergefieldtemplateDTO.getKey() );
        mergefieldtemplate.setLabel( mergefieldtemplateDTO.getLabel() );
        mergefieldtemplate.setType( mergefieldtemplateDTO.getType() );
        mergefieldtemplate.setDescription( mergefieldtemplateDTO.getDescription() );
        mergefieldtemplate.setSamplestringvalue( mergefieldtemplateDTO.getSamplestringvalue() );
        mergefieldtemplate.setSampleintegervalue( mergefieldtemplateDTO.getSampleintegervalue() );
        mergefieldtemplate.setSampledecimalvalue( mergefieldtemplateDTO.getSampledecimalvalue() );
        mergefieldtemplate.setSampledatevalue( mergefieldtemplateDTO.getSampledatevalue() );
        mergefieldtemplate.setSamplebooleanvalue( mergefieldtemplateDTO.getSamplebooleanvalue() );
        mergefieldtemplate.setOptions( mergefieldoptionDTOCollectionToMergefieldoptionCollection( mergefieldtemplateDTO.getOptions(), context ) );

        return mergefieldtemplate;
    }

    protected Rule ruleDTOToRule(RuleDTO ruleDTO, CycleAvoidingMappingContext context) {
        Rule target = context.getMappedInstance( ruleDTO, Rule.class );
        if ( target != null ) {
            return target;
        }

        if ( ruleDTO == null ) {
            return null;
        }

        Rule rule = new Rule();

        context.storeMappedInstance( ruleDTO, rule );

        rule.setVersion( ruleDTO.getVersion() );
        rule.setSource( mergefieldtemplateDTOToMergefieldtemplate( ruleDTO.getSource(), context ) );
        rule.setUuid( ruleDTO.getUuid() );
        rule.setOperand( ruleDTO.getOperand() );
        rule.setStringvalue( ruleDTO.getStringvalue() );
        rule.setBooleanvalue( ruleDTO.getBooleanvalue() );
        rule.setDecimalvalue( ruleDTO.getDecimalvalue() );
        rule.setDatevalue( ruleDTO.getDatevalue() );
        rule.setIntegervalue( ruleDTO.getIntegervalue() );

        return rule;
    }

    protected Collection<Rule> ruleDTOCollectionToRuleCollection(Collection<RuleDTO> collection, CycleAvoidingMappingContext context) {
        Collection<Rule> target = context.getMappedInstance( collection, Collection.class );
        if ( target != null ) {
            return target;
        }

        if ( collection == null ) {
            return null;
        }

        Collection<Rule> collection1 = new ArrayList<Rule>( collection.size() );
        context.storeMappedInstance( collection, collection1 );

        for ( RuleDTO ruleDTO : collection ) {
            collection1.add( ruleDTOToRule( ruleDTO, context ) );
        }

        return collection1;
    }

    protected Rulegroup rulegroupDTOToRulegroup(RulegroupDTO rulegroupDTO, CycleAvoidingMappingContext context) {
        Rulegroup target = context.getMappedInstance( rulegroupDTO, Rulegroup.class );
        if ( target != null ) {
            return target;
        }

        if ( rulegroupDTO == null ) {
            return null;
        }

        Rulegroup rulegroup = new Rulegroup();

        context.storeMappedInstance( rulegroupDTO, rulegroup );

        rulegroup.setVersion( rulegroupDTO.getVersion() );
        rulegroup.setUuid( rulegroupDTO.getUuid() );
        rulegroup.setRules( ruleDTOCollectionToRuleCollection( rulegroupDTO.getRules(), context ) );

        return rulegroup;
    }

    protected Collection<Rulegroup> rulegroupDTOCollectionToRulegroupCollection(Collection<RulegroupDTO> collection, CycleAvoidingMappingContext context) {
        Collection<Rulegroup> target = context.getMappedInstance( collection, Collection.class );
        if ( target != null ) {
            return target;
        }

        if ( collection == null ) {
            return null;
        }

        Collection<Rulegroup> collection1 = new ArrayList<Rulegroup>( collection.size() );
        context.storeMappedInstance( collection, collection1 );

        for ( RulegroupDTO rulegroupDTO : collection ) {
            collection1.add( rulegroupDTOToRulegroup( rulegroupDTO, context ) );
        }

        return collection1;
    }

    protected void rulesetDTOToRuleset(RulesetDTO rulesetDTO, CycleAvoidingMappingContext context, Ruleset mappingTarget) {
        Ruleset target = context.getMappedInstance( rulesetDTO, Ruleset.class );
        if ( target != null ) {
            return;
        }

        if ( rulesetDTO == null ) {
            return;
        }

        context.storeMappedInstance( rulesetDTO, mappingTarget );

        if ( rulesetDTO.getVersion() != null ) {
            mappingTarget.setVersion( rulesetDTO.getVersion() );
        }
        if ( rulesetDTO.getUuid() != null ) {
            mappingTarget.setUuid( rulesetDTO.getUuid() );
        }
        mappingTarget.setOperand( rulesetDTO.isOperand() );
        if ( mappingTarget.getRulegroups() != null ) {
            Collection<Rulegroup> collection = rulegroupDTOCollectionToRulegroupCollection( rulesetDTO.getRulegroups(), context );
            if ( collection != null ) {
                mappingTarget.getRulegroups().clear();
                mappingTarget.getRulegroups().addAll( collection );
            }
        }
        else {
            Collection<Rulegroup> collection = rulegroupDTOCollectionToRulegroupCollection( rulesetDTO.getRulegroups(), context );
            if ( collection != null ) {
                mappingTarget.setRulegroups( collection );
            }
        }
    }

    protected Ruleset rulesetDTOToRuleset1(RulesetDTO rulesetDTO, CycleAvoidingMappingContext context) {
        Ruleset target = context.getMappedInstance( rulesetDTO, Ruleset.class );
        if ( target != null ) {
            return target;
        }

        if ( rulesetDTO == null ) {
            return null;
        }

        Ruleset ruleset = new Ruleset();

        context.storeMappedInstance( rulesetDTO, ruleset );

        ruleset.setVersion( rulesetDTO.getVersion() );
        ruleset.setUuid( rulesetDTO.getUuid() );
        ruleset.setOperand( rulesetDTO.isOperand() );
        ruleset.setRulegroups( rulegroupDTOCollectionToRulegroupCollection( rulesetDTO.getRulegroups(), context ) );

        return ruleset;
    }

    protected Collection<Snippettemplate> snippettemplateDTOCollectionToSnippettemplateCollection(Collection<SnippettemplateDTO> collection, CycleAvoidingMappingContext context) {
        Collection<Snippettemplate> target = context.getMappedInstance( collection, Collection.class );
        if ( target != null ) {
            return target;
        }

        if ( collection == null ) {
            return null;
        }

        Collection<Snippettemplate> collection1 = new ArrayList<Snippettemplate>( collection.size() );
        context.storeMappedInstance( collection, collection1 );

        for ( SnippettemplateDTO snippettemplateDTO : collection ) {
            collection1.add( snippettemplateDTOToSnippettemplate( snippettemplateDTO, context ) );
        }

        return collection1;
    }

    protected Snippettemplate snippettemplateDTOToSnippettemplate(SnippettemplateDTO snippettemplateDTO, CycleAvoidingMappingContext context) {
        Snippettemplate target = context.getMappedInstance( snippettemplateDTO, Snippettemplate.class );
        if ( target != null ) {
            return target;
        }

        if ( snippettemplateDTO == null ) {
            return null;
        }

        Snippettemplate snippettemplate = new Snippettemplate();

        context.storeMappedInstance( snippettemplateDTO, snippettemplate );

        snippettemplate.setVersion( snippettemplateDTO.getVersion() );
        snippettemplate.setRuleset( rulesetDTOToRuleset1( snippettemplateDTO.getRuleset(), context ) );
        snippettemplate.setUuid( snippettemplateDTO.getUuid() );
        snippettemplate.setName( snippettemplateDTO.getName() );
        snippettemplate.setDescription( snippettemplateDTO.getDescription() );
        snippettemplate.setContent( snippettemplateDTO.getContent() );
        snippettemplate.setSortorder( snippettemplateDTO.getSortorder() );
        snippettemplate.setNumbered( snippettemplateDTO.isNumbered() );
        snippettemplate.setSnippettemplates( snippettemplateDTOCollectionToSnippettemplateCollection( snippettemplateDTO.getSnippettemplates(), context ) );

        return snippettemplate;
    }
}
