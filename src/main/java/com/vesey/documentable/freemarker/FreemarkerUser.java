package com.vesey.documentable.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;


import org.jboss.logging.Logger;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerUser {
	protected static String firstTemplate = "firstTemplate";

	private transient static final Logger log = Logger.getLogger(FreemarkerUser.class);

	public static String mergeTemplate(Map<String, String> map, String templateText) throws TemplateException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_21);
		cfg.setIncompatibleImprovements(Configuration.VERSION_2_3_21);
		StringTemplateLoader stringLoader = new StringTemplateLoader();
		cfg.setTemplateLoader(stringLoader);
		stringLoader.putTemplate(firstTemplate, templateText);

		Template template = null;
		try {
			template = cfg.getTemplate(firstTemplate);
		} catch (IOException ex) {
			log.info("mergeTemplate: IO Exception Loading Template : ", ex);
			return null;
		}

		/*
		 * Merge data-model with template
		 */
		Writer out = new StringWriter();
		try {
			template.process(map, out);
		} catch (IOException ex) {
			log.info("mergeTemplate: IO Exception processing template: " + ex.getLocalizedMessage());
			return null;
		}
		try {
			out.flush();
		} catch (IOException ex) {
			log.info("mergeTemplate: IO Exception flushing output stream: ", ex);
			return null;
		}

		return out.toString();
	}
}
