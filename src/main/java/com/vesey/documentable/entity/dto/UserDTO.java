package com.vesey.documentable.entity.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "User")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO extends BaseEntityDTO {

	private String name;
	protected String email;
	protected String profilepicturefilename;

	private String role;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProfilepicturefilename() {
		return profilepicturefilename;
	}

	public void setProfilepicturefilename(String profilepicturefilename) {
		this.profilepicturefilename = profilepicturefilename;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	// public Collection<TripDTO> getTrips() {
	// return trips;
	// }
	//
	// public void setTrips(Collection<TripDTO> trips) {
	// this.trips = trips;
	// }

}
