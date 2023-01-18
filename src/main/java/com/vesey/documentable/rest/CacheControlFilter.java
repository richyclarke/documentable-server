package com.vesey.documentable.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.vesey.documentable.security.NoCache;

@NoCache
@Provider
public class CacheControlFilter implements ContainerResponseFilter {

	private transient static final Logger log = Logger.getLogger(CacheControlFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		// log.info("CacheControlFilter:filter: Start");
		if (requestContext.getMethod().equals("GET"))

		{
			CacheControl cacheControl = new CacheControl();
			cacheControl.setNoStore(true);
			cacheControl.setNoCache(true);
			cacheControl.setMustRevalidate(true);
			cacheControl.setProxyRevalidate(true);
			cacheControl.setMaxAge(0);
			cacheControl.setSMaxAge(0);

			responseContext.getHeaders().add(HttpHeaders.CACHE_CONTROL, cacheControl.toString());
			responseContext.getHeaders().add(HttpHeaders.EXPIRES, 0);
		}
		// log.info("CacheControlFilter: Cache-Control headers added");
	}
}
