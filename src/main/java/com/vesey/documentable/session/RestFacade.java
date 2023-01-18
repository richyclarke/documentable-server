package com.vesey.documentable.session;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import com.vesey.documentable.entity.Snippettemplate;
import com.vesey.documentable.errorhandling.ConflictException;

@Named
@Stateful
@RequestScoped
public class RestFacade implements Serializable {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	private static final long serialVersionUID = 1L;

	public Response moveSnippettemplate(Snippettemplate sourceSnippettemplateInstance, Snippettemplate destinationSnippettemplateInstance, Integer index) throws ConflictException {

		if ((sourceSnippettemplateInstance.getParent() == null && sourceSnippettemplateInstance.getParent() == null) ||
				(sourceSnippettemplateInstance.getParent() != null && destinationSnippettemplateInstance.getParent() != null && sourceSnippettemplateInstance.getParent().equals(
						destinationSnippettemplateInstance.getParent()))) {
			Integer sourceSortOrder = sourceSnippettemplateInstance.getSortorder();
			Integer destinationSortOrder = destinationSnippettemplateInstance.getSortorder();
			sourceSnippettemplateInstance.setSortorder(destinationSortOrder);
			destinationSnippettemplateInstance.setSortorder(sourceSortOrder);

			dbFacade.merge(sourceSnippettemplateInstance);
			dbFacade.merge(destinationSnippettemplateInstance);
		} else {

			sourceSnippettemplateInstance.setParent(destinationSnippettemplateInstance);
		}
		return Response.ok().build();
	}

}
