package com.vesey.documentable.session;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vesey.documentable.draftjs.BlockDTO;
import com.vesey.documentable.draftjs.ContentDTO;
import com.vesey.documentable.draftjs.EntityDTO;
import com.vesey.documentable.draftjs.EntityRangeDTO;
import com.vesey.documentable.draftjs.InlineStyleRangeDTO;
import com.vesey.documentable.draftjs.SplitStringDTO;
import com.vesey.documentable.entity.Document;
import com.vesey.documentable.entity.Documenttemplate;
import com.vesey.documentable.entity.Matter;
import com.vesey.documentable.entity.Mergefield;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.Rule;
import com.vesey.documentable.entity.Rulegroup;
import com.vesey.documentable.entity.Snippet;
import com.vesey.documentable.entity.Snippettemplate;
import com.vesey.documentable.entity.map.SnippetMapperImpl;
import com.vesey.documentable.utils.DocumentUtils;
import com.vesey.documentable.utils.Utils;

@Named
@Stateful
@RequestScoped
public class DocumentBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String PREFACE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" + "<HTML>\n"
			+ "<HEAD><TITLE>Documentable Export</TITLE>"
			+ "<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/purecss@3.0.0/build/base-min.css'>"
			+ "<style> "

			+ "</style></HEAD>\n"

			+ "<BODY>\n";
	private final String EPILOG = "</BODY></HTML>";
	@Inject
	Logger log;
	@Inject
	DBFacade dbFacade;
	@Inject
	SnippetMapperImpl mapper;

	private Matter currentMatter;

	public Collection<Mergefield> getForDocument(DBFacade dbFacade, Document document) {
		log.info("getForDocument: Start");
		Collection<Mergefield> allMergefields = Mergefield.getForMatter(dbFacade, document.getMatter().getId());

		Collection<Snippet> snippets = Snippet.getForDocument(dbFacade, document.getId());

		List<Mergefield> filteredMergefields = new ArrayList<>();
		if (Utils.isNotEmpty(snippets)) {
			for (Snippet thisSnippet : snippets) {
				ObjectMapper objectMapper = new ObjectMapper();

				try {
					ContentDTO dto = objectMapper.readValue(thisSnippet.getContent(), ContentDTO.class);
					if (Utils.isNotEmpty(dto.getEntityMap())) {
						for (Entry<String, EntityDTO> entry : dto.getEntityMap().entrySet()) {
							Map<String, Object> data = entry.getValue().getData();

							String key = DocumentUtils.getKeyFromData(data);

							if (key != null) {
								if (Utils.isNotEmpty(allMergefields)) {
									for (Mergefield thisMF : allMergefields) {
										if (key.equals(thisMF.getMergefieldtemplate().getKey())) {
											if (!filteredMergefields.contains(thisMF)) {
												filteredMergefields.add(thisMF);
											}
										}
									}
								}
							}
						}
					}
				} catch (JsonProcessingException e) {
					log.error("getForDocument: JsonProcessingException : ", e);
				}

				// also check if merge fields are referred to in rules

				if (thisSnippet.getSnippettemplate().getRuleset() != null) {
					if (Utils.isNotEmpty(thisSnippet.getSnippettemplate().getRuleset().getRulegroups())) {
						for (Rulegroup thisRG : thisSnippet.getSnippettemplate().getRuleset().getRulegroups()) {
							if (Utils.isNotEmpty(thisRG.getRules())) {
								for (Rule thisR : thisRG.getRules()) {
									String key = thisR.getSource().getKey();
									if (Utils.isNotEmpty(allMergefields)) {
										for (Mergefield thisMF : allMergefields) {
											if (key.equals(thisMF.getMergefieldtemplate().getKey())) {
												if (!filteredMergefields.contains(thisMF)) {
													filteredMergefields.add(thisMF);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		log.info("getForDocument: End");

		return filteredMergefields;

	}

	public void checkDocument(Document document) {
		log.info("checkDocument: Start");

		// Check all referenced merge fields from template exist for this document
		Collection<Mergefield> mergefields = Mergefield.getForMatter(dbFacade, document.getMatter().getId());

		if (Utils.isNotEmpty(document.getSnippets())) {
			for (Snippet thisSnippet : document.getSnippets()) {
				checkSnippetMergeFields(thisSnippet, null, mergefields);
			}
		}

		log.info("checkDocument: End");
	}

	private void checkSnippetMergeFields(Snippet snippet, Snippet parent, Collection<Mergefield> mergefields) {

		if (snippet != null) {
			checkSnippet(snippet, parent, mergefields);

			if (Utils.isNotEmpty(snippet.getSnippets())) {
				for (Snippet thisSnippet : snippet.getSnippets()) {
					checkSnippetMergeFields(thisSnippet, snippet, mergefields);
				}
			}
		}
	}

	private void checkSnippet(Snippet thisSnippet, Snippet snippet, Collection<Mergefield> mergefields) {
		// iterate through merge fields and create any that don't exist
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			ContentDTO dto = objectMapper.readValue(thisSnippet.getContent(), ContentDTO.class);
			if (Utils.isNotEmpty(dto.getEntityMap())) {
				for (Entry<String, EntityDTO> entry : dto.getEntityMap().entrySet()) {
					Boolean found = false;
					Map<String, Object> data = entry.getValue().getData();

					String key = DocumentUtils.getKeyFromData(data);

					if (Utils.isNotEmpty(mergefields)) {
						for (Mergefield thisMF : mergefields) {
							if (key.equals(thisMF.getMergefieldtemplate().getKey())) {
								found = true;
							}
						}
					}
					if (!found) {
						log.info("checkSnippet: creating new merge field");
						Mergefieldtemplate foundMFT = Mergefieldtemplate.getByKey(dbFacade, key, thisSnippet.getDocument().getCreatedby().getOrganisation().getId());
						Mergefield newMF = new Mergefield(foundMFT, thisSnippet.getDocument().getMatter());
						dbFacade.persist(newMF);
						mergefields.add(newMF);
					}
				}
			}
		} catch (JsonProcessingException e) {

			log.error("checkSnippet: JsonProcessingException : ", e);
		}

		// also check if merge fields are referred to in rules
		if (thisSnippet.getSnippettemplate().getRuleset() != null) {
			if (Utils.isNotEmpty(thisSnippet.getSnippettemplate().getRuleset().getRulegroups())) {
				for (Rulegroup thisRG : thisSnippet.getSnippettemplate().getRuleset().getRulegroups()) {
					if (Utils.isNotEmpty(thisRG.getRules())) {
						for (Rule thisR : thisRG.getRules()) {
							String key = thisR.getSource().getKey();
							Boolean found = false;
							if (Utils.isNotEmpty(mergefields)) {
								for (Mergefield thisMF : mergefields) {
									if (key.equals(thisMF.getMergefieldtemplate().getKey())) {
										found = true;
									}
								}
							}
							if (!found) {
								log.info("checkSnippet: creating new merge field");
								Mergefieldtemplate foundMFT = Mergefieldtemplate.getByKey(dbFacade, key, thisSnippet.getDocument().getCreatedby().getOrganisation().getId());
								Mergefield newMF = new Mergefield(foundMFT, thisSnippet.getDocument().getMatter());
								dbFacade.persist(newMF);
								mergefields.add(newMF);
							}
						}
					}

				}
			}
		}
	}

	public String generateHTMLPreview(String documentUuid) {
		log.info("generateHTMLPreview: Start");
		Document doc = Document.getByUuid(dbFacade, documentUuid);
		if (doc != null) {
			Collection<Mergefield> mergefields = Mergefield.getForMatter(dbFacade, doc.getMatter().getId());

			Collection<Snippet> snippets = DocumentUtils.getSnippetsForDocument(doc, mapper, mergefields);
			StringBuilder sb = new StringBuilder();
			sb.append(PREFACE);
			if (doc.isNumbered()) {
				sb.append("\n<ol>");

			}
			if (Utils.isNotEmpty(snippets)) {
				int index = 0;
				for (Snippet thisSnippet : snippets) {
					if (thisSnippet.getParent() == null) {
						if (thisSnippet.isNumbered()) {
							index++;
						}
						sb.append(processSnippet(thisSnippet, null, mergefields, index + ".", 1, index));
					}
				}
			}
			if (doc.isNumbered()) {
				sb.append("\n</ol>");
			}
			sb.append(EPILOG);
			log.info("generateHTMLPreview:End");
			return sb.toString();
		}
		log.warn(" Failed - Document is null for documentUuid: " + documentUuid);
		return null;
	}

	public void generateDocumentPDF(String documentUuid, OutputStream out) {
		log.info("generateDocumentPDF: Start");
		Document doc = Document.getByUuid(dbFacade, documentUuid);

		if (doc != null) {
			Collection<Mergefield> mergefields = Mergefield.getForMatter(dbFacade, doc.getMatter().getId());
			Collection<Snippet> snippets = DocumentUtils.getSnippetsForDocument(doc, mapper, mergefields);
			PDFFacade.createPDF(doc, mergefields, snippets, out);
		} else {
			log.error("generateDocumentPDF: Failed - Document is null for documentUuid: " + documentUuid);
		}
		log.info("generateDocumentPDF: End");
	}

	public void generateDocumenttemplatePDF(String documenttemplateUuid, OutputStream out) {
		log.info("generateDocumentPDF: Start");
		Documenttemplate dt = Documenttemplate.getByUuid(dbFacade, documenttemplateUuid);

		if (dt != null) {
			Collection<Mergefieldtemplate> mergefieldtemplates = Mergefieldtemplate.getForUser(dbFacade, dt.getCreatedby());
			Collection<Snippettemplate> snippettemplates = Snippettemplate.getForDocumenttemplate(documenttemplateUuid, dbFacade, false);
			PDFFacade.createPDF(dt, mergefieldtemplates, snippettemplates, out);
		} else {
			log.error("generateDocumenttemplatePDF: Failed - Documenttemplate is null for documenttemplateUuid: " + documenttemplateUuid);
		}
		log.info("generateDocumenttemplatePDF: End");
	}

	private String processSnippet(Snippet snippet, Snippet parent, Collection<Mergefield> mergefields, String prefix, Integer level, Integer index) {
		StringBuilder html = new StringBuilder();
		// log.info("processSnippet: snippet ------------ " + snippet.getName());
		// log.info("processSnippet: prefix : " + prefix);
		// log.info("processSnippet: level : " + level);
		// log.info("processSnippet: index : " + index);
		if (snippet != null) {
			html.append(generateHTML(snippet, mergefields, prefix, level, index));

			if (snippet.isNumbered()) {
				html.append("\n<ol>");
			}
			if (Utils.isNotEmpty(snippet.getSnippets())) {
				for (Snippet thisSnippet : snippet.getSnippets()) {
					int childIndex = 1;
					prefix = prefix + childIndex + ".";
					html.append(processSnippet(thisSnippet, snippet, mergefields, prefix, level + 1, childIndex));
					if (thisSnippet.isNumbered()) {
						childIndex++;
					}
				}
			}
			if (snippet.isNumbered()) {
				html.append("\n</ol>");
			}
		}
		return html.toString();
	}

	private String generateHTML(Snippet snippet, Collection<Mergefield> mergefields, String prefix, Integer currentLevel, Integer index) {
		// log.info("generateHTML: Start");
		StringBuilder sb = new StringBuilder();
		String content = snippet.getContent();
		// log.info("generateHTML: content = " + content);
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			ContentDTO dto = objectMapper.readValue(content, ContentDTO.class);

			if (Utils.isNotEmpty(dto.getBlocks())) {
				for (BlockDTO thisBlock : dto.getBlocks()) {

					String blockText = createElement(thisBlock.getText(), thisBlock.getInlineStyleRanges(), thisBlock.getEntityRanges(), mergefields, dto.getEntityMap());

					String style = createElementStyle(thisBlock);

					String elementType = getElementType(thisBlock.getType());
					sb.append("\n<" + elementType + style + ">");
					if (elementType.startsWith("h") && snippet.isNumbered()) {
						sb.append(prefix + " ");
					}

					sb.append(blockText);

					sb.append("\n</" + elementType + ">");
				}
			}

			// log.info("generateHTML: Success!");
		} catch (JsonProcessingException e) {

			log.error("generateHTML: JsonProcessingException : ", e);
		}
		// log.info("generateHTML: End");
		return sb.toString();
	}

	private String createElement(String text, Collection<InlineStyleRangeDTO> inlineStyleRanges, Collection<EntityRangeDTO> entityRanges, Collection<Mergefield> mergefields,
			Map<String, EntityDTO> entityMap) {

		List<SplitStringDTO> sss = DocumentUtils.splitIntoSections(text, inlineStyleRanges, entityRanges, mergefields, entityMap, true);

		StringBuilder sb = new StringBuilder();
		for (SplitStringDTO thisSS : sss) {
			if (Utils.isEmpty(thisSS.getStyle())) {
				sb.append(swapText(thisSS));
			} else {
				for (String thisSSS : thisSS.getStyle()) {
					switch (thisSSS) {
					case "BOLD":
						sb.append("<strong>");
						break;
					case "ITALIC":
						sb.append("<em>");
						break;
					case "INSERT":
						sb.append("<span class='insert'>");
						break;
					case "SUPERSCRIPT":
						sb.append("<sup>");
						break;
					case "SUBSCRIPT":
						sb.append("<sub>");
						break;
					}
				}

				sb.append(swapText(thisSS));

				for (String thisSSS : thisSS.getStyle()) {

					switch (thisSSS) {
					case "BOLD":
						sb.append("</strong>");
						break;
					case "ITALIC":
						sb.append("</em>");
						break;
					case "INSERT":
						sb.append("</span>");
						break;
					case "SUPERSCRIPT":
						sb.append("<span>");
						break;
					case "SUBSCRIPT":
						sb.append("<span>");
						break;
					}
				}
			}
		}

		return sb.toString();

	}

	public String swapText(SplitStringDTO thisSS) {
		if (Utils.isNotEmpty(thisSS.getReplacementText())) {
			if (thisSS.getReplacementText().equals("[UNKNOWN]") || thisSS.getReplacementText().equals("[INCOMPLETE]")) {
				return "<span style='color:red;'>" + thisSS.getReplacementText() + "</span>";
			} else {
				return thisSS.getReplacementText();
			}
		} else {
			return thisSS.getText();
		}
	}

	private String createElementStyle(BlockDTO thisBlock) {

		if (Utils.isNotEmpty(thisBlock.getInlineStyleRanges())) {

			// need to check data and inline styles
			StringBuilder dataSb = new StringBuilder();
			if (Utils.isNotEmpty(thisBlock.getData())) {
				for (Map.Entry<String, Object> entry : thisBlock.getData().entrySet()) {
					String key = entry.getKey();
					Object val = entry.getValue();
					dataSb.append(key);
					dataSb.append(":");
					dataSb.append(val);
					dataSb.append(";");
				}
			}

			StringBuilder styleSb = new StringBuilder();
			for (InlineStyleRangeDTO thisStyle : thisBlock.getInlineStyleRanges()) {

				if (thisStyle.getOffset().equals(0) && thisStyle.getLength().equals(thisBlock.getText().length())) {
					// style applies to whole block (as opposed to inline style)
					switch (thisStyle.getStyle()) {
					case "right":
						styleSb.append("text-align:right;");
						break;
					case "left":
						styleSb.append("text-align:left;");
						break;
					case "center":
						styleSb.append("text-align:center;");
						break;
					}
				}
			}

			StringBuilder sb = new StringBuilder();
			if (dataSb.length() > 0 || styleSb.length() > 0) {
				sb.append(" style= '");
				sb.append(dataSb);
				sb.append(styleSb);
				sb.append("'");
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	private String getElementType(String type) {
		if (Utils.isNotEmpty(type)) {
			switch (type) {
			case "unstyled":
				return "p";
			case "header-one":
				return "h1";
			case "header-two":
				return "h2";
			case "header-three":
				return "h3";
			case "header-four":
				return "h4";
			case "header-five":
				return "h5";
			case "header-six":
				return "h6";
			case "unordered-list-item":
				return "li";
			case "ordered-list-item":
				return "li";
			default:
				return "p";
			}
		}
		return "span";
	}

	public Matter getCurrentMatter() {
		return currentMatter;
	}

	public void setCurrentMatter(Matter currentMatter) {
		this.currentMatter = currentMatter;
	}

}
