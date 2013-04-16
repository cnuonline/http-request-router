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

import java.lang.reflect.Method;

import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.pathutils.PathTemplate;

/**
 * Encapsulates an orderable data structure that binds a request category to a
 * specific implementation class that handles the request.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class Route implements Comparable<Route> {
	final private PathTemplate pathTemplate;
	final private Class<?> implClass;
	final private Method implMethod;
	final private HttpMethod httpMethod;
	final private String requestType;
	final private String returnType;
	final private String requestFormat;
	final private String returnFormat;
	
	
	/**
	 * @param httpMethod - The HTTP method associated with the route
	 * @param requestType - the negotiated request type this type is given in the request Content-Type header
	 * @param returnType - the negotiated return type this type is given in the request Accept header
	 * @param requestFormat - used to identify strategy for unmarshalling the request data
	 * @param returnFormat - used to identify the strategy for marshalling the response data 
	 * @param pathTemplate - the template used to match URI's to routes
	 * @param implClass - the class that implements the handler method
	 * @param implMethod - the handler method in the implementing class
	 */
	public Route(HttpMethod httpMethod, String requestType, String returnType, 
			String requestFormat, String returnFormat,
			PathTemplate pathTemplate, Class<?> implClass, Method implMethod) {
		this.pathTemplate = pathTemplate;
		this.implClass = implClass;
		this.implMethod = implMethod;
		this.httpMethod = httpMethod;
		this.requestType = requestType;
		this.returnType = returnType;
		this.requestFormat = requestFormat;
		this.returnFormat = returnFormat;
	}

	/**
	 * @return the httpMethod
	 * @see #Route
	 */
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	/**
	 * @return the requestType
	 * @see #Route
	 */
	public String getRequestType() {
		return requestType;
	}

	/**
	 * @return the returnType
	 * @see #Route
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @return the requestFormat
	 * @see #Route
	 */
	public String getRequestFormat() {
		return requestFormat;
	}

	/**
	 * @return the returnFormat
	 * @see #Route
	 */
	public String getReturnFormat() {
		return returnFormat;
	}

	/**
	 * @return the pathTemplate
	 * @see #Route
	 */
	public PathTemplate getPathTemplate() {
		return pathTemplate;
	}

	/**
	 * @return the implClass
	 * @see #Route
	 */
	public Class<?> getImplClass() {
		return implClass;
	}

	/**
	 * @return the implMethod
	 * @see #Route
	 */
	public Method getImplMethod() {
		return implMethod;
	}

	private int compareNullableStrings(String thisVal, String thatVal) {
		if(thisVal == null) {
			if(thatVal != null)
				return -1;
		}
		if(thatVal == null)
			return 1;
		return thisVal.compareToIgnoreCase(thatVal);
	}
	
	@Override
	public int compareTo(Route that) {
		int compareResult = this.pathTemplate.compareTo(that.pathTemplate);
		if(compareResult != 0)
			return compareResult;
		compareResult = this.httpMethod.compareTo(that.httpMethod);
		if(compareResult != 0)
			return compareResult;
		compareResult = compareNullableStrings(this.returnType,that.returnType);
		if(compareResult != 0)
			return compareResult;
		compareResult = compareNullableStrings(this.requestType,that.requestType);
		if(compareResult != 0)
			return compareResult;
		compareResult = compareNullableStrings(this.returnFormat,that.returnFormat);
		if(compareResult != 0)
			return compareResult;
		compareResult = compareNullableStrings(this.requestFormat,that.requestFormat);
		if(compareResult != 0)
			return compareResult;
		
		return 0;
	}

	
	
}
