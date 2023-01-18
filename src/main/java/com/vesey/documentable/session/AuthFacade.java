package com.vesey.documentable.session;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.entity.dto.UserDTO;
import com.vesey.documentable.entity.map.CycleAvoidingMappingContext;
import com.vesey.documentable.entity.map.UserMapperImpl;
import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.rest.dto.ActivationDTO;
import com.vesey.documentable.rest.dto.CredentialsDTO;
import com.vesey.documentable.rest.dto.DeleteAccountDTO;
import com.vesey.documentable.rest.dto.ForgotPasswordDTO;
import com.vesey.documentable.rest.dto.LoginResponseDTO;
import com.vesey.documentable.rest.dto.RegistrationDTO;
import com.vesey.documentable.rest.dto.ResetPasswordDTO;
import com.vesey.documentable.utils.Utils;

import de.rtner.security.auth.spi.PBKDF2Engine;
import de.rtner.security.auth.spi.PBKDF2Formatter;
import de.rtner.security.auth.spi.PBKDF2HexFormatter;
import de.rtner.security.auth.spi.PBKDF2Parameters;

@Stateless
public class AuthFacade implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
	Logger log;
	@Inject
	DBFacade dbFacade;
	@Inject
	CurrentUserManager currentUserManager;
	@Inject
	Emailer emailer;

	@Inject
	UserMapperImpl userMapper;

	@javax.annotation.Resource
	private TransactionSynchronizationRegistry reg;

	public final static String PRIVATE_KEY = "VillaWalsall99";
	public final static Integer REFRESH_TOKEN_TIMEOUT = 262800; // 262800 = 6 months in minutes
	public final static Integer ACCESS_TOKEN_TIMEOUT = 30; // in minutes

	public Users getUserFromToken(String authorizationHeader, DBFacade dbFacade) {
		Users user = null;
		if (authorizationHeader != null && authorizationHeader.toLowerCase().startsWith("bearer ")) {
			// Extract the token from the Authorization header
			String token = authorizationHeader.substring(7).trim();
			Algorithm algorithm = Algorithm.HMAC256(PRIVATE_KEY);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build(); // Reusable verifier instance

			DecodedJWT jwt = verifier.verify(token);

			String username = jwt.getSubject();
			user = Users.getByUsername(username, dbFacade);
		}

		return user;
	}

	public Response profile(Cookie cookie) {
		// log.info("profile: Start");
		// if cookie contains valid refresh token, then issue new refresh and access tokens and return user info
		Response response = null;
		if (cookie == null) {
			return Response.serverError().entity("No Authentication Cookie Found").build();
		}

		try {
			// Authenticate the user using the cookie provided
			Users user = authenticate(cookie);
			if (user == null) {
				log.warn("profile: User cannot be autheenticated.");
				return Response.status(Status.BAD_REQUEST).entity("User authentication failed").build();
			}
			if (!user.isActivated()) {
				log.warn("profile: user is not activated. Returning FALSE.");
				response = Response.status(403).entity("User is not activated. Please follow instructions in welcome email and try again.").build();
			} else {
				UserDTO userDTO = userMapper.getDTOFromUser(user, new CycleAvoidingMappingContext(user));

				String accessToken = issueToken(user.getUsername(), ACCESS_TOKEN_TIMEOUT); // short lived
				LoginResponseDTO loginResponse = new LoginResponseDTO(userDTO, accessToken);

				String result = Utils.convertToJSON(loginResponse);
				// Issue a token for the user
				String refreshToken = issueToken(user.getUsername(), REFRESH_TOKEN_TIMEOUT); // long lived
				NewCookie newCookie = createRefreshToken(refreshToken);

				response = Response.ok(result).cookie(newCookie).build();
			}
		} catch (Exception e) {
			log.warn("profile: Exception: ", e);
			response = Response.status(401).entity(e.getLocalizedMessage()).build();
		}

		return response;
	}

	public Response login(CredentialsDTO credentials) {
		log.info("login: Start");
		Response response = null;
		try {
			// Authenticate the user using the credentials provided
			Users user = authenticate(credentials);

			if (!user.isActivated()) {
				log.warn("login: user is not activated. Returning FALSE.");
				response = Response.status(403).entity("User is not activated. Please follow instructions in welcome email and try again.").build();
			} else {

				// Issue a refresh token only for the user
				String refreshToken = issueToken(credentials.getUsername(), REFRESH_TOKEN_TIMEOUT); // long lived
				NewCookie cookie = createRefreshToken(refreshToken);

				response = Response.ok("OK").cookie(cookie).build();
			}
		} catch (Exception e) {
			log.warn("login: Exception: " + e.getLocalizedMessage());
			response = Response.status(401).entity(e.getLocalizedMessage()).build();
		}

		return response;
	}

	// public Response login(CredentialsDTO credentials) {
	// log.info("login: Start");
	// Response response = null;
	// try {
	// // Authenticate the user using the credentials provided
	// Users user = authenticate(credentials);
	//
	// if (!user.isActivated()) {
	// log.warn("login: user is not activated. Returning FALSE.");
	// response = Response.status(403).entity("User is not activated. Please follow instructions in welcome email and try again.").build();
	// } else {
	//
	// // Issue a token for the user
	// String refreshToken = issueToken(credentials.getEmail(), REFRESH_TOKEN_TIMEOUT); // long lived
	// String accessToken = issueToken(credentials.getEmail(), ACCESS_TOKEN_TIMEOUT); // short lived
	//
	// UserDTO userDTO = userMapper.getDTOFromUser(user, new CycleAvoidingMappingContext(user));
	//
	// LoginResponseDTO loginResponse = new LoginResponseDTO(userDTO, accessToken, refreshToken);
	//
	// String result = Utils.convertToJSON(loginResponse);
	// response = Response.ok(result).build();
	// }
	// } catch (Exception e) {
	// log.warn("login: Exception: " + e.getLocalizedMessage());
	// response = Response.status(401).entity(e.getLocalizedMessage()).build();
	// }
	//
	// return response;
	// }

	public NewCookie createRefreshToken(String refreshToken) {
		NewCookie cookie = new NewCookie("refreshToken", refreshToken, "/", null, "Refresh Token", REFRESH_TOKEN_TIMEOUT, true, true); // name and value of the cookie

		return cookie;
	}

	public Response activate(ActivationDTO credentials) {
		log.info("activate: Start");
		Response response = null;
		try {
			// Authenticate the user using the credentials provided
			Users user = Users.getByUsername(credentials.getUsername(), dbFacade);
			if (user != null) {
				if (Utils.isNotEmpty(user.getUuid()) && Utils.isNotEmpty(credentials.getUuid()) && user.getUuid().compareTo(credentials.getUuid()) != 0)

				{
					if (!user.isActivated()) {
						log.warn("activate: user is not activated - activate");
						user.setActivated(true);
						user = dbFacade.merge(user);
						response = Response.ok("User is now activated.").build();
					} else {

						response = Response.ok("User is already activated.").build();
					}
				} else {
					response = Response.status(400).entity("Unable to activate. Please try again").build();
				}
			} else {
				log.warn("activate: User not found for usernME : " + credentials.getUsername());
				response = Response.status(401).entity("User not found").build();
			}
		} catch (ConflictException e) {
			log.warn("activate: ConflictException: " + e.getLocalizedMessage());
			response = Response.status(409).entity(e.getLocalizedMessage()).build();
		}

		return response;
	}

	public Response autologin(ActivationDTO credentials) {
		log.info("autologin: Start");
		Response response = null;

		// Authenticate the user using the credentials provided
		Users user = Users.getByUsername(credentials.getUsername(), dbFacade);
		if (user != null) {
			if (Utils.isNotEmpty(user.getUuid()) && Utils.isNotEmpty(credentials.getUuid()) && user.getUuid().compareTo(credentials.getUuid()) != 0)

			{
				// Issue a token for the user
				String accessToken = issueToken(credentials.getUsername(), ACCESS_TOKEN_TIMEOUT); // short lived

				UserDTO userDTO = userMapper.getDTOFromUser(user, new CycleAvoidingMappingContext(user));

				LoginResponseDTO loginResponse = new LoginResponseDTO(userDTO, accessToken);

				String result = Utils.convertToJSON(loginResponse);

				// Issue a refresh token for the user and attach it as cookie
				String refreshToken = issueToken(credentials.getUsername(), REFRESH_TOKEN_TIMEOUT); // long lived
				NewCookie cookie = createRefreshToken(refreshToken);

				response = Response.ok(result).cookie(cookie).build();
			} else {
				response = Response.status(400).entity("Unable to activate. Please try again").build();
			}
		} else {
			log.warn("autologin: User not found for username : " + credentials.getUsername());
			response = Response.status(401).entity("User not found").build();
		}

		return response;
	}

	public Response register(RegistrationDTO credentials) {
		log.info("register: Start");
		Response response = null;
		try {

			// Check the user doesn't already exist
			Users foundUser = Users.getByUsername(credentials.getUsername(), dbFacade);
			if (foundUser != null) {
				log.warn("registration: user already exists with username : " + credentials.getUsername());
				response = Response.status(Response.Status.CONFLICT).entity("The username '" + credentials.getUsername() + "' already belongs to an existing user").build();
			} else {
				// TODO = Check organisation
				Users newUser = new Users(credentials);
				dbFacade.persist(newUser);

				emailer.sendRegistrationEmail(newUser.getEmail(), newUser.getName(), newUser.getUuid());

				UserDTO userDTO = userMapper.getDTOFromUser(newUser, new CycleAvoidingMappingContext(newUser));

				String result = Utils.convertToJSON(userDTO);
				response = Response.ok(result).build();
			}
		} catch (Exception e) {
			log.warn("register: Exception: " + e.getLocalizedMessage());
			response = Response.status(Response.Status.BAD_REQUEST).entity(e.getLocalizedMessage()).build();
		}

		return response;
	}

	public Response forgotPassword(ForgotPasswordDTO forgotPasswordDTO) throws ConflictException {
		log.info("forgotPassword: Start");
		Response response = null;

		Users user = Users.getByUsername(forgotPasswordDTO.getUsername(), dbFacade);

		if (user != null) {
			user.setPasswordresetexpiry(new DateTime().plusHours(1).toDate());
			user.setPasswordresetuuid(UUID.randomUUID().toString());
			user = dbFacade.merge(user);
			String url = Utils.getPasswordResetURL(user.getPasswordresetuuid());

			emailer.sendPasswordResetEmail(user, url);
		}
		response = Response.ok().build();

		log.info("forgotPassword: End");
		return response;
	}

	public Response deleteAccount(DeleteAccountDTO deleteAccountDTO) throws ConflictException {
		log.info("deleteAccount: Start");
		Response response = null;

		Users user = Users.getByUuid(dbFacade, deleteAccountDTO.getUuid());

		if (user != null) {
			user.setEmail("deleted");
			user.setProfilepicturefilename(null);
			user.setActivated(false);
			user = dbFacade.merge(user);
		}
		response = Response.ok().build();

		log.info("deleteAccount: End");
		return response;
	}

	public Response updatePassword(ResetPasswordDTO updatePasswordDTO) throws ConflictException {
		log.info("updatePassword: Start");
		Response response = null;

		String validationError = checkPasswordUpdateIsValid(updatePasswordDTO);

		if (Utils.isNotEmpty(validationError)) {
			response = Response.status(403).entity(validationError).build();
		} else {
			Users user = Users.getById(dbFacade, updatePasswordDTO.getUserId());
			hashAndSavePassword(updatePasswordDTO.getNewPassword(), user);
			user.setPasswordresetexpiry(null);
			user.setPasswordresetuuid(null);
			user.setTokenvalidfrom(new Date());
			user.setLoginattempts(0);
			user = dbFacade.merge(user);
			response = Response.ok().build();
		}

		log.info("updatePassword: End");
		return response;
	}

	public Response validateUpdatePassword(ResetPasswordDTO updatePasswordDTO) throws ConflictException {
		log.info("validateResetPassword: Start");
		Response response = null;

		String validationError = checkPasswordUpdateIsValid(updatePasswordDTO);

		if (Utils.isNotEmpty(validationError)) {
			response = Response.status(403).entity(validationError).build();
		} else {
			response = Response.ok().build();
		}

		log.info("validateResetPassword: End");
		return response;
	}

	private String checkPasswordUpdateIsValid(ResetPasswordDTO updatePasswordDTO) throws ConflictException {
		if (updatePasswordDTO.getToken() == null) {
			return "Your current password is incorrect. Please try again.";
		} else if (updatePasswordDTO.getUserId() == null) {
			return "Unable to determine user.";
		} else if (updatePasswordDTO.getNewPassword() == null || updatePasswordDTO.getNewPassword().length() < 8) {
			return "New Password must be at least 8 characters long. Please try again.";
		} else {
			Users user = Users.getById(dbFacade, updatePasswordDTO.getUserId());

			if (!validatePassword(user, updatePasswordDTO.getToken())) {
				return "Your current password is incorrect. Please try again.";
			}
		}
		return null;
	}

	public Response resetPassword(ResetPasswordDTO resetPasswordDTO) throws ConflictException {
		log.info("resetPassword: Start");
		Response response = null;

		String validationError = checkPasswordResetIsValid(resetPasswordDTO);

		if (Utils.isNotEmpty(validationError)) {
			response = Response.status(401).entity(validationError).build();
		} else {
			Users user = Users.getById(dbFacade, resetPasswordDTO.getUserId());
			hashAndSavePassword(resetPasswordDTO.getNewPassword(), user);
			user.setPasswordresetexpiry(null);
			user.setPasswordresetuuid(null);
			user.setTokenvalidfrom(new Date());
			user.setLoginattempts(0);
			user = dbFacade.merge(user);
			response = Response.ok().build();
		}

		log.info("resetPassword: End");
		return response;
	}

	public Response validateResetPassword(ResetPasswordDTO resetPasswordDTO) {
		log.info("validateResetPassword: Start");
		Response response = null;

		String validationError = checkPasswordResetIsValid(resetPasswordDTO);

		if (Utils.isNotEmpty(validationError)) {
			response = Response.status(401).entity(validationError).build();
		} else {
			response = Response.ok().build();
		}

		log.info("validateResetPassword: End");
		return response;
	}

	private String checkPasswordResetIsValid(ResetPasswordDTO resetPasswordDTO) {
		if (resetPasswordDTO.getToken() == null || resetPasswordDTO.getUserId() == null) {
			return "Link is Invalid. Please request a new password reset link.";
		} else {
			Users user = Users.getById(dbFacade, resetPasswordDTO.getUserId());
			if (user == null || Utils.isEmpty(user.getPasswordresetuuid())
					|| !resetPasswordDTO.getToken().equals(user.getPasswordresetuuid())) {
				return "Link not recognised. Please request a new password reset link.";
			}
			if (user.getPasswordresetexpiry() == null || user.getPasswordresetexpiry().before(new Date())) {
				return "Link expired. Please request a new password reset link.";
			}
		}
		return null;
	}

	public Response refreshAccessToken(String refreshToken) {
		log.info("refreshAccessToken: Start");
		Response response = null;
		try {
			DecodedJWT decodedToken = JWT.decode(refreshToken);

			if (decodedToken != null) {
				log.info("refreshAccessToken: Refresh Token expires at : " + decodedToken.getExpiresAt());
				if (decodedToken.getExpiresAt().before(new Date())) {
					throw new TokenExpiredException("Token has expired");
				} else {

					Users user = Users.getByUsername(decodedToken.getSubject(), dbFacade);
					if (user != null) {
						if (decodedToken.getIssuedAt() == null || user.getTokenvalidfrom() == null
								|| (user.getTokenvalidfrom() != null && user.getTokenvalidfrom().after(decodedToken.getIssuedAt()))) {

							// Issue a token for the user
							String accessToken = issueToken(decodedToken.getSubject(), ACCESS_TOKEN_TIMEOUT);

							UserDTO userDTO = userMapper.getDTOFromUser(user, new CycleAvoidingMappingContext(user));

							LoginResponseDTO loginResponse = new LoginResponseDTO(userDTO, accessToken);
							String result = Utils.convertToJSON(loginResponse);
							response = Response.ok(result).build();
						} else {
							throw new TokenExpiredException("Token has expired");
						}
					} else {
						response = Response.status(Response.Status.UNAUTHORIZED).build();
					}
				}
			} else {
				response = Response.status(Response.Status.UNAUTHORIZED).build();
			}
		} catch (TokenExpiredException e) {
			log.warn("refreshAccessToken: Exception: " + e.getLocalizedMessage());
			response = Response.status(Response.Status.UNAUTHORIZED).build();
		} catch (Exception e) {
			log.error("refreshAccessToken: Exception: ", e);
			response = Response.status(Response.Status.FORBIDDEN).build();
		}
		return response;
	}

	public Users authenticate(Cookie cookie) {
		// log.info("authenticate(cookie): Start");
		String refreshToken = cookie.getValue();
		// log.info("authenticate(cookie): refreshToken = " + refreshToken);
		if (Utils.isNotEmpty(refreshToken)) {

			try {
				DecodedJWT decodedToken = JWT.decode(refreshToken);
				if (decodedToken != null) {
					// log.info("authenticate(cookie): Access Token expires at : " + decodedToken.getExpiresAt());
				}
				Algorithm algorithm = Algorithm.HMAC256(AuthFacade.PRIVATE_KEY);
				JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build(); // Reusable verifier instance
				verifier.verify(refreshToken);

				Users user = Users.getByUsername(decodedToken.getSubject(), dbFacade);
				if (user != null) {
					if (decodedToken.getIssuedAt() != null || user.getTokenvalidfrom() == null
							|| user.getTokenvalidfrom().after(decodedToken.getIssuedAt())) {

						// all good - return user
						return user;
					} else {
						log.warn("authenticate(cookie): refreshToken has expired: ");
						log.warn("authenticate(cookie): decodedToken.getIssuedAt(): " + decodedToken.getIssuedAt());
						log.warn("authenticate(cookie): user.getTokenvalidfrom(): " + user.getTokenvalidfrom());
					}
				}
			} catch (Exception e) {
				log.warn("authenticate(cookie): refreshToken is not valid: " + e.getLocalizedMessage());
			}
		}
		// log.info("authenticate(cookie): End - returning null");
		return null;
	}

	public Users authenticate(CredentialsDTO credentials) throws Exception {
		return authenticate(credentials.getUsername(), credentials.getPassword());
	}

	public Users authenticate(String username, String password) throws Exception {
		Users theUser = Users.getByUsername(username, dbFacade);
		if (theUser == null) {
			throw new Exception("Incorrect username or password.\nPlease try again.");
		}

		if (Utils.isNotEmpty(theUser.getLoginattempts()) && theUser.getLoginattempts() >= 999) {
			if (Utils.isNotEmpty(theUser.getLastloginattempt()) && theUser.getLastloginattempt().after(new DateTime().minusMinutes(5).toDate())) {
				Integer minutesToGo = Minutes.minutesBetween(new DateTime().minusMinutes(5), new DateTime(theUser.getLastloginattempt())).getMinutes() + 1;
				throw new Exception("Too many failed login attempts.\nPlease try again in " + minutesToGo + " minute" + (minutesToGo == 1 ? "" : "s"));
			} else {
				theUser.setLoginattempts(0);
				theUser = dbFacade.merge(theUser);
			}
		}

		if (!validatePassword(theUser, password)) {
			theUser.setLoginattempts(theUser.getLoginattempts() + 1);
			theUser.setLastloginattempt(new Date());
			theUser = dbFacade.merge(theUser);
			throw new Exception("Incorrect username or password.\nPlease try again.");
		}

		theUser.setLoginattempts(0);
		theUser.setLastloginattempt(new Date());
		theUser = dbFacade.merge(theUser);
		return theUser;

	}

	public void hashAndSavePassword(String password, Users user) throws ConflictException {

		if (Utils.isEmpty(password)) {
			log.error("hasAndSavePassword: Password is NULL");
			return;
		}

		if (user == null) {
			log.error("hasAndSavePassword: User is NULL");
			return;
		}
		try {
			String passwordString = createHashedPassword(password);
			user.setHashedpassword(passwordString);

			user = dbFacade.merge(user);
		} catch (NoSuchAlgorithmException e) {
			log.error("hashAndSavePassword: NoSuchAlgorithmException: ", e);
		}
	}

	public boolean validatePassword(Users user, String password) throws ConflictException {

		if (user == null) {
			log.error("validatePassword: user is NULL. Returning FALSE.");
			return false;
		}

		if (Utils.isEmpty(user.getHashedpassword())) {
			log.error("validatePassword: user doesn't have a hashed password. Returning FALSE.");
			// return false;
			// for now let's set the hashed password and return true

			hashAndSavePassword(password, user);
			user.setPasswordresetexpiry(null);
			user.setPasswordresetuuid(null);
			user.setTokenvalidfrom(new Date());
			user.setLoginattempts(0);
			user = dbFacade.merge(user);
		}

		if (Utils.isEmpty(password)) {
			log.error("validatePassword: password is NULL/ EMPTY. Returning FALSE.");
			return false;
		}

		String[] parts = user.getHashedpassword().split(":");

		int iterations = Integer.parseInt(parts[1]);
		byte[] salt = Utils.fromHex(parts[0]);
		byte[] hash = Utils.fromHex(parts[2]);

		PBKDF2Parameters p = new PBKDF2Parameters("HmacSHA256", "ISO-8859-1", salt, iterations);
		PBKDF2Engine e = new PBKDF2Engine(p);
		p.setDerivedKey(e.deriveKey(password));

		byte[] testHash = p.getDerivedKey();
		int diff = hash.length ^ testHash.length;
		for (int i = 0; i < hash.length && i < testHash.length; i++) {
			diff |= hash[i] ^ testHash[i];
		}
		return diff == 0;

		// } catch (NoSuchAlgorithmException e) {
		// log.error("validatePassword: NoSuchAlgorithmException: ", e);
		// } catch (InvalidKeySpecException e) {
		// log.error("validatePassword: InvalidKeySpecException: ", e);
		// }

		// return false;
	}

	public static String createHashedPassword(String password) throws NoSuchAlgorithmException {
		PBKDF2Formatter formatter = new PBKDF2HexFormatter();
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[8];
		sr.nextBytes(salt);
		int iterations = 1000;
		PBKDF2Parameters p = new PBKDF2Parameters("HmacSHA256", "ISO-8859-1", salt, iterations);
		PBKDF2Engine e = new PBKDF2Engine(p);
		p.setDerivedKey(e.deriveKey(password));
		return formatter.toString(p);
	}

	private String issueToken(String username, Integer mins) {
		// Issue a token (can be a random String persisted to a database or a JWT token)
		// The issued token must be associated to a user
		// Return the issued token

		String token = null;

		try {
			Algorithm algorithm = Algorithm.HMAC256(PRIVATE_KEY);
			Date exp = new DateTime().plusMinutes(mins).toDate();
			log.info("issueToken: Creating token with expiry date : " + exp);
			token = JWT.create().withIssuer("auth0").withExpiresAt(exp).withSubject(username).sign(algorithm);
		} catch (JWTCreationException exception) {
			log.error("login: Exception: ", exception);
			return null;
		}

		return token;
	}

}
