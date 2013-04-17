package com.doitnext.http.router.exceptions;

import java.util.List;

import com.doitnext.http.router.annotations.enums.HttpMethod;

/**
 * Used to handle Http405 errors from within RestRouterServlet.
 * 
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
public class Http405Exception extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2301765097582380073L;

	public Http405Exception(HttpMethod method, List<String> allowedMethods) {
		super(String.format(
				"HTTP 405: The requested method %s is now allowed for this resource. "
						+ "The allowed methods are ", method.name(),
				allowedMethods));
	}
}
