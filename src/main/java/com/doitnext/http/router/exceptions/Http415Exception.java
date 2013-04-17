package com.doitnext.http.router.exceptions;

/**
 * Used to handle Http415 errors from within RestRouterServlet.
 * 
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
public class Http415Exception extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2301765097582380073L;

	public Http415Exception(String contentType) {
		super(String.format(
				"HTTP 415: Unsupported media type.  The request Content-Type '%s' is not "
						+ "a content type suitable for submission "
						+ "to the server resource.", contentType));
	}

}
