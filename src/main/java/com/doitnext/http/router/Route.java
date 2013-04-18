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
import com.doitnext.http.router.responsehandlers.ResponseHandler;
import com.doitnext.pathutils.PathTemplate;

/**
 * Encapsulates an orderable data structure that binds a request category to a
 * specific implementation class that handles the request.
 * 
 * <p>This class is Immutable and thread safe (presuming of course that 
 * java.lang.reflect.Method and java.lang.Class<> are thread safe).</p>
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
	final private MethodInvoker invoker;
	final private Object implInstance;
	final private ResponseHandler successHandler;
	final private ResponseHandler errorHandler;
	
	
	/**
	 * @param httpMethod - The HTTP method associated with the route
	 * @param requestType - the negotiated request type this type is given in the request Content-Type header
	 * @param returnType - the negotiated return type this type is given in the request Accept header
	 * @param requestFormat - used to identify strategy for unmarshalling the request data
	 * @param returnFormat - used to identify the strategy for marshalling the response data 
	 * @param pathTemplate - the template used to match URI's to routes
	 * @param implClass - the class that implements the handler method
	 * @param invoker - the invoker used to invoke methods on the implementation instance
	 * @param implInstance - the implementation instance on which methods are to be invoked.
	 * @param implMethod - the handler method in the implementing class
	 * @param successHandler - handles the method response on normal return.
	 * @param errorHandler - handles the method response on exception.
	 */
	public Route(HttpMethod httpMethod, String requestType, String returnType, 
			String requestFormat, String returnFormat,
			PathTemplate pathTemplate, Class<?> implClass, Method implMethod,
			MethodInvoker invoker, Object implInstance,
			ResponseHandler successHandler, ResponseHandler errorHandler) {
		this.pathTemplate = pathTemplate;
		this.implClass = implClass;
		this.implMethod = implMethod;
		this.httpMethod = httpMethod;
		this.requestType = requestType;
		this.returnType = returnType;
		this.requestFormat = requestFormat;
		this.returnFormat = returnFormat;
		this.invoker = invoker;
		this.implInstance = implInstance;
		this.successHandler = successHandler;
		this.errorHandler = errorHandler;
		// Ensure that pathTemplate is frozen thus making this class immutable
		if(this.pathTemplate != null)
			this.pathTemplate.freeze();
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

	/**
	 * @return the invoker
	 */
	public MethodInvoker getInvoker() {
		return invoker;
	}

	/**
	 * @return the implInstance
	 */
	public Object getImplInstance() {
		return implInstance;
	}

	
	/**
	 * @return the successHandler
	 */
	public ResponseHandler getSuccessHandler() {
		return successHandler;
	}

	/**
	 * @return the errorHandler
	 */
	public ResponseHandler getErrorHandler() {
		return errorHandler;
	}

	private int compareNullableStrings(String thisVal, String thatVal) {
		// Comparison rule: Nulls come last
		if(thisVal == null)
				return (thatVal == null) ? 0 : 1;
		if(thatVal == null)
			return -1;
		return thisVal.compareToIgnoreCase(thatVal);
	}
	
	private <T extends Comparable<T>> int compareNullableValues(T thisVal, T thatVal) {
		// Comparison rule: Nulls come last
		if(thisVal == null)
			return (thatVal == null) ? 0 : 1;
		if(thatVal == null)
			return -1;
		return thisVal.compareTo(thatVal);
	}

	
	@Override
	public int compareTo(Route that) {
		int compareResult = compareNullableValues(this.pathTemplate,that.pathTemplate);
		if(compareResult != 0)
			return compareResult;
		compareResult = compareNullableValues(this.httpMethod,that.httpMethod);
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
