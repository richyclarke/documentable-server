package com.vesey.documentable.session;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import com.vesey.documentable.entity.Users;
import com.vesey.documentable.security.Authenticated;

@Named
@Stateful
@SessionScoped
public class CurrentUserManager implements Serializable {
	private static final long serialVersionUID = 1L;

	private Users currentUser;

	@Produces
	@Authenticated
	@Named("currentUser")
	public Users getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(Users currentUser) {
		this.currentUser = currentUser;
	}

}
