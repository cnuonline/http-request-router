package com.doitnext.http.router.responseformatter;

import java.net.URI;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.schema.JsonSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to format an object into JSON using a schemaUri.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class JacksonResponseFormatter implements ResponseFormatter {
	Logger logger = LoggerFactory.getLogger(JacksonResponseFormatter.class);
	
	private ObjectMapper objectMapper = new ObjectMapper();
	 
	public JacksonResponseFormatter() {
		objectMapper = new ObjectMapper();
	}

	@Override
	public String formatResponse(Object response, URI schemaUri, URI templateUri) {
		try {
			String json = objectMapper.writeValueAsString(response);
			JsonSchema schema = new JsonSchema(null);
		} catch(Exception e) {
			logger.error(String.format("Exception caught formatting response for %s using schema %s and tempmlate %s",
					response.getClass().getName(), schemaUri, templateUri));
		}
		return null;
	}

}
