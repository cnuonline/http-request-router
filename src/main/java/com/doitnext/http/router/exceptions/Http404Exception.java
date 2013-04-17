package com.doitnext.http.router.exceptions;

/**
 * Used to handle Http404 errors from within RestRouterServlet.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class Http404Exception extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2301765097582380073L;

	public Http404Exception() {
		super("HTTP 404: The requested URL [URL] was not found on this server.");
	}

}
