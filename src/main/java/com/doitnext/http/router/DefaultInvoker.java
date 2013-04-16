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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doitnext.http.router.annotations.PathParameter;
import com.doitnext.http.router.annotations.QueryParameter;
import com.doitnext.http.router.annotations.Terminus;
import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.pathutils.PathElement;

/**
 * The default implementation for MethodInvoker.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class DefaultInvoker implements MethodInvoker {

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
		
		// First map annotated parameters, and HttpServletRequest 
		// and HttpServletResponse parameters to arguments.
		mapParametersToArguments(parameterAnnotations, parameterTypes, variableMatches,
				req, resp, terminus, arguments);
		
		try {
			Object invocationResult = implMethod.invoke(implClass, arguments);
		} catch(Exception e) {
			throw new ServletException(String.format("Error invoking method '%s' in class '%s'.", 
					implMethod.getName(), implClass.getName()));
		}
		return false;
	}
	
	// Default scope for unit testing
	void mapParametersToArguments(Annotation[][] parameterAnnotations,
			Class<?>[] parameterTypes, Map<String, PathElement> variableMatches,
		    HttpServletRequest req, HttpServletResponse resp, String terminus, 
		    Object[] arguments) {
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
								parameterTypes[x], variableMatches);
						bContinue = true;
						continue;
					} else if(annotation instanceof QueryParameter) {
						mapQueryParameter((QueryParameter)annotation, arguments, 
								parameterTypes[x], variableMatches);
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
			Class<?> parameterClass, Map<String, PathElement> variableMatches) {
		
	}

	private void mapPathParameter(PathParameter parameterAnnotation, Object[] arguments, 
			Class<?> parameterClass, Map<String, PathElement> variableMatches) {
		
	}
}
