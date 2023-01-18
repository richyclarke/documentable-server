package com.vesey.documentable.session;

import java.io.Serializable;
import java.util.Collection;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import com.vesey.documentable.entity.Snippettemplate;
import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.utils.Utils;

@Named
@Stateful
@RequestScoped
public class RestFacade implements Serializable {

	@Inject
	Logger log;

	@Inject
	DBFacade dbFacade;

	private static final long serialVersionUID = 1L;

	public Response addChild(Snippettemplate sourceSnippettemplateInstance, Snippettemplate destinationSnippettemplateInstance, Integer index) throws ConflictException {

		Collection<Snippettemplate> snippettemplates = Snippettemplate.getForDocumenttemplate(destinationSnippettemplateInstance.getDocumenttemplate().getUuid(), dbFacade, true);

		if (Utils.isNotEmpty(snippettemplates)) {
			int sortorder = 1;
			for (Snippettemplate thisST : snippettemplates) {
				if (!thisST.equals(sourceSnippettemplateInstance)) {
					thisST.setSortorder(sortorder);
					thisST = dbFacade.merge(thisST);
					if (thisST.equals(destinationSnippettemplateInstance)) {
						// adding to children of this one
						sortorder++;
						sourceSnippettemplateInstance.setSortorder(sortorder);
						sourceSnippettemplateInstance.setParent(destinationSnippettemplateInstance); // could be null - that's fine
						dbFacade.merge(sourceSnippettemplateInstance);
					}
					sortorder++;
				}
			}
		}

		int sortorder = 1;
		if (Utils.isNotEmpty(destinationSnippettemplateInstance.getSnippettemplates())) {
			// other children exist
			for (Snippettemplate thisST : destinationSnippettemplateInstance.getSnippettemplates()) {
				thisST.setSortorder(sortorder);
				thisST = dbFacade.merge(thisST);
				if (thisST.equals(sourceSnippettemplateInstance)) {
					// inserting after this one
					sortorder++;
					sourceSnippettemplateInstance.setSortorder(sortorder);
					sourceSnippettemplateInstance.setParent(destinationSnippettemplateInstance.getParent());
					dbFacade.merge(sourceSnippettemplateInstance);
				}
				sortorder++;
			}
		} else {

		}

		sourceSnippettemplateInstance.setParent(destinationSnippettemplateInstance);
		dbFacade.merge(sourceSnippettemplateInstance);

		return Response.ok().build();
	}

	public Response addSibling(Snippettemplate sourceSnippettemplateInstance, Snippettemplate destinationSnippettemplateInstance, Integer index) throws ConflictException {

		Collection<Snippettemplate> snippettemplates = Snippettemplate.getForDocumenttemplate(destinationSnippettemplateInstance.getDocumenttemplate().getUuid(), dbFacade, true);

		if (Utils.isNotEmpty(snippettemplates)) {
			int sortorder = 1;
			for (Snippettemplate thisST : snippettemplates) {
				if (!thisST.equals(sourceSnippettemplateInstance)) {
					thisST.setSortorder(sortorder);
					thisST = dbFacade.merge(thisST);
					if (thisST.equals(destinationSnippettemplateInstance)) {
						// inserting after this one
						sortorder++;
						sourceSnippettemplateInstance.setSortorder(sortorder);
						sourceSnippettemplateInstance.setParent(destinationSnippettemplateInstance.getParent()); // could be null - that's fine
						dbFacade.merge(sourceSnippettemplateInstance);
					}
					sortorder++;
				}
			}
		}

		return Response.ok().build();
	}

}
