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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doitnext.http.router.annotations.PathParameter;
import com.doitnext.http.router.annotations.QueryParameter;
import com.doitnext.http.router.annotations.RequestBody;
import com.doitnext.http.router.annotations.Terminus;
import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.exceptions.DeserializationException;
import com.doitnext.http.router.exceptions.TypeConversionException;
import com.doitnext.http.router.requestdeserializers.DefaultJsonDeserializer;
import com.doitnext.http.router.requestdeserializers.RequestDeserializer;
import com.doitnext.http.router.typeconverters.StringConversionUtil;
import com.doitnext.http.router.typeconverters.TypeConversionUtil;
import com.doitnext.pathutils.PathElement;

/**
 * The default implementation for MethodInvoker.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class DefaultInvoker implements MethodInvoker {
	static Logger logger = LoggerFactory.getLogger(DefaultInvoker.class);
	
	
	/**
	 * Converts path and query parameters into method argument types.
	 */
	TypeConversionUtil<String> stringConverter = new StringConversionUtil();
	
	/**
	 * A list of {@link RequestDeserializer} implementations that the invoker can call upon to
	 * unmarshal a request body into a data type suitable for being passed into a method argument.
	 */
	private List<RequestDeserializer> requestDeserializers = new ArrayList<RequestDeserializer>();
	
	public DefaultInvoker() {
		// Start off with the known serializers
		requestDeserializers.add(new DefaultJsonDeserializer());
	}

	
	/**
	 * @return the {@link #stringConverter}
	 */
	public TypeConversionUtil<String> getStringConverter() {
		return stringConverter;
	}


	/**
	 * @param stringConverter the {@link #stringConverter} to set
	 */
	public void setStringConverter(TypeConversionUtil<String> stringConverter) {
		this.stringConverter = stringConverter;
	}


	/**
	 * @return the {@link #requestDeserializers}
	 */
	public List<RequestDeserializer> getRequestDeserializers() {
		return requestDeserializers;
	}


	/**
	 * @param requestDeserializers the {@link #requestDeserializers} to set
	 */
	public void setRequestDeserializers(
			List<RequestDeserializer> requestDeserializers) {
		this.requestDeserializers = requestDeserializers;
	}


	@Override
	public InvokeResult invokeMethod(HttpMethod method, PathMatch pm,
			HttpServletRequest req, HttpServletResponse resp) 
					throws ServletException {
		String terminus = pm.getMatchedPath().getTerminus();
		
		// Extract variables from the path
		Map<String, PathElement> variableMatches = new HashMap<String,PathElement>();
		for(int x = 0; x < pm.getMatchedPath().size(); x++) {
			PathElement p = pm.getMatchedPath().get(x);
			if(!p.getType().equalsIgnoreCase("LITERAL")) {
				variableMatches.put(p.getName(), p);
			}
		}
		
		Route route = pm.getRoute();
		
		Method implMethod = route.getImplMethod();
		
		Annotation[][] parameterAnnotations = implMethod.getParameterAnnotations();
		Class<?>[] parameterTypes = implMethod.getParameterTypes();
		Object[] arguments = new Object[parameterTypes.length];
		try {	
			// First map annotated parameters, and HttpServletRequest 
			// and HttpServletResponse parameters to arguments.
			mapParametersToArguments(parameterAnnotations, parameterTypes, variableMatches,
					req, resp, terminus, pm, arguments);
			if(logger.isTraceEnabled()) {
				logger.trace(String.format("Invoking %s", route));
			}
			Object invocationResult = implMethod.invoke(route.getImplInstance(), arguments);
			if(logger.isTraceEnabled()) {
				ObjectMapper mapper = new ObjectMapper();
				logger.trace(String.format("Returned %s from %s", mapper.writeValueAsString(invocationResult),
						route));
			}
			if(route.getSuccessHandler().handleResponse(pm, req, resp, invocationResult))
				return InvokeResult.METHOD_SUCCESS;
			else
				return InvokeResult.METHOD_SUCCESS_UNHANDLED;
		} catch(InvocationTargetException ite) {
			Throwable t = ite.getCause();
			if(t == null)
				t = ite;
			if(logger.isDebugEnabled()) {
				logger.debug("Invocation threw exception", ite);
			}
			if(route.getErrorHandler().handleResponse(pm, req, resp, t))
				return InvokeResult.METHOD_ERROR;
			else
				return InvokeResult.METHOD_ERROR_UNHANDLED;
		} catch(Exception e) {
			throw new ServletException(String.format("Error invoking %s", route), e);
		}
	}
	
	private Map<String,String[]> parseQueryArgs(HttpServletRequest req) {
		Map<String,String[]> result = new HashMap<String,String[]>();
		Map<String,String[]> parameterMap = req.getParameterMap();
		String queryString = req.getQueryString();
		
		for(Entry<String,String[]> entry : parameterMap.entrySet()) {
			if(queryString.contains(String.format("?%s=",entry.getKey()))) {
				result.put(entry.getKey(), entry.getValue());
			} else if(queryString.contains(String.format("&%s=",entry.getKey()))) {
				result.put(entry.getKey(), entry.getValue());
			} 
		}
		return result;
	}
	
	
	// Default scope for unit testing
	void mapParametersToArguments(Annotation[][] parameterAnnotations,
			Class<?>[] parameterTypes, Map<String, PathElement> variableMatches,
		    HttpServletRequest req, HttpServletResponse resp, String terminus, 
		    PathMatch pm, Object[] arguments) throws Exception {
		Map<String,String[]> queryArgs = parseQueryArgs(req);
		for(int x = 0; x < parameterTypes.length; x++){
			if(parameterTypes.equals(HttpServletRequest.class)) {
				arguments[x] = req;
				continue;
			}
			if(parameterTypes.equals(HttpServletResponse.class)){
				arguments[x] = resp;
				continue;
			}
			if(parameterAnnotations[x].length > 0) {
				boolean bContinue = false;
				for(int y = 0; y < parameterAnnotations[x].length; y++) {
					Annotation annotation = parameterAnnotations[x][y];
					if(annotation instanceof PathParameter) {
						mapPathParameter((PathParameter)annotation, arguments, 
								parameterTypes[x], variableMatches, x);
						bContinue = true;
						continue;
					} else if(annotation instanceof QueryParameter) {
						mapQueryParameter((QueryParameter)annotation, arguments, 
								parameterTypes[x], queryArgs, x);
						bContinue = true;
						continue;						
					} else if(annotation instanceof Terminus) {
						if(parameterTypes[x].isAssignableFrom(String.class)) {
							arguments[x] = terminus;
						}
					} else if(annotation instanceof RequestBody) {
						RequestDeserializer deserializer = selectDeserializer(pm);
						Object argument = deserializer.deserialize(req.getInputStream(), parameterTypes[x],
								pm.getRoute().getRequestType(), req.getCharacterEncoding());
						arguments[x] = argument;
					}
				}
				if(bContinue)
					continue;
			}
		}
	}
	
	private RequestDeserializer selectDeserializer(PathMatch pm) throws DeserializationException {
		for(RequestDeserializer deserializer : this.requestDeserializers) {
			//TODO: Refine this logic to be more discriminating
			if(pm.getRoute().getRequestFormat().equalsIgnoreCase(deserializer.getRequestFormat()))
				return deserializer;
		}
		throw new DeserializationException(String.format("No deserializer for content type %s", pm.getRoute().getRequestFormat()));
	}
	private void mapQueryParameter(QueryParameter parameterAnnotation, Object[] arguments, 
			Class<?> parameterClass, Map<String,String[]> queryArgs, int paramIndex) throws TypeConversionException {
		String queryArg[] = queryArgs.get(parameterAnnotation.name());
		if(!parameterClass.isArray()) {
			if(queryArg.length > 0) {
				arguments[paramIndex] = stringConverter.convert(queryArg[0], parameterClass);
			}
		} else {
			Object queryParam[] = new Object[queryArg.length];
			for(int x = 0; x < queryArg.length; x++) {
				queryParam[x] = stringConverter.convert(queryArg[x], parameterClass.getComponentType());
			}
			arguments[paramIndex] = queryParam;
		}	
	}

	private void mapPathParameter(PathParameter parameterAnnotation, Object[] arguments, 
			Class<?> parameterClass, Map<String, PathElement> variableMatches, int paramIndex) throws TypeConversionException {
		PathElement pe = variableMatches.get(parameterAnnotation.name());
		arguments[paramIndex] = stringConverter.convert(pe.getValue(), parameterClass);
	}
}
