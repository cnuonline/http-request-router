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
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doitnext.http.router.annotations.PathParameter;
import com.doitnext.http.router.annotations.QueryParameter;
import com.doitnext.http.router.annotations.Terminus;
import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.typeconverters.StringConversionUtil;
import com.doitnext.pathutils.PathElement;

/**
 * The default implementation for MethodInvoker.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class DefaultInvoker implements MethodInvoker {
	StringConversionUtil stringConverter = new StringConversionUtil();
	public DefaultInvoker() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public boolean invokeMethod(HttpMethod method, PathMatch pm,
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
		Class<?> implClass = route.getImplClass();
		Method implMethod = route.getImplMethod();
		
		Annotation[][] parameterAnnotations = implMethod.getParameterAnnotations();
		Class<?>[] parameterTypes = implMethod.getParameterTypes();
		Object[] arguments = new Object[parameterTypes.length];
		try {	
			// First map annotated parameters, and HttpServletRequest 
			// and HttpServletResponse parameters to arguments.
			mapParametersToArguments(parameterAnnotations, parameterTypes, variableMatches,
					req, resp, terminus, arguments);
		
			Object invocationResult = implMethod.invoke(route.getImplInstance(), arguments);
			return route.getSuccessHandler().handleResponse(pm, req, resp, invocationResult);
		} catch(InvocationTargetException ite) {
			Throwable t = ite.getCause();
			if(t == null)
				t = ite;
			return route.getErrorHandler().handleResponse(pm, req, resp, t);
		} catch(Exception e) {
			throw new ServletException(String.format("Error invoking method '%s' in class '%s'.", 
					implMethod.getName(), implClass.getName()), e);
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
		    Object[] arguments) throws Exception {
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
				for(int y = 0; y < parameterAnnotations.length; y++) {
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
					}
				}
				if(bContinue)
					continue;
			}
		}
	}
	
	private void mapQueryParameter(QueryParameter parameterAnnotation, Object[] arguments, 
			Class<?> parameterClass, Map<String,String[]> queryArgs, int paramIndex) throws ParseException {
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
			Class<?> parameterClass, Map<String, PathElement> variableMatches, int paramIndex) throws ParseException {
		PathElement pe = variableMatches.get(parameterAnnotation.name());
		arguments[paramIndex] = stringConverter.convert(pe.getValue(), parameterClass);
	}
}
