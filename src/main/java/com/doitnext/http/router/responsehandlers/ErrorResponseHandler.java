package com.doitnext.http.router.responsehandlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doitnext.http.router.PathMatch;

/**
 * The interface for Exception Handler implementations for exceptions thrown by
 * endpoint invocations.
 * 
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
public interface ErrorResponseHandler {

	/**
	 * @param e
	 *            the error thrown
	 * @param pathMatch
	 *            the {@link PathMatch} that identifies the invoked resource.
	 * @param request
	 *            the {@link HttpServletRequest} passed to the
	 *            {@link RestRouterServlet}.
	 * @param response
	 *            the {@link HttpServletResponse} passed to the
	 *            {@link RestRouterServlet}
	 * @return <code>true</code> if the error response handler handled the
	 *         response <code>false</code> otherwise.
	 */
	public boolean handleResponse(Throwable e, PathMatch pathMatch,
			HttpServletRequest request, HttpServletResponse response);
}
