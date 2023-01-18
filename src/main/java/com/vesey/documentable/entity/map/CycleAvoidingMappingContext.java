package com.vesey.documentable.entity.map;

import java.util.IdentityHashMap;
import java.util.Map;

import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

import com.vesey.documentable.entity.Users;

/**
 * A type to be used as {@link Context} parameter to track cycles in graphs.
 * <p>
 * Depending on the actual use case, the two methods below could also be changed to only accept certain argument types, e.g. base classes of graph nodes, avoiding the need to capture any other objects that wouldn't necessarily result in cycles.
 *
 * @author Andreas Gudian
 */
public class CycleAvoidingMappingContext {
	private Map<Object, Object> knownInstances = new IdentityHashMap<Object, Object>();
	private Users user;

	public CycleAvoidingMappingContext() {
	}

	public CycleAvoidingMappingContext(Users user) {
		this.user = user;
	}

	@SuppressWarnings("unchecked")
	@BeforeMapping
	public <T> T getMappedInstance(Object source, @TargetType Class<T> targetType) {
		return (T) knownInstances.get(source);
	}

	@BeforeMapping
	public void storeMappedInstance(Object source, @MappingTarget Object target) {
		knownInstances.put(source, target);
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

}