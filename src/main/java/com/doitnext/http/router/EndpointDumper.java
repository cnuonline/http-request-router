package com.doitnext.http.router;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Used to dump the endpoints in response to an endpoint dump request.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public interface EndpointDumper {

	/**
	 * @return the formats supported for the response (e.g. "application/json")
	 */
	List<String> getReturnFormats();
	
	/**
	 * @param req the HttpServletRequest being responded to
	 * @param resp the HttpServletResponse on which the endpoints will be dumped.
	 */
	void dumpEndpoints(HttpServletRequest req, HttpServletResponse resp);
}
