package com.doitnext.http.router;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doitnext.http.router.annotations.enums.HttpMethod;

/**
 * Interface to be impemented by the invoker of methods.  Different web
 * applicatoins may choose to handle invocation differently.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public interface MethodInvoker {
	/**
	 * Invokes a method on behalf of an HttpServletRequest
	 * @param method the HttpMethod specified by the HttpServletRequest
	 * @param pm the PathMatch object containing the matched path and the route.
	 * @param req the HttpServletRequest
	 * @param resp the HttpServletResponse
	 * @return <code>true</code> if request was handled <code>false</code> otherwise.
	 * @throws ServletException
	 * 		on invocation error.
	 * @see java.lang.reflect.Method#invoke
	 */
	boolean invokeMethod(HttpMethod method, PathMatch pm,
			HttpServletRequest req, HttpServletResponse resp) throws ServletException;
}
