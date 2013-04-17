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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.HttpRequestHandler;

import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.exceptions.Http404Exception;
import com.doitnext.http.router.exceptions.Http405Exception;
import com.doitnext.http.router.exceptions.Http406Exception;
import com.doitnext.http.router.exceptions.Http415Exception;
import com.doitnext.http.router.exceptions.Http500Exception;
import com.doitnext.http.router.responsehandlers.DefaultErrorHandler;
import com.doitnext.http.router.responsehandlers.ResponseHandler;
import com.doitnext.pathutils.Path;
import com.google.common.collect.ImmutableSortedSet;

public class RestRouterServlet implements HttpRequestHandler, InitializingBean {

	/**
	 * Serial version id for this servlet
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 149227713315378579L;
	
	private static Logger logger = LoggerFactory
			.getLogger(RestRouterServlet.class);
	
	
	private volatile ImmutableSortedSet<Route> routes;
	

	// Spring Injected
	private String restPackageRoot;
	private String pathPrefix = "";
	private DynamicEndpointResolver dynamicEndpointResolver = null;
	private MethodInvoker methodInvoker = new DefaultInvoker();
	private EndpointResolver endpointResolver = new DefaultEndpointResolver();
	private ResponseHandler errorHandler = new DefaultErrorHandler();
	
	public RestRouterServlet() {
	}

	@Required
	public void setRestPackageRoot(String value) {
		this.restPackageRoot = value;
	}
	
	public void setPathPrefix(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}
	
	public void setMethodInvoker(MethodInvoker methodInvoker) {
		this.methodInvoker = methodInvoker;
	}
	 
	public void setEndpointResolver(EndpointResolver endpointResolver) {
		this.endpointResolver = endpointResolver;
	}
	
	public void setDynamicEndpointResolver(DynamicEndpointResolver value) {
		this.dynamicEndpointResolver = value;
	}
	
	public void setErrorHandler(ResponseHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		routes = endpointResolver.resolveEndpoints(pathPrefix, restPackageRoot);
		if (routes.isEmpty()) {
			logger.warn("On init no routes established. This servlet will not respond to any requests while in this state.");
		} else {
			logger.debug(String.format("Identified %d routes.", routes.size()));
		}
	}

	/**
	 * This method is called in order to initiate a callback to the
	 * {@link #dynamicEndpointResolver} implementation of
	 * {@link DynamicEndpointResolver#updateRoutes(RestRouterServlet, TreeSet) 
	 */
	public void fireUpdateRoutes() {
		if (dynamicEndpointResolver != null) {
			dynamicEndpointResolver.updateRoutes(this, routes);
		} else
			throw new IllegalStateException("No dynamicEndpointResolver set.");
	}

	public void routesUpdated(ImmutableSortedSet<Route> newRoutes) {
		this.routes = newRoutes;
	}

	protected boolean do404(HttpMethod method, HttpServletRequest req,
			HttpServletResponse resp) {
		resp.setStatus(404);
		return errorHandler.handleResponse(null, req, resp,
				new Http404Exception());
	}

	protected boolean do406(HttpMethod method, HttpServletRequest req,
			HttpServletResponse resp) {
		resp.setStatus(406);
		return errorHandler.handleResponse(null, req, resp,
				new Http406Exception());
	}

	protected boolean do415(HttpMethod method, HttpServletRequest req,
			HttpServletResponse resp) {
		resp.setStatus(415);
		String contentType = req.getHeader("Content-Type");
		return errorHandler.handleResponse(null, req, resp,
				new Http415Exception(contentType));
	}

	protected boolean do405(HttpMethod method, List<String> allowedMethods,
			HttpServletRequest req, HttpServletResponse resp) {
		resp.setStatus(405);
		return errorHandler.handleResponse(null, req, resp,
				new Http405Exception(method, allowedMethods));
	}

	protected boolean do500(HttpMethod method, HttpServletRequest req,
			HttpServletResponse resp, Throwable error) {
		resp.setStatus(500);
		return errorHandler.handleResponse(null, req, resp,
				new Http500Exception(error));
	}

	protected boolean routeRequest(HttpMethod method, HttpServletRequest req,
			HttpServletResponse resp) {
		SortedSet<Route> routes = this.routes;
		List<PathMatch> pathMatches = new ArrayList<PathMatch>();
		String pathString = req.getPathInfo();
		for (Route route : routes) {
			Path path = route.getPathTemplate().match(pathString);
			if (path != null) {
				pathMatches.add(new PathMatch(route, path));
			}
		}
		if (pathMatches.isEmpty())
			return do404(method, req, resp); // Resource not found

		String acceptTypes = req.getHeader("Accept");
		String accepts[] = acceptTypes.split(",");
		List<AcceptKey> acceptKeys = new ArrayList<AcceptKey>();
		for (String accept : accepts) {
			acceptKeys.add(new AcceptKey(accept.trim()));
		}
		List<PathMatch> pathMatchesByResponseType = new ArrayList<PathMatch>();
		for (PathMatch pm : pathMatches) {
			for (AcceptKey acceptKey : acceptKeys) {
				if (acceptKey.matches(pm.getRoute()))
					pathMatchesByResponseType.add(pm);
			}
		}
		if (pathMatchesByResponseType.isEmpty())
			return do406(method, req, resp);

		List<PathMatch> pathMatchesByContentType = new ArrayList<PathMatch>();
		String contentTypeHeader = req.getHeader("Content-Type");
		ContentTypeKey contentTypeKey = new ContentTypeKey(contentTypeHeader);
		for (PathMatch pm : pathMatchesByResponseType) {
			Route route = pm.getRoute();
			if (contentTypeKey.matches(route))
				pathMatchesByContentType.add(pm);
		}
		if (pathMatchesByContentType.isEmpty())
			return do415(method, req, resp);

		Set<HttpMethod> allowedMethods = new TreeSet<HttpMethod>();
		for (PathMatch pm : pathMatchesByContentType) {
			if (pm.getRoute().getHttpMethod().equals(method))
				try {
					return methodInvoker.invokeMethod(method, pm, req, resp);
				} catch (Throwable t) {
					return do500(method, req, resp, t);
				}
			else
				allowedMethods.add(pm.getRoute().getHttpMethod());
		}
		List<String> am = new ArrayList<String>();
		for (HttpMethod m : allowedMethods)
			am.add(m.name());
		return do405(method, am, req, resp);
	}

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod().toUpperCase());
		if(!routeRequest(httpMethod, request, response))
			logger.error(String.format("Failed to handle %s request.", httpMethod.name()));
	}
}
