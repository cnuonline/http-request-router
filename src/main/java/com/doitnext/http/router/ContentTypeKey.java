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
package com.doitnext.http.router;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to index a set of routes by common easily matched Route attributes.
 * <p>The expected use case for this class is to match the content type header 
 * of an incoming request to a Route.</p>
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class ContentTypeKey {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ContentTypeKey.class);
	
	final private String requestType;
	final private String requestFormat;
	
	public ContentTypeKey(String contentTypeHeader) {
		if(contentTypeHeader != null) {
			String parts[] = contentTypeHeader.split(";");
			this.requestFormat = parts[0].trim();
			String requestType = null;
			for(int x = 1; x < parts.length; x++) {
				String part = parts[x].trim();
				if(part.startsWith("model=")) {
					int startIndex = part.indexOf("=");
					requestType = part.substring(startIndex+1);
					break;
				}
			}
			this.requestType = requestType;
		} else {
			requestType = null;
			requestFormat = null;
		}
	}

	/**
	 * @return the requestType
	 */
	public String getRequestType() {
		return requestType;
	}

	
	/**
	 * @return the requestFormat
	 */
	public String getRequestFormat() {
		return requestFormat;
	}

	/**
	 * Matches a given route.
	 * 
	 * @param route the route to match
	 * @return the result of the match <code>true</code> is a match <code>false</code> is not a match.
	 */
	public boolean matches(Route route) {
		// Routes with no request format expect no body
		// Requests with no request format presumably 
		// have no body.
		if(StringUtils.isEmpty(route.getRequestFormat())){
			return StringUtils.isEmpty(requestFormat);
		}
		if(StringUtils.isEmpty(requestFormat)) {
			
			return StringUtils.isEmpty(route.getRequestFormat());
		}

		// --- Examine requests that have an input body ---
		
		// Route matches all input formats
		if(route.getRequestFormat().equals("*/*")) {
			// Check for input model compatibilities

			// Client doesn't specify input model so verify route doesn't care
			// about the input model.
			if(StringUtils.isEmpty(requestType))
				return StringUtils.isEmpty(route.getRequestType());  
			
			// Client and route specify input models, make sure they match
			return requestType.equalsIgnoreCase(route.getRequestType());
		}
		
		// Client and route both specify an input format, make sure they match
		if(requestFormat.matches(route.getRequestFormat())) { // they do
			
			// Client doesn't specify input model so verify route doesn't care
			// about the input model.
			if(StringUtils.isEmpty(requestType))
				return StringUtils.isEmpty(route.getRequestType());  
			
			// Client and route specify input models, make sure they match
			return requestType.equalsIgnoreCase(route.getRequestType());
		}
		return false;
	}
	
}
