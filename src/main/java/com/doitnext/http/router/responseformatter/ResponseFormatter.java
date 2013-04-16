package com.doitnext.http.router.responseformatter;

import java.net.URI;

/**
 * Formats a java object to a specific format with amplifying details provided by a 
 * Schema resource.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public interface ResponseFormatter {
	/**
	 * Formats a java object to a specific format with amplifying details provided by a 
	 * schema resource.
	 * @param response 
	 * the object model containing the data for the response.
	 * @param schemaUri 
	 * a URI that identifies a resource that the ResponseFormatter
	 * implementation can use verify that the {@link #response} conforms to a pre-determined
	 * set of constraints.
	 * @param templateUri 
	 * a URI that identifies a resource that the ResponseFormatter can use to 
	 * template the response for output.
	 * @return 
	 * a string representation of the formatted response or null if an error occured while formatting.
	 */
	String formatResponse(Object response, URI schemaUri, URI templateUri);
}
