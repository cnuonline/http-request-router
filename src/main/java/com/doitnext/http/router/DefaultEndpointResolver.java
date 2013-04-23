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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.responsehandlers.DefaultErrorHandler;
import com.doitnext.http.router.responsehandlers.DefaultSuccessHandler;
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
	private DefaultSuccessHandler defaultSuccessHandler = new DefaultSuccessHandler();
	private PathTemplateParser pathTemplateParser = new PathTemplateParser("/","?");
	private ApplicationContext applicationContext;
	private EndpointDumper endpointDumper;
	
	public DefaultEndpointResolver() {
		MethodReturnKey defaultKey = new MethodReturnKey("", "application/json");
		errorHandlers = new HashMap<MethodReturnKey,ResponseHandler>();
		successHandlers = new HashMap<MethodReturnKey, ResponseHandler>();
		errorHandlers.put(defaultKey, defaultErrorHandler);
		successHandlers.put(defaultKey, defaultSuccessHandler);
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
	
	public void setEndpointDumper(EndpointDumper endpointDumper){
		this.endpointDumper = endpointDumper;
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
		if(this.applicationContext == null)
			throw new IllegalStateException("No applicationContext set.");
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
			Object implInstance = applicationContext.getBean(resource.value(), classz);
			RequestResponseContext rrCtx = 
					new RequestResponseContext(new InheritableValue(resource.requestType(), methodImpl.requestType()),
							new InheritableValue(resource.returnType(), methodImpl.returnType()),
							new InheritableValue(resource.requestFormat(), methodImpl.requestFormat()),
							new InheritableValue(resource.returnFormat(), methodImpl.returnFormat()));
			
			addMethodToRoutes(pathBuilder.toString(), implInstance, rrCtx, method, classz, methodImpl.method(), routes);
		}
		
		// Add dump routes to the set
		if(endpointDumper != null) {
			for(String returnFormat : endpointDumper.getReturnFormats()) {
				pathBuilder = new StringBuilder("endpoints_");
				Object implInstance = this;
				RequestResponseContext rrCtx = 
						new RequestResponseContext(new InheritableValue("", ""),
								new InheritableValue("", ""),
								new InheritableValue("", ""),
								new InheritableValue(returnFormat, returnFormat));
				try {
					Method method = endpointDumper.getClass().getMethod("dumpEndpoints", HttpServletRequest.class, HttpServletResponse.class);
					addMethodToRoutes(pathBuilder.toString(), implInstance, rrCtx, method, 
							endpointDumper.getClass(), HttpMethod.GET, routes);		
				} catch (SecurityException e) {
					logger.error("Unable to add endpoint dump", e);
				} catch (NoSuchMethodException e) {
					logger.error("Unable to add endpoint dump", e);
				}
			}
		}
	}

	private void addMethodToRoutes(String path, Object implInstance, RequestResponseContext rrCtx,
			Method implMethod, Class<?> implClass, HttpMethod httpMethod,
			TreeSet<Route> routes) {
		try {
			PathTemplate pathTemplate = pathTemplateParser.parse(path);
			MethodReturnKey acceptKey = new MethodReturnKey(rrCtx.responseType.resolve(),
					rrCtx.responseFormat.resolve());

			if (!successHandlers.containsKey(acceptKey)) {
				logger.error(String
						.format("No response handler for method with %s",
								acceptKey));
				if(logger.isDebugEnabled())
					logger.debug(String.format("successHandlers = %s", successHandlers));		
				return;
			}
			// If no error handler in errorHandlers use a 
			// default handler so we can handle errors.
			ResponseHandler errorHandler = defaultErrorHandler;
			if (errorHandlers.containsKey(acceptKey)) {
				errorHandler = errorHandlers.get(acceptKey);
			}
			ResponseHandler successHandler = successHandlers.get(acceptKey);
			Route route = new Route(httpMethod,
					rrCtx.requestType.resolve(), rrCtx.responseType.resolve(),
					rrCtx.requestFormat.resolve(), rrCtx.responseFormat.resolve(),
					pathTemplate, implClass, implMethod, invoker, implInstance,
					successHandler, errorHandler);
			if (routes.contains(route)) {
				Route existingRoute = null;
				for(Route r: routes) {
					if(r.compareTo(route) == 0){
						existingRoute = r;
						break;
					}
				}
				logger.debug(String
						.format("An equivalent route to %s is already in routes. Conflicting route: %s",
								route, existingRoute));
			} else {
				logger.debug(String.format("Adding route %s to routes.",
						route));
				routes.add(route);
			}
		} catch (Exception e) {
			logger.error(
					String.format("Error addding route for %s.%s",
							implClass.getName(), implMethod.getName()), e);
		}
	}
	
	private static class InheritableValue {
		final String defaultValue;
		final String value;
		InheritableValue(String defaultValue, String value){
			this.defaultValue = defaultValue;
			this.value = value;
			
		}
		 String resolve() {
			if (StringUtils.isEmpty(value))
				return defaultValue;
			else
				return value;
		}	
	}
	private static class RequestResponseContext {
		final InheritableValue requestType;
		final InheritableValue responseType;
		final InheritableValue requestFormat;
		final InheritableValue responseFormat;
		
		public RequestResponseContext(InheritableValue requestType,
				InheritableValue responseType, InheritableValue requestFormat,
				InheritableValue responseFormat){
			this.requestFormat = requestFormat;
			this.requestType = requestType;
			this.responseFormat = responseFormat;
			this.responseType = responseType;
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
