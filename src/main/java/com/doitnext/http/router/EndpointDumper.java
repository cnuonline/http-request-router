package com.doitnext.http.router;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.JsonMappingException;

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
	 * @throws JsonMappingException 
	 * @throws IOException 
	 */
	void dumpEndpoints(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
