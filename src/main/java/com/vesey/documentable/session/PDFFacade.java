package com.vesey.documentable.session;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfWriter;
import com.vesey.documentable.draftjs.BlockDTO;
import com.vesey.documentable.draftjs.ContentDTO;
import com.vesey.documentable.draftjs.EntityDTO;
import com.vesey.documentable.draftjs.EntityRangeDTO;
import com.vesey.documentable.draftjs.InlineStyleRangeDTO;
import com.vesey.documentable.draftjs.SplitStringDTO;
import com.vesey.documentable.entity.Documenttemplate;
import com.vesey.documentable.entity.Mergefield;
import com.vesey.documentable.entity.Mergefieldtemplate;
import com.vesey.documentable.entity.Snippet;
import com.vesey.documentable.entity.Snippettemplate;
import com.vesey.documentable.utils.DocumentUtils;
import com.vesey.documentable.utils.Utils;

public class PDFFacade implements Serializable {

	private static final long serialVersionUID = 1L;

	public static float FONT_SIZE_H1 = 16.0f;
	public static float FONT_SIZE_H2 = 14.0f;
	public static float FONT_SIZE_H3 = 13.0f;
	public static float FONT_SIZE_H4 = 12.0f;
	public static float FONT_SIZE_H5 = 11.0f;
	public static float FONT_SIZE_H6 = 10.0f;
	public static float FONT_SIZE_P = 10f;

	public static float MARGIN_TOP_H1 = 20f;
	public static float MARGIN_TOP_H2 = 20f;
	public static float MARGIN_TOP_H3 = 10f;
	public static float MARGIN_TOP_H4 = 8f;
	public static float MARGIN_TOP_H5 = 6f;
	public static float MARGIN_TOP_H6 = 4f;
	public static float MARGIN_TOP_P = 0f;
	public static float MARGIN_TOP_LI = 0f;
	public static float MARGIN_TOP_LIST = 10f;

	public static float MARGIN_BOTTOM_H1 = 10f;
	public static float MARGIN_BOTTOM_H2 = 8f;
	public static float MARGIN_BOTTOM_H3 = 6f;
	public static float MARGIN_BOTTOM_H4 = 4f;
	public static float MARGIN_BOTTOM_H5 = 4f;
	public static float MARGIN_BOTTOM_H6 = 4f;
	public static float MARGIN_BOTTOM_P = 2f;
	public static float MARGIN_BOTTOM_LI = 1f;
	public static float MARGIN_BOTTOM_LIST = 10f;

	public static float INDENTATION = 20f;

	private transient static final Logger log = Logger.getLogger(PDFFacade.class);

	public static void createPDF(Documenttemplate dt, Collection<Mergefieldtemplate> mergefieldtemplates, Collection<Snippettemplate> snippettemplates, OutputStream out) {
		// TODO Auto-generated method stub

	}

	public static void createPDF(com.vesey.documentable.entity.Document doc, Collection<Mergefield> mergefields, Collection<Snippet> snippets, OutputStream out) {
		Document document = new Document();

		try {

			PdfWriter pdf = PdfWriter.getInstance(document, out);
			pdf.setTagged();

			// open
			document.open();
			Integer index = 0;

			if (Utils.isNotEmpty(snippets)) {

				for (Snippet thisSnippet : snippets) {
					if (thisSnippet.getParent() == null) {
						if (thisSnippet.isNumbered()) {
							index++;
						}
						processSnippet(thisSnippet, mergefields, document, index + ".", 0, index);
					}
				}
			}

			if (Utils.isNotEmpty(snippets)) {

				for (Snippet thisSnippet : snippets) {
					if (thisSnippet.getParent() == null) {
						if (thisSnippet.isNumbered()) {
							index++;
						}
						processSnippet(thisSnippet, mergefields, document, index + ".", 0, index);
					}
				}
			}
			// close
			document.close();

			System.out.println("Done");

		} catch (DocumentException e) {
			log.error("createPDF: Exception : ", e);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				log.error("createPDF: Exception Closing output stream: ", e);
			}
		}
	}

	private static void processSnippet(Snippet snippet, Collection<Mergefield> mergefields, Document document, String prefix, Integer level, Integer index) {

		if (snippet != null) {
			try {

				addContent(snippet, mergefields, document, prefix, level, index);

				if (Utils.isNotEmpty(snippet.getSnippets())) {
					int childIndex = 1;
					for (Snippet thisSnippet : snippet.getSnippets()) {
						prefix = prefix + childIndex + ".";
						processSnippet(thisSnippet, mergefields, document, prefix, level + 1, childIndex);
						if (thisSnippet.isNumbered()) {
							childIndex++;
						}
					}
				}

			} catch (Exception e) {
				log.error("processSnippet: Exception : ", e);
			}
		}
	}

	private static void addContent(Snippet snippet, Collection<Mergefield> mergefields, Document document, String prefix, Integer level, Integer index) {
		String content = snippet.getContent();
		ObjectMapper objectMapper = new ObjectMapper();

		int listItemIndex = 1;

		try {
			ContentDTO dto = objectMapper.readValue(content, ContentDTO.class);

			if (Utils.isNotEmpty(dto.getBlocks())) {

				for (BlockDTO thisBlock : dto.getBlocks()) {

					ListModeEnum listMode = ListModeEnum.fromValue(thisBlock.getType());
					boolean lastListItem = isLastListItem(thisBlock, dto.getBlocks());
					Element el = createElement(thisBlock, mergefields, dto.getEntityMap(), prefix, level, index, listMode, listItemIndex, lastListItem);

					document.add(el);

					if (lastListItem) {
						listItemIndex = 1;
					} else if (listMode != null) {
						listItemIndex++;
					}
				}
			}
		} catch (JsonProcessingException e) {
			log.error("generateHTML: JsonProcessingException : ", e);
		} catch (DocumentException e) {
			log.error("generateHTML: DocumentException : ", e);
		}
	}

	private static boolean isLastListItem(BlockDTO block, Collection<BlockDTO> blocks) {

		ListModeEnum blockListMode = ListModeEnum.fromValue(block.getType());
		if (blockListMode == null) {
			// not interested
			return false;
		}

		boolean found = false;
		for (BlockDTO thisBlock : blocks) {
			if (thisBlock.getKey().equals(block.getKey())) {
				// found block in list - next iteration is teh one to look at
				found = true;
			} else if (found) {
				// looking at next item
				ListModeEnum thisListMode = ListModeEnum.fromValue(thisBlock.getType());
				if (thisListMode == null) {
					// next item is not a list item so return true;
					return true;
				} else if (thisListMode.equals(blockListMode)) {
					// another li of the same type - return false;
					return false;
				} else {
					// another li of different type - return true;
					return true;
				}
			}
		}
		// no more items - return true
		return true;
	}

	private static Element createElement(BlockDTO thisBlock, Collection<Mergefield> mergefields, Map<String, EntityDTO> entityMap, String prefix, Integer level, Integer index,
			ListModeEnum listMode, Integer listItemIndex, boolean lastListItem) {

		String text = thisBlock.getText();
		Collection<InlineStyleRangeDTO> inlineStyleRanges = thisBlock.getInlineStyleRanges();
		Collection<EntityRangeDTO> entityRanges = thisBlock.getEntityRanges();

		List<SplitStringDTO> sss = DocumentUtils.splitIntoSections(text, inlineStyleRanges, entityRanges, mergefields, entityMap, false);

		Paragraph p = null;

		if (Utils.isNotEmpty(thisBlock.getType())) {
			PdfName role = calculateRole(thisBlock.getType());
			p = createParagraph(role, prefix, level, listMode, listItemIndex, lastListItem);
			p = addElementStyle(thisBlock, p);
		}

		for (SplitStringDTO thisSS : sss) {

			Chunk chunk = swapText(thisSS);

			if (Utils.isEmpty(thisSS.getStyle())) {
				p.add(chunk);
			} else {
				int fontStyle = Font.NORMAL;
				for (String thisSSS : thisSS.getStyle()) {
					switch (thisSSS) {
					case "BOLD":
						fontStyle = fontStyle | Font.BOLD;
						break;
					case "ITALIC":
						fontStyle = fontStyle | Font.ITALIC;
						break;
					case "UNDERLINE":
						fontStyle = fontStyle | Font.UNDERLINE;
						break;
					case "INSERT":
						// TODO
						break;
					case "SUPERSCRIPT":
						chunk.setTextRise(5f);
						break;
					case "SUBSCRIPT":
						chunk.setTextRise(-5f);
						break;
					case "right":
					case "left":
					case "center":
						// these are paragraph styles and are handled elsewhere
						break;
					default:
						log.error("createElement: Unknown style : " + thisSS.getStyle());
						break;
					}
				}

				chunk.getFont().setStyle(fontStyle);
				p.add(chunk);
			}
		}
		return p;
	}

	private static Paragraph createParagraph(PdfName role, String prefix, Integer level, ListModeEnum listMode, Integer listItemIndex, boolean lastListItem) {

		Font font;
		float marginTop;
		float marginBottom;
		boolean addPrefix = false;

		switch (role.toString()) {
		case "/H1":
			font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, FONT_SIZE_H1);
			marginTop = MARGIN_TOP_H1;
			marginBottom = MARGIN_BOTTOM_H1;
			addPrefix = true;
			break;
		case "/H2":
			font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, FONT_SIZE_H2);
			marginTop = MARGIN_TOP_H2;
			marginBottom = MARGIN_BOTTOM_H2;
			addPrefix = true;
			break;
		case "/H3":
			font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, FONT_SIZE_H3);
			marginTop = MARGIN_TOP_H3;
			marginBottom = MARGIN_BOTTOM_H3;
			addPrefix = true;
			break;
		case "/H4":
			font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, FONT_SIZE_H4);
			marginTop = MARGIN_TOP_H4;
			marginBottom = MARGIN_BOTTOM_H4;
			addPrefix = true;
			break;
		case "/H5":
			font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, FONT_SIZE_H5);
			marginTop = MARGIN_TOP_H5;
			marginBottom = MARGIN_BOTTOM_H5;
			addPrefix = true;
			break;
		case "/H6":
			font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, FONT_SIZE_H6);
			marginTop = MARGIN_TOP_H6;
			marginBottom = MARGIN_BOTTOM_H6;
			addPrefix = true;
			break;
		case "/P":
			font = FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE_P);
			marginTop = MARGIN_TOP_P;
			marginBottom = MARGIN_BOTTOM_P;
			break;
		case "/LI":
			font = FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE_P);
			marginTop = MARGIN_TOP_LI;
			marginBottom = MARGIN_BOTTOM_LI;
			break;

		default:
			font = FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE_P);
			marginTop = MARGIN_TOP_P;
			marginBottom = MARGIN_BOTTOM_P;
			break;
		}

		Paragraph p = new Paragraph("", font);

		if (addPrefix) {
			p.add(prefix + "  ");
		}

		if (role.equals(PdfName.LI) && listMode != null) {

			if (listItemIndex == 1) {
				marginTop = MARGIN_TOP_LIST;
			}
			if (lastListItem) {
				marginBottom = MARGIN_BOTTOM_LIST;
			}
			switch (listMode) {
			case UNORDERED:

				Font zapfdingbats = new Font(FontFamily.ZAPFDINGBATS, 5.5f);
				Chunk bullet = new Chunk(String.valueOf((char) 108), zapfdingbats);
				bullet.setTextRise(2f);

				p.add(bullet);
				p.add("  ");
				break;
			case ORDERED:
				p.add(listItemIndex.toString() + ".  ");
				break;
			default:
				break;

			}

		}

		p.setIndentationLeft(level * INDENTATION);

		p.setSpacingAfter(marginBottom);
		p.setSpacingBefore(marginTop);

		return p;
	}

	private static PdfName calculateRole(String type) {
		switch (type) {
		case "unstyled":
			return PdfName.P;
		case "header-one":
			return PdfName.H1;
		case "header-two":
			return PdfName.H2;
		case "header-three":
			return PdfName.H3;
		case "header-four":
			return PdfName.H4;
		case "header-five":
			return PdfName.H5;
		case "header-six":
			return PdfName.H6;
		case "unordered-list-item":
			return PdfName.LI;
		case "ordered-list-item":
			return PdfName.LI;
		default:
			return PdfName.P;
		}
	}

	public static Chunk swapText(SplitStringDTO thisSS) {
		if (Utils.isNotEmpty(thisSS.getReplacementText())) {
			if (thisSS.getReplacementText().equals("[UNKNOWN]") || thisSS.getReplacementText().equals("[INCOMPLETE]")) {
				Chunk c = new Chunk(thisSS.getReplacementText());
				Font f = new Font();
				f.setColor(BaseColor.RED);
				c.setFont(f);
				return c;
			} else {
				return new Chunk(thisSS.getReplacementText());
			}
		} else {
			return new Chunk(thisSS.getText());
		}
	}

	private static Paragraph addElementStyle(BlockDTO thisBlock, Paragraph paragraph) {

		// need to check data and inline styles
		if (Utils.isNotEmpty(thisBlock.getData())) {
			for (Map.Entry<String, Object> entry : thisBlock.getData().entrySet()) {

				String key = entry.getKey();
				Object val = entry.getValue();

				switch (key) {
				case "text-align":
					paragraph.setAlignment(Element.ALIGN_LEFT);
					break;
				case "text-center":
					paragraph.setAlignment(Element.ALIGN_CENTER);
					break;
				case "text-right":
					paragraph.setAlignment(Element.ALIGN_RIGHT);
					break;
				default:
					log.error("createElementStyle: Unknown data style : key = " + key + ", value = " + val);
					break;
				}
			}
		}
		if (Utils.isNotEmpty(thisBlock.getInlineStyleRanges())) {
			for (InlineStyleRangeDTO thisStyle : thisBlock.getInlineStyleRanges()) {

				if (thisStyle.getOffset().equals(0) && thisStyle.getLength().equals(thisBlock.getText().length())) {
					// style applies to whole block (as opposed to inline style)
					switch (thisStyle.getStyle()) {
					case "right":
						paragraph.setAlignment(Element.ALIGN_RIGHT);
						break;
					case "left":
						paragraph.setAlignment(Element.ALIGN_LEFT);
						break;
					case "center":
						paragraph.setAlignment(Element.ALIGN_CENTER);
						break;
					default:
						log.error("createElementStyle: Unknown inline style : " + thisStyle.getStyle());
						break;
					}
				}
			}
		}

		return paragraph;
	}

	public enum ListModeEnum {
		ORDERED("ORDERED"), UNORDERED("UNORDERED");

		private String value;

		ListModeEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static ListModeEnum fromValue(String type) {
			switch (type) {
			case "unordered-list-item":
				return UNORDERED;
			case "ordered-list-item":
				return ORDERED;
			default:
				return null;
			}
		}
	}

}
