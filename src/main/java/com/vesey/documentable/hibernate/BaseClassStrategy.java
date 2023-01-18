package com.vesey.documentable.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.MetaAttribute;

public class BaseClassStrategy extends DelegatingReverseEngineeringStrategy {

	public BaseClassStrategy(ReverseEngineeringStrategy delegate) {
		super(delegate);
	}

	@Override
	public Map<String, MetaAttribute> tableToMetaAttributes(TableIdentifier tableIdentifier) {

		Map<String, MetaAttribute> metaAttributes = super.tableToMetaAttributes(tableIdentifier);
		if (metaAttributes == null) {
			metaAttributes = new HashMap<String, MetaAttribute>();
		}

		// Update modifier
		if (!metaAttributes.containsKey("scope-class")) {
			MetaAttribute metaAttribute = new MetaAttribute("scope-class");
			metaAttribute.addValue("public abstract");
			metaAttributes.put(metaAttribute.getName(), metaAttribute);

			// Update extends modifier
			MetaAttribute metaAttribute2 = new MetaAttribute("extends");
			metaAttribute2.addValue("BaseEntity");
			metaAttributes.put(metaAttribute2.getName(), metaAttribute2);
		}

		// Update class name
		if (!metaAttributes.containsKey("generated-class")) {
			MetaAttribute metaAttribute = new MetaAttribute("generated-class");
			metaAttribute.addValue(tableToAbstractClassName(tableIdentifier));
			metaAttributes.put(metaAttribute.getName(), metaAttribute);
		}

		return metaAttributes;
	}

	public String getTableIdentifierStrategyName(TableIdentifier tableIdentifier) {
		return super.getTableIdentifierStrategyName(tableIdentifier);
	}

	private String tableToAbstractClassName(TableIdentifier tableIdentifier) {
		String className = super.tableToClassName(tableIdentifier);
		int dotIndex = className.lastIndexOf('.');
		return className.substring(0, dotIndex + 1) + className.substring(dotIndex + 1) + "Base";
	}

	@SuppressWarnings("rawtypes")
	public boolean excludeForeignKeyAsCollection(String keyname, TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns) {
		return true;
	}

	@Override
	public boolean excludeForeignKeyAsManytoOne(String keyname, TableIdentifier fromTable, List<?> fromColumns, TableIdentifier referencedTable, List<?> referencedColumns) {
		return false;
	}
}