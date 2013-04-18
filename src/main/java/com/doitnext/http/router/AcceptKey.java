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


/**
 * Used to match annotations and reqeusts against a set of routes by request 'Accept' header related Route attributes.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class AcceptKey {
	/**
	 * The type or model of a method response (such as '/mymodels/team/roster')
	 */
	final private String returnType;
	/**
	 * The format of a method response (such as application/json)
	 */
	final private String returnFormat;

	/**
	 * This constuctor is used by {@link DefaultEndpointResolver} in order to 
	 * provide a map key for collections of Response Handlers.  This enables
	 * the endpoint resolver to properly wire an error and success handler to
	 * a route.
	 * 
	 * @param returnType the type (or model) of the response body
	 * @param returnFormat the format of the response body
	 */
	public AcceptKey(String returnType, String returnFormat) {
		this.returnType = returnType;
		this.returnFormat = returnFormat;
	}

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
		String parts[] = acceptHeaderPart.split(";");
		this.returnFormat = parts[0].trim();
		for(int x = 1; x < parts.length; x++) {
			if(parts[x].trim().toLowerCase().startsWith("model=")) {
				String pieces[] = parts[x].split("=",2);
				if(pieces.length > 1){
					this.returnType = pieces[1].trim();
					return;
				}
			}
		}
		this.returnType = "";
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
		if(this.returnFormat.equals("*/*")) {
			return true;
		} else if(this.returnFormat.equals(returnFormat)) {
			if(this.returnType.isEmpty())
				return true;
			else
				return this.returnType.equals(returnType);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((returnFormat == null) ? 0 : returnFormat.hashCode());
		result = prime * result
				+ ((returnType == null) ? 0 : returnType.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AcceptKey other = (AcceptKey) obj;
		if (returnFormat == null) {
			if (other.returnFormat != null)
				return false;
		} else if (!returnFormat.equals(other.returnFormat))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		return true;
	}
	
	
	
}
