package com.vesey.documentable.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.vesey.documentable.security.Logged;

@Logged
@Provider
public class RequestLoggingFilter implements ContainerRequestFilter {

	private transient static final Logger log = Logger.getLogger(RequestLoggingFilter.class);

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {
		// example to log the invoked path
		final String invokedPath = requestContext.getUriInfo().getPath();
		// log.info("Request to path has been made : " + invokedPath);
	}
}