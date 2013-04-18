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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.doitnext.http.router.annotations.RestMethod;
import com.doitnext.http.router.annotations.RestResource;
import com.doitnext.http.router.responsehandlers.DefaultErrorHandler;
import com.doitnext.http.router.responsehandlers.ResponseHandler;
import com.doitnext.pathutils.PathTemplate;
import com.doitnext.pathutils.PathTemplateParser;
import com.google.common.collect.ImmutableSortedSet;

/**
 * <p>Uses Reflections to locate @RestResource annotated classes to build routes
 * from to @RestMethod definitions. These {@link Route} objects are returned as an
 * Ordered Immutable set which can be used to map an HTTP request to a request 
 * handler implementation
 * on a first match first serve basis.</p>
 * 
 * <p>The ordering ensures longest {@link PathTemplate} routes are tried first.</p>
 * 
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
@Component("defaultEndpointResolver")
public class DefaultEndpointResolver implements EndpointResolver, ApplicationContextAware {

	private static Logger logger = LoggerFactory
			.getLogger(DefaultEndpointResolver.class);
	private MethodInvoker invoker;
	private Map<MethodReturnKey, ResponseHandler> successHandlers;
	private Map<MethodReturnKey, ResponseHandler> errorHandlers;
	private DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler();
	private PathTemplateParser pathTemplateParser = new PathTemplateParser("/","?");
	private ApplicationContext applicationContext;
	
	public DefaultEndpointResolver() {
	}

	@Required
	public void setMethodInvoker(MethodInvoker invoker) {
		this.invoker = invoker;
	}

	@Required
	public void setSuccessHandlers(
			Map<MethodReturnKey, ResponseHandler> successHandlers) {
		this.successHandlers = successHandlers;
	}

	@Required
	public void setErrorHandlers(Map<MethodReturnKey, ResponseHandler> errorHandlers) {
		this.errorHandlers = errorHandlers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.doitnext.http.router.EndpointResolver#resolveEndpoints(java.lang.
	 * String, java.lang.String)
	 */
	public ImmutableSortedSet<Route> resolveEndpoints(String pathPrefix,
			String basePackage) {
		TreeSet<Route> result = new TreeSet<Route>();
		logger.debug(String.format("Scanning %s for @EndpointImpl classes.",
				basePackage));
		Reflections reflections = new Reflections(basePackage);
		Set<Class<?>> endpointSets = reflections
				.getTypesAnnotatedWith(RestResource.class);
		for (Class<?> classz : endpointSets) {
			addEndpointsToResult(pathPrefix, classz, result);
		}
		return ImmutableSortedSet.copyOf(result);
	}

	private void addEndpointsToResult(String pathPrefix, Class<?> classz,
			TreeSet<Route> routes) {
		RestResource resource = classz.getAnnotation(RestResource.class);
		StringBuilder pathBuilder = new StringBuilder(pathPrefix);
		pathBuilder.append(resource.pathprefix());

		String resourcePathPrefix = pathBuilder.toString();
		@SuppressWarnings("unchecked")
		Set<Method> methods = ReflectionUtils.getAllMethods(classz,
				ReflectionUtils.withAnnotation(RestMethod.class));

		if(logger.isDebugEnabled())
			logger.debug(String.format("Creating routes for %d methods in %s", methods.size(), classz.getName()));
		for (Method method : methods) {
			if(logger.isDebugEnabled())
				logger.debug(String.format("Attempting to create route for method %s", method.getName()));
			RestMethod methodImpl = method.getAnnotation(RestMethod.class);
			pathBuilder = new StringBuilder(resourcePathPrefix);
			pathBuilder.append(methodImpl.template());
			try {
				PathTemplate pathTemplate = pathTemplateParser.parse(pathBuilder.toString());
				
				Object implInstance = applicationContext.getBean(resource.value(), classz);
				MethodReturnKey acceptKey = new MethodReturnKey(methodImpl.returnType(),
						methodImpl.returnFormat());
				if (!successHandlers.containsKey(acceptKey)) {
					logger.error(String
							.format("No response handler for method with %s",
									acceptKey));
					if(logger.isDebugEnabled())
						logger.debug(String.format("successHandlers = %s", successHandlers));		
					continue;
				}
				ResponseHandler errorHandler = defaultErrorHandler;
				if (errorHandlers.containsKey(acceptKey)) {
					errorHandler = errorHandlers.get(acceptKey);
				}
				ResponseHandler successHandler = successHandlers.get(acceptKey);
				Route route = new Route(methodImpl.method(),
						resolveRequestType(resource, methodImpl),
						resolveReturnType(resource, methodImpl),
						resolveRequestFormat(resource, methodImpl),
						resolveReturnFormat(resource, methodImpl),
						pathTemplate, classz, method, invoker, implInstance,
						successHandler, errorHandler);
				if (routes.contains(route)) {
					logger.debug(String
							.format("An equivalent route to %s is already in routes. Ignoring.",
									route.toString()));
				} else {
					logger.debug(String.format("Adding route %s to routes.",
							route.toString()));
					routes.add(route);
				}
			} catch (Exception e) {
				logger.error(
						String.format("Error addding route for %s.%s",
								classz.getName(), method.getName()), e);
			}
		}
	}

	private String resolveReturnFormat(RestResource res, RestMethod mthd) {
		if (mthd.requestFormat().trim().isEmpty())
			return res.returnFormat();
		else
			return mthd.returnFormat();
	}

	private String resolveRequestFormat(RestResource res, RestMethod mthd) {
		if (mthd.requestFormat().trim().isEmpty())
			return res.requestFormat();
		else
			return mthd.requestFormat();
	}

	private String resolveReturnType(RestResource res, RestMethod mthd) {
		if (mthd.returnType().trim().isEmpty())
			return res.returnType();
		else
			return mthd.returnType();
	}

	private String resolveRequestType(RestResource res, RestMethod mthd) {
		if (mthd.requestType().trim().isEmpty())
			return res.requestType();
		else
			return mthd.requestType();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
