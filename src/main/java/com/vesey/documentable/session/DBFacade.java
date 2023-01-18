package com.vesey.documentable.session;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.logging.Logger;

import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.utils.Utils;

@Named
@Stateful
@RequestScoped
public class DBFacade implements Serializable {

	@Inject
	Logger log;

	@PersistenceContext(unitName = "documentablePU")
	EntityManager em;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public <T extends Object> T getEntity(Class<T> type, String id) {
		try {
			if (id == null) {
				log.error("getEntity: NULL passed in as id");
				return null;
			}
			return em.find(type, id);
		} catch (Exception ex) {
			log.warn("getEntity: Exception: no Entity found for ID : " + id);
			log.error(ex);
			return null;
		}
	}

	public <T extends Object> T getEntity(Class<T> type, Integer id) {
		try {
			if (id == null) {
				log.error("getEntity: NULL passed in as id");
				return null;
			}
			return em.find(type, id);
		} catch (Exception e) {
			log.warn("getEntity: Exception: no Entity found for ID : " + id);
			log.error(e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> List<T> doListQuery(Class<T> type, String sql, Map<String, Object> params, boolean nativeQuery) {
		if (type == null) {
			log.error("doListQuery: type is NULL");
			return null;
		}

		if (Utils.isEmpty(sql)) {
			log.error("doListQuery: sql is NULL");
			return null;
		}

		Query query = null;

		if (nativeQuery) {
			query = em.createNativeQuery(sql);
		} else {
			query = em.createQuery(sql);
		}

		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}

		try {
			return (List<T>) query.getResultList();
		} catch (Exception ex) {
			log.error("doListQuery: ", ex);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Integer> doIntegerListQuery(String sql, Map<String, Object> params, boolean nativeQuery) {

		if (Utils.isEmpty(sql)) {
			log.error("doIntegerListQuery: sql is NULL");
			return null;
		}

		Query query = null;

		if (nativeQuery) {
			query = em.createNativeQuery(sql);
		} else {
			query = em.createQuery(sql);
		}

		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}

		try {
			return query.getResultList();
		} catch (Exception ex) {
			log.error("doIntegerListQuery: ", ex);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> List<T> getEntityList(Class<T> type, String sql, Map<String, Object> params,
			boolean nativeQuery, Integer first, Integer maxResults) {
		if (type == null) {
			log.error("getEntityList: type is NULL");
			return null;
		}

		Query query = null;

		if (nativeQuery) {
			query = em.createNativeQuery(sql);
		} else {
			query = em.createQuery(sql);
		}

		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}

		if (first != null) {
			query.setFirstResult(first);
		}

		if (maxResults != null) {
			query.setMaxResults(maxResults);
		}

		try {
			return query.getResultList();
		} catch (Exception ex) {
			log.error("getEntityList: ", ex);
			return null;
		}
	}

	public Integer getEntityListCount(String sql, Map<String, Object> params, boolean nativeQuery) {

		Query query = null;

		if (nativeQuery) {
			query = em.createNativeQuery(sql);
		} else {
			query = em.createQuery(sql);
		}

		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}

		try {
			Object obj = query.getSingleResult();
			if (obj instanceof BigInteger) {
				return ((BigInteger) obj).intValue();
			} else if (obj instanceof Integer) {
				return (Integer) obj;
			} else if (obj instanceof Long) {
				return ((Long) obj).intValue();
			} else {
				return 0;
			}
		} catch (NoResultException ex) {
			return 0;
		} catch (Exception ex) {
			log.error("getEntityList: ", ex);
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T getEntityQuery(Class<T> type, String sql, Map<String, Object> params,
			boolean nativeQuery) {
		if (type == null) {
			log.error("getEntityQuery: type is NULL");
			return null;
		}

		if (Utils.isEmpty(sql)) {
			log.error("getEntityQuery: sql is NULL");
			return null;
		}

		Query query = null;

		if (nativeQuery) {
			query = em.createNativeQuery(sql);
		} else {
			query = em.createQuery(sql);
		}

		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}

		try {
			return (T) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		} catch (NonUniqueResultException ex) {
			log.error("getEntityQuery: NonUniqueResultException: ", ex);
			return null;
		} catch (Exception ex) {
			log.error("getEntityQuery: Exception : ", ex);
			return null;
		}
	}

	public <T extends Object> T merge(T c) throws ConflictException {
		try {
			T x = em.merge(c);
			return x;
		} catch (org.hibernate.StaleObjectStateException e) {
			log.warn("merge: StaleObjectStateException  : ", e);
			throw new ConflictException(e);
		} catch (javax.persistence.OptimisticLockException e) {
			log.warn("merge: OptimisticLockException  : ", e);
			throw new ConflictException(e);
		}
	}

	public void persist(Object c) {
		em.persist(c);
	}

	public void delete(Object c) {
		em.remove(c);

	}

}
