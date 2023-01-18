package com.vesey.documentable.session;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
import com.vesey.documentable.entity.Matter;
import com.vesey.documentable.entity.Mergefield;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.Rule;
import com.vesey.documentable.entity.Rulegroup;
import com.vesey.documentable.entity.Snippet;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.SnippetMapperImpl;
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

							String key = getKeyFromData(data);

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
									Boolean found = false;
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

					String key = getKeyFromData(data);

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

	public String getKeyFromData(Map<String, Object> data) {
		String key = (String) data.get("key");
		if (Utils.isEmpty(key)) {
			// key may be 'value' attribute of 'mention'
			@SuppressWarnings("unchecked")
			Map<String, Object> mention = (Map<String, Object>) data.get("mention");
			if (Utils.isNotEmpty(mention)) {
				key = (String) mention.get("value");
			}
		}
		if (key == null) {
			log.warn("getKeyFromData: key is null");
		}
		return key;
	}

	public String generateHTMLPreview(String documentUuid) {
		log.info("generateHTMLPreview: Start");
		Document doc = Document.getByUuid(dbFacade, documentUuid);
		if (doc != null) {
			Collection<Mergefield> mergefields = Mergefield.getForMatter(dbFacade, doc.getMatter().getId());

			Collection<Snippet> snippets = getSnippetsForDocument(doc);
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

		// split into sections
		List<SplitStringDTO> sss = new ArrayList<>();
		int currentStart = 0;
		while (currentStart < text.length()) {
			int end = getNextBreak(currentStart, text.length(), inlineStyleRanges, entityRanges);
			String subStr = text.substring(currentStart, end);
			SplitStringDTO newSplit = new SplitStringDTO(currentStart, end, subStr);

			newSplit.setReplacementText(findReplacementText(newSplit, entityRanges, mergefields, entityMap));
			sss.add(newSplit);
			currentStart = end;
		}

		// for each section, add styles
		for (InlineStyleRangeDTO thisRange : inlineStyleRanges) {
			for (SplitStringDTO thisSSS : sss) {
				int rangeStart = thisRange.getOffset();
				int rangeEnd = thisRange.getOffset() + thisRange.getLength();
				if (rangeStart <= thisSSS.getStart() && rangeEnd >= thisSSS.getEnd()) {
					thisSSS.getStyle().add(thisRange.getStyle());
				}
			}
		}
		// for each section, add styles
		for (EntityRangeDTO thisRange : entityRanges) {
			for (SplitStringDTO thisSSS : sss) {
				int rangeStart = thisRange.getOffset();
				int rangeEnd = thisRange.getOffset() + thisRange.getLength();
				if (rangeStart <= thisSSS.getStart() && rangeEnd >= thisSSS.getEnd()) {
					if (!thisSSS.getStyle().contains("INSERT")) {
						thisSSS.getStyle().add("INSERT");
					}
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for (SplitStringDTO thisSS : sss) {
			if (Utils.isEmpty(thisSS.getStyle())) {
				sb.append(thisSS.getText());
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
				if ((Utils.isNotEmpty(thisSS.getReplacementText()))) {
					sb.append(thisSS.getReplacementText());
				} else {
					sb.append(thisSS.getText());
				}

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

	private String findReplacementText(SplitStringDTO split, Collection<EntityRangeDTO> entityRanges, Collection<Mergefield> mergefields, Map<String, EntityDTO> entityMap) {
		if (Utils.isNotEmpty(entityRanges)) {
			for (EntityRangeDTO thisEntity : entityRanges) {
				int start = thisEntity.getOffset();
				int end = thisEntity.getOffset() + thisEntity.getLength();

				if (start == split.getStart() && end == split.getEnd()) {
					// found
					return findReplacement(thisEntity, mergefields, entityMap);
				}
			}

		}
		return null;
	}

	private String findReplacement(EntityRangeDTO entityRangeDTO, Collection<Mergefield> mergefields, Map<String, EntityDTO> entityMap) {

		String key = getKeyFromEntity(entityRangeDTO.getKey(), entityMap);

		if (Utils.isNotEmpty(mergefields) && Utils.isNotEmpty(key)) {
			for (Mergefield thisMF : mergefields) {

				if (key.equals(thisMF.getMergefieldtemplate().getKey())) {

					switch (thisMF.getMergefieldtemplate().getType()) {
					case BOOLEAN:
						if (Utils.isNotEmpty(thisMF.getBooleanvalue())) {
							return thisMF.getBooleanvalue().toString();
						}
						break;
					case CURRENCY:
						if (Utils.isNotEmpty(thisMF.getDecimalvalue())) {
							NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
							return currencyFormatter.format(thisMF.getDecimalvalue());
						}
						break;
					case DATE:
						if (Utils.isNotEmpty(thisMF.getDatevalue())) {
							SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
							return dateFormatter.format(thisMF.getDatevalue());
						}
						break;
					case DECIMAL:
						if (Utils.isNotEmpty(thisMF.getDecimalvalue())) {
							DecimalFormat decimalFormatter = new DecimalFormat("#,###.##");
							return decimalFormatter.format(thisMF.getDecimalvalue());
						}
						break;
					case INTEGER:
						if (Utils.isNotEmpty(thisMF.getIntegervalue())) {
							return thisMF.getIntegervalue().toString();
						}
						break;
					case STRING:
					case SELECT:
						if (Utils.isNotEmpty(thisMF.getStringvalue())) {
							return thisMF.getStringvalue();
						}
						break;
					default:
						return "<span style='color:red;'>[UNKNOWN]</span>";

					}
				}
			}
		}
		return "<span style='color:red;'>[INCOMPLETE]</span>";
	}

	private String getKeyFromEntity(Integer keyNum, Map<String, EntityDTO> entityMap) {
		// extract key from data map in entity

		EntityDTO thisEntity = entityMap.get(keyNum.toString());

		if (thisEntity.getType().equals("MENTION") || thisEntity.getType().equals("$mention")) {
			String foundKey = getKeyFromData(thisEntity.getData());
			return foundKey;
		}

		return null;
	}

	private int getNextBreak(int start, int end, Collection<InlineStyleRangeDTO> styleRanges, Collection<EntityRangeDTO> entityRanges) {

		int potentialEnd = end;
		for (InlineStyleRangeDTO thisRange : styleRanges) {
			if (thisRange.getOffset() > start && thisRange.getOffset() < potentialEnd) {
				potentialEnd = thisRange.getOffset();
			}
			int rangeEnd = thisRange.getOffset() + thisRange.getLength();
			if (rangeEnd > start && rangeEnd < potentialEnd) {
				potentialEnd = rangeEnd;
			}
		}
		for (EntityRangeDTO thisRange : entityRanges) {
			if (thisRange.getOffset() > start && thisRange.getOffset() < potentialEnd) {
				potentialEnd = thisRange.getOffset();
			}
			int rangeEnd = thisRange.getOffset() + thisRange.getLength();
			if (rangeEnd > start && rangeEnd < potentialEnd) {
				potentialEnd = rangeEnd;
			}
		}
		return potentialEnd;
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

	public Collection<Snippet> getSnippetsForDocument(Document document) {
		// log.info("getSnippetsForDocument: Start.");
		currentMatter = document.getMatter();
		Collection<Snippet> initialSnippets = document.getSnippets();
		Collection<Snippet> finalSnippets = new ArrayList<>();
		if (Utils.isNotEmpty(initialSnippets)) {
			log.info("getSnippetsForDocument: Iterating document snippets......................");
			for (Snippet thisSnippet : initialSnippets) {
				if (thisSnippet.getParent() == null) {
					// log.info("getSnippetsForDocument: Processing this snippet = " + thisSnippet.getName());
					Snippet filteredSnippet = filterSnippet(thisSnippet);
					if (filteredSnippet != null) {
						// log.info("getSnippetsForDocument: Adding filtered snippet");
						finalSnippets.add(filteredSnippet);
					} else {
						// log.info("getSnippetsForDocument: NOT Adding snippet");
					}
				}
			}
		}
		// log.info("getSnippetsForDocument: End.");
		return finalSnippets;
	}

	private Snippet filterSnippet(Snippet snippet) {
		// log.info("filterSnippet: Processing snippet... " + snippet.getName());
		if (!shouldInclude(snippet)) {
			// log.info("filterSnippet: NOT including this snippet = " + snippet.getName());
			return null;
		} else {
			// log.info("filterSnippet: Will be including this snippet after processing children... " + snippet.getName());
			Snippet newSnippet = mapper.cloneSnippet(snippet, new CycleAvoidingMappingContext());
			newSnippet.setSnippets(new ArrayList<>());
			{
				if (Utils.isNotEmpty(snippet.getSnippets())) {
					// log.info("filterSnippet: Snippet has children... " + snippet.getName());
					for (Snippet childSnippet : snippet.getSnippets()) {
						Snippet filteredSnippet = filterSnippet(childSnippet);
						if (filteredSnippet != null) {
							// log.info("filterSnippet: Adding child snippet has children... " + filteredSnippet.getName());
							newSnippet.getSnippets().add(filteredSnippet);
						}
					}

				}
			}
			// log.info("filterSnippet: Returning filtered snippet : " + newSnippet.getName());

			return newSnippet;
		}
	}

	private boolean shouldInclude(Snippet thisSnippet) {
		if (thisSnippet.getSnippettemplate().getRuleset() == null) {
			return true;
		}
		if (Utils.isEmpty(thisSnippet.getSnippettemplate().getRuleset().getRulegroups())) {
			return true;
		}

		boolean shouldInclude = true;

		if (thisSnippet.getSnippettemplate().getRuleset().isOperand()) {
			// AND rule groups together - all satisfied rule group is good
			boolean allRuleGroupsSatisfied = true;
			for (Rulegroup thisRG : thisSnippet.getSnippettemplate().getRuleset().getRulegroups()) {
				if (!ruleGroupSatisfied(thisRG)) {
					allRuleGroupsSatisfied = false;
				}
			}
			shouldInclude = allRuleGroupsSatisfied;
		} else {
			// OR rule groups together - any satisfied rule group is good
			boolean anyRuleGroupsSatisfied = false;
			for (Rulegroup thisRG : thisSnippet.getSnippettemplate().getRuleset().getRulegroups()) {
				if (ruleGroupSatisfied(thisRG)) {
					anyRuleGroupsSatisfied = true;
				}
			}
			shouldInclude = anyRuleGroupsSatisfied;
		}
		return shouldInclude;
	}

	private boolean ruleGroupSatisfied(Rulegroup rulegroup) {
		boolean ruleGroupSatisfied = true;

		if (rulegroup.getRuleset().isOperand()) {
			// OR rules together - any satisfied rule is good
			boolean anyRuleSatisfied = false;
			for (Rule thisR : rulegroup.getRules()) {
				if (ruleSatisfied(thisR)) {
					anyRuleSatisfied = true;
				}
			}
			ruleGroupSatisfied = anyRuleSatisfied;
		} else {
			// AND rule groups together - all satisfied rule group is good
			boolean allRulesSatisfied = true;
			for (Rule thisR : rulegroup.getRules()) {
				if (!ruleSatisfied(thisR)) {
					allRulesSatisfied = false;
				}
			}
			ruleGroupSatisfied = allRulesSatisfied;
		}
		return ruleGroupSatisfied;
	}

	private boolean ruleSatisfied(Rule rule) {
		boolean ruleSatisfied = true;

		if (rule.getSource() != null) {
			switch (rule.getSource().getType()) {
			case BOOLEAN:
				ruleSatisfied = checkBooleanRule(rule);
				break;
			case CURRENCY:
			case DECIMAL:
				ruleSatisfied = checkDecimalRule(rule);
				break;
			case DATE:
				ruleSatisfied = checkDateRule(rule);
				break;

			case INTEGER:
				ruleSatisfied = checkIntegerRule(rule);
				break;
			case SELECT:
			case STRING:
				ruleSatisfied = checkStringRule(rule);
				break;
			default:
				log.error("ruleSatisfied: Unknow rule type : " + rule.getSource().getType());
				break;
			}
		}
		return ruleSatisfied;
	}

	private Mergefield findMergefield(String key) {
		if (Utils.isNotEmpty(currentMatter.getMergefields())) {
			for (Mergefield thisMF : currentMatter.getMergefields()) {
				if (thisMF.getMergefieldtemplate().getKey().equals(key)) {
					return thisMF;
				}
			}
		}
		return null;
	}

	private boolean checkBooleanRule(Rule rule) {
		Mergefield mf = findMergefield(rule.getSource().getKey());
		if (mf == null) {
			log.error("checkBooleanRule: Unable to find Mergefield for Mergefieldtemplate key : " + rule.getSource().getKey());
			return false;
		}
		if (rule.getBooleanvalue() != null && mf.getBooleanvalue() != null) {
			switch (rule.getOperand()) {
			case EQUALS:
				if (rule.getBooleanvalue().equals(mf.getBooleanvalue()))
					return true;
				break;
			case NOT_EQUALS:
				if (!rule.getBooleanvalue().equals(mf.getBooleanvalue()))
					return true;
				break;
			default:
				// return true so snippet appears if rule has an invalid operand
				return true;
			}
		} else {
			// return true so snippet appears if rule value not set
			return false;
		}
		return false;
	}

	private boolean checkStringRule(Rule rule) {
		Mergefield mf = findMergefield(rule.getSource().getKey());
		if (mf == null) {
			log.error("checkStringRule: Unable to find Mergefield for Mergefieldtemplate key : " + rule.getSource().getKey());
			return false;
		}
		if (rule.getStringvalue() != null && mf.getStringvalue() != null) {
			switch (rule.getOperand()) {
			case EQUALS:
				if (rule.getStringvalue().equals(mf.getStringvalue()))
					return true;
				break;
			case NOT_EQUALS:
				if (!rule.getStringvalue().equals(mf.getStringvalue()))
					return true;
				break;
			case INCLUDES:
				if (mf.getStringvalue().contains(rule.getStringvalue()))
					return true;
				break;
			case STARTS_WITH:
				if (mf.getStringvalue().startsWith(rule.getStringvalue()))
					return true;
				break;
			case EXCLUDES:
				if (!mf.getStringvalue().contains(rule.getStringvalue()))
					return true;
				break;
			default:
				// return true so snippet appears if rule has an invalid operand
				return true;
			}
		} else {
			// return true so snippet appears if rule value not set
			return false;
		}
		return false;
	}

	private boolean checkDecimalRule(Rule rule) {
		Mergefield mf = findMergefield(rule.getSource().getKey());
		if (mf == null) {
			log.error("checkDecimalRule: Unable to find Mergefield for Mergefieldtemplate key : " + rule.getSource().getKey());
			return false;
		}
		if (rule.getDecimalvalue() != null && mf.getDecimalvalue() != null) {
			switch (rule.getOperand()) {
			case EQUALS:
				if (rule.getDecimalvalue().equals(mf.getDecimalvalue()))
					return true;
				break;
			case NOT_EQUALS:
				if (!rule.getDecimalvalue().equals(mf.getDecimalvalue()))
					return true;
				break;
			case GREATER_THAN:
				if (mf.getDecimalvalue().compareTo(rule.getDecimalvalue()) > 0)
					return true;
				break;
			case LESS_THAN:
				if (mf.getDecimalvalue().compareTo(rule.getDecimalvalue()) < 0)
					return true;
				break;

			default:
				// return true so snippet appears if rule has an invalid operand
				return true;
			}
		} else {
			// return true so snippet appears if rule value not set
			return false;
		}
		return false;
	}

	private boolean checkIntegerRule(Rule rule) {
		Mergefield mf = findMergefield(rule.getSource().getKey());
		if (mf == null) {
			log.error("checkIntegerRule: Unable to find Mergefield for Mergefieldtemplate key : " + rule.getSource().getKey());
			return false;
		}
		if (rule.getIntegervalue() != null && mf.getIntegervalue() != null) {
			switch (rule.getOperand()) {
			case EQUALS:
				if (rule.getIntegervalue().equals(mf.getIntegervalue()))
					return true;
				break;
			case NOT_EQUALS:
				if (!rule.getIntegervalue().equals(mf.getIntegervalue()))
					return true;
				break;
			case GREATER_THAN:
				if (mf.getIntegervalue().compareTo(rule.getIntegervalue()) > 0)
					return true;
				break;
			case LESS_THAN:
				if (mf.getIntegervalue().compareTo(rule.getIntegervalue()) < 0)
					return true;
				break;

			default:
				// return true so snippet appears if rule has an invalid operand
				return true;
			}
		} else {
			// return true so snippet appears if rule value not set
			return false;
		}
		return false;
	}

	private boolean checkDateRule(Rule rule) {
		Mergefield mf = findMergefield(rule.getSource().getKey());
		if (mf == null) {
			log.error("checkDecimalRule: Unable to find Mergefield for Mergefieldtemplate key : " + rule.getSource().getKey());
			return false;
		}
		if (rule.getDatevalue() != null && mf.getDatevalue() != null) {
			switch (rule.getOperand()) {
			case EQUALS:
				if (rule.getDatevalue().equals(mf.getDatevalue()))
					return true;
				break;
			case NOT_EQUALS:
				if (!rule.getDatevalue().equals(mf.getDatevalue()))
					return true;
				break;
			case GREATER_THAN:
				if (mf.getDatevalue().compareTo(rule.getDatevalue()) > 0)
					return true;
				break;
			case LESS_THAN:
				if (mf.getDatevalue().compareTo(rule.getDatevalue()) < 0)
					return true;
				break;

			default:
				// return true so snippet appears if rule has an invalid operand
				return true;
			}
		} else {
			// return true so snippet appears if rule value not set
			return false;
		}
		return false;
	}

	public Matter getCurrentMatter() {
		return currentMatter;
	}

	public void setCurrentMatter(Matter currentMatter) {
		this.currentMatter = currentMatter;
	}

}
