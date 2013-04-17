package com.doitnext.http.router.exceptions;

/**
 * Used to handle Http500 errors from within RestRouterServlet.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class Http500Exception extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2301765097582380073L;

	public Http500Exception(Throwable t) {
		super("HTTP 500: Internal server error.", t);
	}

}
