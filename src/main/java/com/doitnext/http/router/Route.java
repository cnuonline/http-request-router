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

import com.doitnext.http.router.annotations.RestMethod;
import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.responsehandlers.ResponseHandler;
import com.doitnext.pathutils.PathTemplate;

/**
 * <p>Encapsulates an orderable data structure that binds a request category to a
 * specific implementation class that handles the request.</p>
 * 
 * <p>This class is Immutable and thread safe (presuming of course that 
 * java.lang.reflect.Method and java.lang.Class<> are thread safe).</p>
 * 
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
public class Route implements Comparable<Route> {
	/**
	 * The {@link PathTemplate} instance that is used a request URI
	 */
	final private PathTemplate pathTemplate;
	/**
	 * The {@link Class} that implements the request handler for this route.
	 */
	final private Class<?> implClass;
	/**
	 * The {@link Method} in {@link #implClass} that implements the request handler for this route. 
	 */
	final private Method implMethod;
	/**
	 * The {@link HttpMethod} provided by the {@link javax.servlet.http.HttpServletRequest}
	 */
	final private HttpMethod httpMethod;
	/**
	 * A model or schema that refines he description of the request body.  The type of the request
	 * is independent of it's format and can be thought of as being akin to a Java Class but need
	 * not actually be a Java Class.  It is a collection of information conforming to a particular
	 * schema.
	 */
	final private String requestType;
	/**
	 * A model or schema that refines he description of the request body.  The type of the request
	 * is independent of it's format and can be thought of as being akin to a Java Class but need
	 * not actually be a Java Class.  It is a collection of information conforming to a particular
	 * schema.
	 */
	final private String returnType;
	/**
	 * The serialization format of the request body.  This is typically the first segment of the
	 * Content-Type header in an HTTP request.
	 */
	final private String requestFormat;
	/**
	 * The serialization format of the response body.  This is typically the first segment of one of the
	 * elements in an Accept header in an HTTP request.
	 */
	final private String returnFormat;
	/**
	 * The {@link MethodInvoker} implementation that this route uses to invoke the handler method.
	 */
	final private MethodInvoker invoker;
	/**
	 * The actual instance of {@link Route#implClass}
	 */
	final private Object implInstance;
	/**
	 * The {@link ResponseHandler} implementation that will handle the return value of
	 * a call to {@link #implMethod}.  If this is null then {@link #implMethod} should return
	 * void and handle the HttpRequest directly.
	 */
	final private ResponseHandler successHandler;
	/**
	 * The {@link ResponseHandler} implementation that will handle any exception thrown by 
	 * a call to {@link #implMethod}.  If this is null then <code>implMethod</code> should never
	 * throw an exception but should handle the error directly, by setting up the HttpServletResponse.
	 */
	final private ResponseHandler errorHandler;
	
	/**
	 * A boolean value indicating whether this route was created via Dynamic endpoint resolution.  If 
	 * this is a static route then dynamic should be false.
	 */
	final private boolean dynamic;
	
	final private String extendedHttpMethod;
	
	/**
	 * @param httpMethod - The HTTP method associated with the route
	 * @param responseType - the negotiated request type this type is given in the request Content-Type header
	 * @param returnType - the negotiated return type this type is given in the request Accept header
	 * @param responseFormat - used to identify strategy for unmarshalling the request data
	 * @param returnFormat - used to identify the strategy for marshalling the response data 
	 * @param pathTemplate - the template used to match URI's to routes
	 * @param implClass - the class that implements the handler method
	 * @param invoker - the invoker used to invoke methods on the implementation instance
	 * @param implInstance - the implementation instance on which methods are to be invoked.
	 * @param implMethod - the handler method in the implementing class
	 * @param successHandler - handles the method response on normal return.
	 * @param errorHandler - handles the method response on exception.
	 * @param dynamic - true if this is a route created by DynamicEndpointResolver else false.
	 */
	public Route(HttpMethod httpMethod, String requestType, String returnType, 
			String requestFormat, String returnFormat,
			PathTemplate pathTemplate, Class<?> implClass, Method implMethod,
			MethodInvoker invoker, Object implInstance,
			ResponseHandler successHandler, ResponseHandler errorHandler,
			boolean dynamic) {
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
		this.dynamic = dynamic;
		// Ensure that pathTemplate is frozen thus making this class immutable
		if(this.pathTemplate != null)
			this.pathTemplate.freeze();
		
		if(httpMethod == HttpMethod.EXTENDED){
			RestMethod rm = implMethod.getAnnotation(RestMethod.class);
			extendedHttpMethod = rm.extendedHttpMethod();
		} else
			extendedHttpMethod = null;
		
	}

	public boolean isWildcardReturn() {
		return ("*/*".equals(returnFormat) && "*/*".equals(returnType));
	}
	
	public boolean isWildcardConsumer() {
		return ("*/*".equals(requestFormat) && "*/*".equals(requestType));
	}
	
	/**
	 * @return the {@link #httpMethod}
	 * @see #Route
	 */
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	/**
	 * @return the {@link #responseType}
	 * @see #Route
	 */
	public String getRequestType() {
		return requestType;
	}

	/**
	 * @return the {@link #returnType}
	 * @see #Route
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @return the {@link #responseFormat}
	 * @see #Route
	 */
	public String getRequestFormat() {
		return requestFormat;
	}

	/**
	 * @return the {@link #returnFormat}
	 * @see #Route
	 */
	public String getReturnFormat() {
		return returnFormat;
	}

	/**
	 * @return the {@link #pathTemplate}
	 * @see #Route
	 */
	public PathTemplate getPathTemplate() {
		return pathTemplate;
	}

	/**
	 * @return the {@link #implClass}
	 * @see #Route
	 */
	public Class<?> getImplClass() {
		return implClass;
	}

	/**
	 * @return the {@link #implMethod}
	 * @see #Route
	 */
	public Method getImplMethod() {
		return implMethod;
	}

	/**
	 * @return the {@link #invoker}
	 */
	public MethodInvoker getInvoker() {
		return invoker;
	}

	/**
	 * @return the {@link #implInstance}
	 */
	public Object getImplInstance() {
		return implInstance;
	}

	
	/**
	 * @return the {@link #successHandler}
	 */
	public ResponseHandler getSuccessHandler() {
		return successHandler;
	}

	/**
	 * @return the {@link #errorHandler}
	 */
	public ResponseHandler getErrorHandler() {
		return errorHandler;
	}

	/**
	 * @return the {@link #dynamic} property
	 */
	public boolean isDynamic() {
		return dynamic;
	}

	
	/**
	 * @return the extendedHttpMethod
	 */
	public String getExtendedHttpMethod() {
		return extendedHttpMethod;
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(this.httpMethod.name());
		sb.append(": \"");
		sb.append(this.pathTemplate.getLexicalPath());
		sb.append("\", ReturnFormat: \"");
		sb.append(this.returnFormat);
		sb.append("; ReturnType:\"");
		sb.append(this.returnType);
		sb.append("\", RequestFormat: \"");
		sb.append(this.requestFormat);
		sb.append("\", RequestType: \"");
		sb.append(this.requestType);
		sb.append("\"} --> [");
		sb.append(this.implClass.getName());
		sb.append(".");
		sb.append(this.implMethod.getName());
		sb.append("(");
		boolean first = true;
		for(Class<?> classz : this.implMethod.getParameterTypes()) {
			if(!first)
				sb.append(", ");
			sb.append(classz.getSimpleName());
			first = false;
		}
		sb.append(")]");
		return sb.toString();
	}
}
