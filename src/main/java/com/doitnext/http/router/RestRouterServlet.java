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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
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

public class RestRouterServlet implements HttpRequestHandler, InitializingBean, EndpointDumper {

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
	private ObjectMapper objectMapper = new ObjectMapper();
	
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
		if(logger.isTraceEnabled()){
			logger.trace(String.format("%s %s returns 404", method.name(), req.getRequestURI()));
		}
		resp.setStatus(404);
		return errorHandler.handleResponse(null, req, resp,
				new Http404Exception(req.getRequestURI()));
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
			if(logger.isTraceEnabled()){
				logger.trace(String.format("Trying to match path '%s' against '%s'",
						pathString, route.toString() ));
			}
			Path path = route.getPathTemplate().match(pathString);
			if (path != null) {
				if(logger.isTraceEnabled()) {
					logger.trace(String.format("Matched path '%s' against '%s'",
							pathString, route ));
				}
				pathMatches.add(new PathMatch(route, path));
			}
		}
		if (pathMatches.isEmpty())
			return do404(method, req, resp); // Resource not found
		else if(logger.isTraceEnabled())
			logger.trace(String.format("There are %d routes that match by uri path.", pathMatches.size()));
		
		
		List<PathMatch> pathMatchesByPathAndMethod = new ArrayList<PathMatch>();
		Set<HttpMethod> allowedMethods = new TreeSet<HttpMethod>();
		for (PathMatch pm : pathMatches) {
			if (pm.getRoute().getHttpMethod().equals(method)){
				pathMatchesByPathAndMethod.add(pm);
				if(logger.isTraceEnabled()) {
					logger.trace(String.format("Http request method: %s matches route %s", method.name(), pm.getRoute()));
				}
			} else {
				allowedMethods.add(pm.getRoute().getHttpMethod());
				if(logger.isTraceEnabled()) {
					logger.trace(String.format("Http request method: %s does not match route %s.  This route will be excluded from further consideration.", method.name(), pm.getRoute()));
				}				
			}
		}
		
		if(pathMatchesByPathAndMethod.isEmpty()) {
			List<String> am = new ArrayList<String>();
			for (HttpMethod m : allowedMethods)
				am.add(m.name());
			return do405(method, am, req, resp);
		}
			
		String acceptTypes = req.getHeader("Accept");
		String accepts[] = acceptTypes.split(",");
		List<AcceptKey> acceptKeys = new ArrayList<AcceptKey>();
		for (String accept : accepts) {
			acceptKeys.add(new AcceptKey(accept.trim()));
		}
		List<PathMatch> pathMatchesByResponseType = new ArrayList<PathMatch>();
		for (PathMatch pm : pathMatchesByPathAndMethod) {
			for (AcceptKey acceptKey : acceptKeys) {
				if (acceptKey.matches(pm.getRoute())) {
					pathMatchesByResponseType.add(pm);
					if(logger.isTraceEnabled()) {
						logger.trace(String.format("Accept key: %s matches route %s", acceptKey, pm.getRoute()));
					}
				} else if(logger.isTraceEnabled()) {
					logger.trace(String.format("Accept key: %s does not match route %s.  This route will be excluded from further consideration.", acceptKey, pm.getRoute()));
				}
			}
		}
		if (pathMatchesByResponseType.isEmpty())
			return do406(method, req, resp);
		else if(logger.isTraceEnabled())
			logger.trace(String.format("There are %d routes that match by response type.", pathMatchesByResponseType.size()));

		List<PathMatch> pathMatchesByContentType = new ArrayList<PathMatch>();
		String contentTypeHeader = req.getHeader("Content-Type");
		ContentTypeKey contentTypeKey = new ContentTypeKey(contentTypeHeader);
		for (PathMatch pm : pathMatchesByResponseType) {
			Route route = pm.getRoute();
			if (contentTypeKey.matches(route)){
				pathMatchesByContentType.add(pm);
				if(logger.isTraceEnabled()) {
					logger.trace(String.format("Content type key: %s matches route %s", contentTypeKey, pm.getRoute()));
				} else if(logger.isTraceEnabled()) {
					logger.trace(String.format("Content type key: %s does not match route %s.  This route will be excluded from further consideration.", contentTypeKey, pm.getRoute()));
				}
			}
		}
		if (pathMatchesByContentType.isEmpty())
			return do415(method, req, resp);
		else if(logger.isTraceEnabled()){
			logger.trace(String.format("There are %d routes that match by request type.", pathMatchesByContentType.size()));
		}

		List<PathMatch> pathMatchesFinalCandidates = pathMatchesByContentType;
		
		if(logger.isTraceEnabled()){
			logger.trace(String.format("There are %d routes that match by all criteria selecting most specific route. Final candidates:", pathMatchesFinalCandidates.size()));
			for(PathMatch pm : pathMatchesFinalCandidates)
				logger.trace(String.format("Final candidate: ", pm.getRoute()));
		}
		PathMatch selectedMatch = pathMatchesFinalCandidates.get(0);
		for(PathMatch pm : pathMatchesFinalCandidates) {
			PathMatch prelimSelection = selectedMatch;
			if(pm != selectedMatch){
				if(StringUtils.isEmpty(selectedMatch.getRoute().getReturnType())
					&& !StringUtils.isEmpty(pm.getRoute().getReturnType())){
					selectedMatch = pm;
				} else if(StringUtils.isEmpty(selectedMatch.getRoute().getRequestType()) 
						&& !StringUtils.isEmpty(pm.getRoute().getRequestType())) {
						selectedMatch = pm;
				}
			}
			if(logger.isTraceEnabled() && prelimSelection != selectedMatch){
				logger.trace(String.format("Route %s is more specific than %s", selectedMatch, prelimSelection));
			}
		}
		if(logger.isTraceEnabled())
			logger.trace(String.format("Route chosen for invocation %s", selectedMatch));
		try {
			return methodInvoker.invokeMethod(method, selectedMatch, req, resp).handled;
		} catch (Throwable t) {
			return do500(method, req, resp, t);
		}
		
	}

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod().toUpperCase());
		if(!routeRequest(httpMethod, request, response))
			logger.error(String.format("Failed to handle %s request.", httpMethod.name()));
	}
	
	@Override
	public List<String> getReturnFormats() {
		return Arrays.asList("application/json");
	}

	@Override
	public void dumpEndpoints(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		List<String> endpointPaths = new ArrayList<String>();
		for(Route route : this.routes) {
			endpointPaths.add(route.getPathTemplate().getLexicalPath());
		}
		resp.setStatus(200);
		byte bytes[] = objectMapper.writeValueAsBytes(endpointPaths);
		resp.setContentLength(bytes.length);
		resp.setContentType("application/json");
		resp.getOutputStream().write(bytes);
		resp.getOutputStream().close();
	}
}
