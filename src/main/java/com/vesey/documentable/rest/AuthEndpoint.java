package com.vesey.documentable.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.rest.dto.ActivationDTO;
import com.vesey.documentable.rest.dto.CredentialsDTO;
import com.vesey.documentable.rest.dto.DeleteAccountDTO;
import com.vesey.documentable.rest.dto.ForgotPasswordDTO;
import com.vesey.documentable.rest.dto.RegistrationDTO;
import com.vesey.documentable.rest.dto.ResetPasswordDTO;
import com.vesey.documentable.security.Logged;
import com.vesey.documentable.session.AuthFacade;

import io.swagger.v3.oas.annotations.Operation;

@Stateless
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthEndpoint {

	@Inject
	Logger log;

	@Inject
	AuthFacade authFacade;

	@POST
	@Logged
	@Path("/login")
	@Operation(summary = "Login", description = "Login with email and password", tags = { "Auth" })
	public Response login(@Context HttpServletRequest request, CredentialsDTO credentials) {
		log.info("login: Start");
		Response response = authFacade.login(credentials);
		log.info("login: End");
		return response;
	}

	@GET
	@Logged
	@Path("/profile")
	@Operation(summary = "Profile", description = "Get Profile", tags = { "Auth" })
	public Response profile(@Context HttpServletRequest request, @CookieParam("refreshToken") Cookie cookie) {
		log.info("profile: Start");
		Response response = authFacade.profile(cookie);
		log.info("profile: End");
		return response;
	}

	@POST
	@Logged
	@Path("/register")
	@Operation(summary = "Register", description = "Register as a new user", tags = { "Auth" })
	public Response register(@Context HttpServletRequest request, RegistrationDTO credentials) {
		log.info("register: Start");
		Response response = authFacade.register(credentials);
		log.info("register: End");
		return response;
	}

	@POST
	@Logged
	@Path("/activate")
	@Operation(summary = "Activate", description = "Activate as a new user", tags = { "Auth" })
	public Response activate(@Context HttpServletRequest request, ActivationDTO credentials) {
		log.info("activate: Start");
		Response response = authFacade.activate(credentials);
		log.info("activate: End");
		return response;
	}

	@POST
	@Logged
	@Path("/autologin")
	@Operation(summary = "Autologin", description = "Autologin a user user", tags = { "Auth" })
	public Response autologin(@Context HttpServletRequest request, ActivationDTO credentials) {
		log.info("autologin: Start");
		Response response = authFacade.autologin(credentials);
		log.info("autologin: End");
		return response;
	}

	@POST
	@Logged
	@Path("/forgot-password")
	@Operation(summary = "Forgot Password", description = "Forgot Password", tags = { "Auth" })
	public Response forgotPassword(@Context HttpServletRequest request, ForgotPasswordDTO resetPasswordDTO) throws ConflictException {
		log.info("forgotPassword: Start");
		Response response = authFacade.forgotPassword(resetPasswordDTO);
		log.info("forgotPassword: End");
		return response;
	}

	@POST
	@Logged
	@Path("/delete-account")
	@Operation(summary = "Delete Account", description = "Delete Account", tags = { "Auth" })
	public Response deleteAccount(@Context HttpServletRequest request, DeleteAccountDTO deleteAccountDTO) throws ConflictException {
		log.info("deleteAccount: Start");
		Response response = authFacade.deleteAccount(deleteAccountDTO);
		log.info("deleteAccount: End");
		return response;
	}

	@POST
	@Logged
	@Path("/reset-password")
	@Operation(summary = "Reset Password", description = "Reset Password", tags = { "Auth" })
	public Response resetPassword(@Context HttpServletRequest request, ResetPasswordDTO resetPasswordDTO) throws ConflictException {
		log.info("resetPassword: Start");
		Response response = authFacade.resetPassword(resetPasswordDTO);
		log.info("resetPassword: End");
		return response;
	}

	@POST
	@Logged
	@Path("/update-password")
	@Operation(summary = "Update Password", description = "Update Password", tags = { "Auth" })
	public Response updatePassword(@Context HttpServletRequest request, ResetPasswordDTO updatePasswordDTO) throws ConflictException {
		log.info("updatePassword: Start");
		Response response = authFacade.updatePassword(updatePasswordDTO);
		log.info("updatePassword: End");
		return response;
	}

	@POST
	@Logged
	@Path("/validate-reset-password")
	@Operation(summary = "Validate Reset Password", description = "Validate Reset Password", tags = { "Auth" })
	public Response validateResetPassword(@Context HttpServletRequest request, ResetPasswordDTO resetPasswordDTO) {
		log.info("validateResetPassword: Start");
		Response response = authFacade.validateResetPassword(resetPasswordDTO);
		log.info("validateResetPassword: End");
		return response;
	}

	@POST
	@Logged
	@Path("/refresh-token")
	@Operation(summary = "Refresh Token", description = "Submit with a valid refresh token to retrieve refreshed tokens with a new expiry time", tags = {
			"Auth" })
	public Response refreshAccessToken(@Context HttpServletRequest request, @CookieParam("refreshToken") Cookie cookie) {
		log.info("refreshAccessToken: Start");
		Response response = authFacade.profile(cookie);
		log.info("refreshAccessToken: End");
		return response;
	}

}
