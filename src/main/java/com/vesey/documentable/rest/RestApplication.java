package com.vesey.documentable.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@ApplicationPath("/rest")
@OpenAPIDefinition(info = @Info(description = "Documentable REST API", version = "Version 1.0.0", title = "Documentable UI (React) REST API", contact = @Contact(name = "Richard Clarke", email = "richard.clarke@veseypartnership.com", url = "http://www.documentable.co.uk/doucmentable")), servers = {
		@Server(url = "https://www.documentable.co.uk/documentable") })

public class RestApplication extends Application {

}
