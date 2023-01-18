package com.vesey.documentable.session;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.vesey.documentable.entity.Datasource;
import com.vesey.documentable.entity.Matter;
import com.vesey.documentable.entity.Mergefield;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.rest.dto.IntegrationCategoryDTO;
import com.vesey.documentable.rest.dto.IntegrationMatterDTO;
import com.vesey.documentable.rest.dto.IntegrationFormFieldDTO;
import com.vesey.documentable.utils.Utils;

@Named
@Stateful
@RequestScoped
public class IntegrationFacade implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
	Logger log;
	@Inject
	DBFacade dbFacade;

	public Response processPostMatter(IntegrationMatterDTO dto, Users user) throws ConflictException {
		log.info("processPostMatter: Start");
		Matter matter = Matter.getByOrganisationAndSourceId(dbFacade, user.getOrganisation().getId(), dto.getSourceId());
		Response response = null;
		if (matter == null) {
			matter = new Matter();
			matter.setArchive(false);
			matter.setCreatedby(user);
			matter.setCreateddate(new Date());
			matter.setName(dto.getName());
			dbFacade.persist(matter);
		}
		matter.setName(dto.getName());
		matter.setDescription(dto.getDescription());
		matter.setReference(dto.getReference());
		matter.setRefreshUrl(dto.getRefreshUrl());
		matter = dbFacade.merge(matter);

		if (Utils.isNotEmpty(dto.getCategories())) {
			processCategories(dto.getCategories(), user, matter);
		}

		response = Response.status(Status.OK).entity("Success.").build();
		log.info("processPostMatter: End");
		return response;
	}

	private void processCategories(Collection<IntegrationCategoryDTO> categories, Users user, Matter matter) throws ConflictException {
		log.info("updateMatter: Start.");

		for (IntegrationCategoryDTO thisCat : categories) {
			// first ensure Datasources exist
			Datasource ds = Datasource.getByNameAndOrganisation(dbFacade, thisCat.getName(), user.getOrganisation().getId());
			if (ds == null) {
				log.info("updateMatter: Unable to find datasource with name : " + thisCat.getName() + ", creating...");
				ds = new Datasource();
				ds.setCreatedby(user);
				ds.setUuid(UUID.randomUUID().toString());
				ds.setName(thisCat.getName());
				dbFacade.persist(ds);
			}

			ds.setName(thisCat.getName());
			ds.setDescription(thisCat.getDescription());
			ds = dbFacade.merge(ds);

			if (Utils.isNotEmpty(thisCat.getFields())) {
				processFields(thisCat.getFields(), user, ds, matter);
			}

		}

		log.info("updateMatter: End.");
	}

	private void processFields(List<IntegrationFormFieldDTO> fields, Users user, Datasource datasource, Matter matter) throws ConflictException {
		log.info("processFields: Start.");

		for (IntegrationFormFieldDTO thisField : fields) {

			// first ensure MFTs exist
			Mergefieldtemplate mft = Mergefieldtemplate.getByKey(dbFacade, thisField.getKey(), user.getOrganisation().getId());
			if (mft == null) {
				log.info("processFields: Unable to find mergefieldtemplate with key : " + thisField.getKey() + " and organisation ID: " + user.getOrganisation().getId()
						+ ", creating...");

				mft = new Mergefieldtemplate();
				mft.setCreatedby(user);
				mft.setDatasource(datasource);
				mft.setKey(thisField.getKey());
				mft.setSource("Created on first use via integration on " + new SimpleDateFormat("dd/MM/yyyy hh:MM").format(new Date()));
				mft.setUuid(UUID.randomUUID().toString());

				switch (thisField.getType()) {
				case BOOLEAN:
					mft.setSamplebooleanvalue(true);
					break;
				case CURRENCY:
				case DECIMAL:
					mft.setSampledecimalvalue(new BigDecimal(200000));
					break;
				case DATE:
					mft.setSampledatevalue(new Date());
					break;
				case INTEGER:
					mft.setSampleintegervalue(10);
					break;
				case SELECT:
					mft.setSamplestringvalue("Option");
					break;
				case STRING:
					mft.setSamplestringvalue("Text");
					break;
				default:
					break;

				}

				dbFacade.persist(mft);
			}

			mft.setDescription(thisField.getDescription());
			mft.setLabel(thisField.getLabel());

			dbFacade.merge(mft);

			// Now create Mergefields for this matter
			Mergefield mf = Mergefield.getByKeyAndMatter(dbFacade, thisField.getKey(), matter.getId());
			if (mf == null) {
				log.info("processFields: Unable to find mergefield with key : " + thisField.getKey() + " and matter ID: " + matter.getId() + ", creating...");
				mf = new Mergefield(mft, matter);
				dbFacade.persist(mf);
			}

		}
		log.info("processFields: End.");
	}

}
