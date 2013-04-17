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
 * Used to index a set of routes by common easily matched Route attributes.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class AcceptKey {
	final private String returnType;
	final private String returnFormat;

	public AcceptKey(String returnType, String returnFormat) {
		this.returnType = returnType;
		this.returnFormat = returnFormat;
	}
	
	public AcceptKey(Route route) {
		this.returnType = route.getReturnType();
		this.returnFormat = route.getReturnFormat();
	}
	
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
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @return the returnFormat
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
