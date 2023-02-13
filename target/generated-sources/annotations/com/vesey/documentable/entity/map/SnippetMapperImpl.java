package com.vesey.documentable.entity.map;

import com.vesey.documentable.entity.Document;
import com.vesey.documentable.entity.Snippet;
import com.vesey.documentable.entity.Snippettemplate;
import com.vesey.documentable.entity.dto.SnippetDTO;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.processing.Generated;
import javax.enterprise.context.ApplicationScoped;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-02-07T15:58:59+0000",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 1.4.100.v20220318-0906, environment: Java 11.0.9 (Oracle Corporation)"
)
@ApplicationScoped
public class SnippetMapperImpl extends SnippetMapper {

    @Override
    public SnippetDTO getDTOFromSnippet(Snippet entity, CycleAvoidingMappingContext context) {
        SnippetDTO target = context.getMappedInstance( entity, SnippetDTO.class );
        if ( target != null ) {
            return target;
        }

        if ( entity == null ) {
            return null;
        }

        SnippetDTO snippetDTO = new SnippetDTO();

        context.storeMappedInstance( entity, snippetDTO );

        snippetDTO.setSnippettemplateUuid( entitySnippettemplateUuid( entity ) );
        snippetDTO.setDocumentUuid( entityDocumentUuid( entity ) );
        snippetDTO.setVersion( entity.getVersion() );
        snippetDTO.setUuid( entity.getUuid() );
        snippetDTO.setName( entity.getName() );
        snippetDTO.setDescription( entity.getDescription() );
        snippetDTO.setSnippets( snippetCollectionToSnippetDTOCollection( entity.getSnippets(), context ) );
        snippetDTO.setContent( entity.getContent() );
        snippetDTO.setSortorder( entity.getSortorder() );
        snippetDTO.setNumbered( entity.isNumbered() );

        return snippetDTO;
    }

    @Override
    public Snippet getSnippetFromDTO(SnippetDTO dto, Snippet entity, CycleAvoidingMappingContext context) {
        Snippet target = context.getMappedInstance( dto, Snippet.class );
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
        if ( dto.getContent() != null ) {
            entity.setContent( dto.getContent() );
        }
        if ( dto.getSortorder() != null ) {
            entity.setSortorder( dto.getSortorder() );
        }
        entity.setNumbered( dto.isNumbered() );
        if ( entity.getSnippets() != null ) {
            Collection<Snippet> collection = snippetDTOCollectionToSnippetCollection( dto.getSnippets(), context );
            if ( collection != null ) {
                entity.getSnippets().clear();
                entity.getSnippets().addAll( collection );
            }
        }
        else {
            Collection<Snippet> collection = snippetDTOCollectionToSnippetCollection( dto.getSnippets(), context );
            if ( collection != null ) {
                entity.setSnippets( collection );
            }
        }

        return entity;
    }

    @Override
    public Snippet cloneSnippet(Snippet original, CycleAvoidingMappingContext context) {
        Snippet target = context.getMappedInstance( original, Snippet.class );
        if ( target != null ) {
            return target;
        }

        if ( original == null ) {
            return null;
        }

        Snippet snippet = new Snippet();

        context.storeMappedInstance( original, snippet );

        snippet.setId( original.getId() );
        snippet.setVersion( original.getVersion() );
        snippet.setDocument( original.getDocument() );
        snippet.setParent( cloneSnippet( original.getParent(), context ) );
        snippet.setSnippettemplate( original.getSnippettemplate() );
        snippet.setUuid( original.getUuid() );
        snippet.setName( original.getName() );
        snippet.setDescription( original.getDescription() );
        snippet.setContent( original.getContent() );
        snippet.setSortorder( original.getSortorder() );
        snippet.setNumbered( original.isNumbered() );
        Collection<Snippet> collection = original.getSnippets();
        if ( collection != null ) {
            snippet.setSnippets( new ArrayList<Snippet>( collection ) );
        }

        return snippet;
    }

    private String entitySnippettemplateUuid(Snippet snippet) {
        if ( snippet == null ) {
            return null;
        }
        Snippettemplate snippettemplate = snippet.getSnippettemplate();
        if ( snippettemplate == null ) {
            return null;
        }
        String uuid = snippettemplate.getUuid();
        if ( uuid == null ) {
            return null;
        }
        return uuid;
    }

    private String entityDocumentUuid(Snippet snippet) {
        if ( snippet == null ) {
            return null;
        }
        Document document = snippet.getDocument();
        if ( document == null ) {
            return null;
        }
        String uuid = document.getUuid();
        if ( uuid == null ) {
            return null;
        }
        return uuid;
    }

    protected Collection<SnippetDTO> snippetCollectionToSnippetDTOCollection(Collection<Snippet> collection, CycleAvoidingMappingContext context) {
        Collection<SnippetDTO> target = context.getMappedInstance( collection, Collection.class );
        if ( target != null ) {
            return target;
        }

        if ( collection == null ) {
            return null;
        }

        Collection<SnippetDTO> collection1 = new ArrayList<SnippetDTO>( collection.size() );
        context.storeMappedInstance( collection, collection1 );

        for ( Snippet snippet : collection ) {
            collection1.add( getDTOFromSnippet( snippet, context ) );
        }

        return collection1;
    }

    protected Collection<Snippet> snippetDTOCollectionToSnippetCollection(Collection<SnippetDTO> collection, CycleAvoidingMappingContext context) {
        Collection<Snippet> target = context.getMappedInstance( collection, Collection.class );
        if ( target != null ) {
            return target;
        }

        if ( collection == null ) {
            return null;
        }

        Collection<Snippet> collection1 = new ArrayList<Snippet>( collection.size() );
        context.storeMappedInstance( collection, collection1 );

        for ( SnippetDTO snippetDTO : collection ) {
            collection1.add( snippetDTOToSnippet( snippetDTO, context ) );
        }

        return collection1;
    }

    protected Snippet snippetDTOToSnippet(SnippetDTO snippetDTO, CycleAvoidingMappingContext context) {
        Snippet target = context.getMappedInstance( snippetDTO, Snippet.class );
        if ( target != null ) {
            return target;
        }

        if ( snippetDTO == null ) {
            return null;
        }

        Snippet snippet = new Snippet();

        context.storeMappedInstance( snippetDTO, snippet );

        snippet.setVersion( snippetDTO.getVersion() );
        snippet.setUuid( snippetDTO.getUuid() );
        snippet.setName( snippetDTO.getName() );
        snippet.setDescription( snippetDTO.getDescription() );
        snippet.setContent( snippetDTO.getContent() );
        snippet.setSortorder( snippetDTO.getSortorder() );
        snippet.setNumbered( snippetDTO.isNumbered() );
        snippet.setSnippets( snippetDTOCollectionToSnippetCollection( snippetDTO.getSnippets(), context ) );

        return snippet;
    }
}
