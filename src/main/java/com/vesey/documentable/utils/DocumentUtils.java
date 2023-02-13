package com.vesey.documentable.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import com.vesey.documentable.draftjs.EntityDTO;
import com.vesey.documentable.draftjs.EntityRangeDTO;
import com.vesey.documentable.draftjs.InlineStyleRangeDTO;
import com.vesey.documentable.draftjs.SplitStringDTO;
import com.vesey.documentable.entity.Mergefield;
import com.vesey.documentable.entity.Rule;
import com.vesey.documentable.entity.Rulegroup;
import com.vesey.documentable.entity.Snippet;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.SnippetMapperImpl;

public class DocumentUtils {

	private transient static final Logger log = Logger.getLogger(DocumentUtils.class);

	public static String getKeyFromEntity(Integer keyNum, Map<String, EntityDTO> entityMap) {
		// extract key from data map in entity

		EntityDTO thisEntity = entityMap.get(keyNum.toString());

		if (thisEntity.getType().equals("MENTION") || thisEntity.getType().equals("$mention")) {
			String foundKey = getKeyFromData(thisEntity.getData());
			return foundKey;
		}

		return null;
	}

	public static String getKeyFromData(Map<String, Object> data) {
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

	public static Collection<Snippet> getSnippetsForDocument(com.vesey.documentable.entity.Document document, SnippetMapperImpl mapper, Collection<Mergefield> mergefields) {
		// log.info("getSnippetsForDocument: Start.");

		Collection<Snippet> initialSnippets = document.getSnippets();
		Collection<Snippet> finalSnippets = new ArrayList<>();
		if (Utils.isNotEmpty(initialSnippets)) {
			log.info("getSnippetsForDocument: Iterating document snippets......................");
			for (Snippet thisSnippet : initialSnippets) {
				if (thisSnippet.getParent() == null) {
					// log.info("getSnippetsForDocument: Processing this snippet = " + thisSnippet.getName());
					Snippet filteredSnippet = filterSnippet(thisSnippet, mapper, mergefields);
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

	public static Snippet filterSnippet(Snippet snippet, SnippetMapperImpl mapper, Collection<Mergefield> mergefields) {
		// log.info("filterSnippet: Processing snippet... " + snippet.getName());
		if (!shouldInclude(mergefields, snippet)) {
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
						Snippet filteredSnippet = filterSnippet(childSnippet, mapper, mergefields);
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

	public static int getNextBreak(int start, int end, Collection<InlineStyleRangeDTO> styleRanges, Collection<EntityRangeDTO> entityRanges) {

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

	public static boolean shouldInclude(Collection<Mergefield> mergefields, Snippet thisSnippet) {
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
				if (!ruleGroupSatisfied(mergefields, thisRG)) {
					allRuleGroupsSatisfied = false;
				}
			}
			shouldInclude = allRuleGroupsSatisfied;
		} else {
			// OR rule groups together - any satisfied rule group is good
			boolean anyRuleGroupsSatisfied = false;
			for (Rulegroup thisRG : thisSnippet.getSnippettemplate().getRuleset().getRulegroups()) {
				if (ruleGroupSatisfied(mergefields, thisRG)) {
					anyRuleGroupsSatisfied = true;
				}
			}
			shouldInclude = anyRuleGroupsSatisfied;
		}
		return shouldInclude;
	}

	public static boolean ruleGroupSatisfied(Collection<Mergefield> mergefields, Rulegroup rulegroup) {
		boolean ruleGroupSatisfied = true;

		if (rulegroup.getRuleset().isOperand()) {
			// OR rules together - any satisfied rule is good
			boolean anyRuleSatisfied = false;
			for (Rule thisR : rulegroup.getRules()) {
				if (ruleSatisfied(mergefields, thisR)) {
					anyRuleSatisfied = true;
				}
			}
			ruleGroupSatisfied = anyRuleSatisfied;
		} else {
			// AND rule groups together - all satisfied rule group is good
			boolean allRulesSatisfied = true;
			for (Rule thisR : rulegroup.getRules()) {
				if (!ruleSatisfied(mergefields, thisR)) {
					allRulesSatisfied = false;
				}
			}
			ruleGroupSatisfied = allRulesSatisfied;
		}
		return ruleGroupSatisfied;
	}

	public static boolean ruleSatisfied(Collection<Mergefield> mergefields, Rule rule) {
		boolean ruleSatisfied = true;

		if (rule.getSource() != null) {
			switch (rule.getSource().getType()) {
			case BOOLEAN:
				ruleSatisfied = checkBooleanRule(mergefields, rule);
				break;
			case CURRENCY:
			case DECIMAL:
				ruleSatisfied = checkDecimalRule(mergefields, rule);
				break;
			case DATE:
				ruleSatisfied = checkDateRule(mergefields, rule);
				break;

			case INTEGER:
				ruleSatisfied = checkIntegerRule(mergefields, rule);
				break;
			case SELECT:
			case STRING:
				ruleSatisfied = checkStringRule(mergefields, rule);
				break;
			default:
				log.error("ruleSatisfied: Unknow rule type : " + rule.getSource().getType());
				break;
			}
		}
		return ruleSatisfied;
	}

	public static Mergefield findMergefield(Collection<Mergefield> mergefields, String key) {
		if (Utils.isNotEmpty(mergefields)) {
			for (Mergefield thisMF : mergefields) {
				if (thisMF.getMergefieldtemplate().getKey().equals(key)) {
					return thisMF;
				}
			}
		}
		return null;
	}

	public static boolean checkBooleanRule(Collection<Mergefield> mergefields, Rule rule) {
		Mergefield mf = findMergefield(mergefields, rule.getSource().getKey());
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

	public static boolean checkStringRule(Collection<Mergefield> mergefields, Rule rule) {
		Mergefield mf = findMergefield(mergefields, rule.getSource().getKey());
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

	public static boolean checkDecimalRule(Collection<Mergefield> mergefields, Rule rule) {
		Mergefield mf = findMergefield(mergefields, rule.getSource().getKey());
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

	public static boolean checkIntegerRule(Collection<Mergefield> mergefields, Rule rule) {
		Mergefield mf = findMergefield(mergefields, rule.getSource().getKey());
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

	public static boolean checkDateRule(Collection<Mergefield> mergefields, Rule rule) {
		Mergefield mf = findMergefield(mergefields, rule.getSource().getKey());
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

	public static List<SplitStringDTO> splitIntoSections(String text, Collection<InlineStyleRangeDTO> inlineStyleRanges, Collection<EntityRangeDTO> entityRanges,
			Collection<Mergefield> mergefields, Map<String, EntityDTO> entityMap, Boolean isHTML) {
		// split into sections
		List<SplitStringDTO> sss = new ArrayList<>();
		int currentStart = 0;
		while (currentStart < text.length()) {
			int end = DocumentUtils.getNextBreak(currentStart, text.length(), inlineStyleRanges, entityRanges);
			String subStr = text.substring(currentStart, end);
			SplitStringDTO newSplit = new SplitStringDTO(currentStart, end, subStr);

			String repText = findReplacementText(newSplit, entityRanges, mergefields, entityMap);
			newSplit.setReplacementText(repText);
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
		return sss;
	}

	public static String findReplacementText(SplitStringDTO split, Collection<EntityRangeDTO> entityRanges, Collection<Mergefield> mergefields, Map<String, EntityDTO> entityMap) {
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

	private static String findReplacement(EntityRangeDTO entityRangeDTO, Collection<Mergefield> mergefields, Map<String, EntityDTO> entityMap) {

		String key = DocumentUtils.getKeyFromEntity(entityRangeDTO.getKey(), entityMap);

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
						return "[UNKNOWN]";

					}
				}
			}
		}
		return "[INCOMPLETE]";
	}

}
