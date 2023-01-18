package com.vesey.documentable.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.vesey.documentable.security.Logged;

@Logged
@Provider
public class ResponseLoggingFilter implements ContainerResponseFilter {
	private transient static final Logger log = Logger.getLogger(ResponseLoggingFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		// example to log the invoked path
		final String invokedPath = requestContext.getUriInfo().getPath();
		// log.info("Response from path has been made : " + invokedPath);
	}
}