package com.doitnext.http.router.exceptions;

/**
 * Used to handle Http406 errors from within RestRouterServlet.
 * 
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
public class Http406Exception extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2301765097582380073L;

	public Http406Exception() {
		super(
				String.format("HTTP 406: Not acceptable. The requested resource cannot be served as any of"
						+ " the content types specified in the HTTP request 'Accept' header."));
	}

}
