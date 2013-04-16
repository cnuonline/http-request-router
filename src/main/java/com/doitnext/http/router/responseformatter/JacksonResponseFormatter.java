/**
 * Copyright (C) 2013 Steve Owens (DoItNext.com) http://www.doitnext.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
