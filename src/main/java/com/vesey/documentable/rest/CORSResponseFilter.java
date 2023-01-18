package com.vesey.documentable.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class CORSResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

		MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		if (!responseContext.getHeaders().containsKey("Access-Control-Allow-Credentials")) {
			headers.add("Access-Control-Allow-Credentials", "true");
		}

		if (!responseContext.getHeaders().containsKey("Access-Control-Allow-Origin")) {
			headers.add("Access-Control-Allow-Origin", "http://127.0.0.1:3000");
		}

		if (!responseContext.getHeaders().containsKey("Access-Control-Allow-Methods")) {
			headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
		}
		headers.add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");

		headers.add("Transfer-Encoding", "identity");
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// TODO Auto-generated method stub

	}

}