package com.vesey.documentable.errorhandling;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ConflictException extends Exception {

	public ConflictException(Exception e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
