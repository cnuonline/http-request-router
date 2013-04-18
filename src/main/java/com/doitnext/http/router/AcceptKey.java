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


/**
 * Used to match reqeusts against a set of routes by request 'Accept' header related Route attributes.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public final class AcceptKey {
	/**
	 * The type or model of a method response (such as '/mymodels/team/roster')
	 */
	final private String returnType;
	/**
	 * The format of a method response (such as application/json)
	 */
	final private String returnFormat;


	/**
	 * This constructor is used primarily by {@link RequestRouterServlet} to match incoming request
	 * accept header values to routes.
	 * @param acceptHeaderPart a single part of an accept header.
	 * For example if the request Accept header was
	 * 'application/json; model=/mymodels/team/roster, application/xml; model=/mymodels/team/roster'
	 * Then the header would be broken into two parts
	 * <ol><li>application/json; model=/mymodels/team/roster</li>
	 * <li>application/xml; model=/mymodels/team/roster</li></ol>
	 */
	public AcceptKey(String acceptHeaderPart) {
		if(acceptHeaderPart != null) {
			String parts[] = acceptHeaderPart.split(";");
			this.returnFormat = parts[0].trim();
			String returnType = null;
			for(int x = 1; x < parts.length; x++) {
				String part = parts[x].trim();
				if(part.startsWith("model=")) {
					int startIndex = part.indexOf("=");
					returnType = part.substring(startIndex+1);
					break;
				}
			}
			this.returnType = returnType;
		} else {
			this.returnType = null;
			this.returnFormat = null;
		}
	}

	/**
	 * @return the {@link #returnType}
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @return the {@link #returnFormat}
	 */
	public String getReturnFormat() {
		return returnFormat;
	}

	public boolean matches(Route route) {
		return matches(route.getReturnFormat(), route.getReturnType());
	}
	
	public boolean matches(String returnFormat, String returnType) {
		// If no accept header then no return is expected
		if(StringUtils.isEmpty(this.returnFormat))
			return (StringUtils.isEmpty(returnFormat)&&StringUtils.isEmpty(returnType));
		
		//If request accepts any format check for model match
		if(this.returnFormat.equals("*/*")) {
			if(StringUtils.isEmpty(this.returnType))
				return true; // Request accepts any model
			else // Request wants a particular model
				return this.returnType.equalsIgnoreCase(returnType);
		}
		
		// If formats match check for model match
		if(this.returnFormat.equalsIgnoreCase(returnFormat)) {
			if(StringUtils.isEmpty(this.returnType))
				return true; // Any model will do
			else
				return this.returnType.equalsIgnoreCase(returnType);
		}
		
		// No match
		return false;
	}
}
