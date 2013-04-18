package com.doitnext.http.router.exceptions;

/**
 * This exception may be thrown by server side implementation methods 
 * when an update operation would result in a duplicate key constraint violation.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class DuplicateKeyConstraintException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5004612980707199832L;

	/**
	 * @param message a message that describes the reason for the error.
	 */
	public DuplicateKeyConstraintException(String message) {
		super(message);
	}
}
