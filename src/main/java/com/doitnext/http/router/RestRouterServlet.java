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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.exceptions.Http404Exception;
import com.doitnext.http.router.exceptions.Http405Exception;
import com.doitnext.http.router.exceptions.Http406Exception;
import com.doitnext.http.router.exceptions.Http415Exception;
import com.doitnext.http.router.exceptions.Http500Exception;
import com.doitnext.http.router.responsehandlers.DefaultErrorHandler;
import com.doitnext.pathutils.Path;
import com.google.common.collect.ImmutableSortedSet;

public class RestRouterServlet extends HttpServlet {

	/**
	 * Serial version id for this servlet
	 */
	private static final long serialVersionUID = 149227713315378579L;
	private static Logger logger = LoggerFactory
			.getLogger(RestRouterServlet.class);

	private String restPackageRoot;
	private String pathPrefix;
	private DynamicEndpointResolver dynamicEndpointResolver = null;
	private MethodInvoker methodInvoker;
	private volatile ImmutableSortedSet<Route> routes;
	private DefaultErrorHandler errorHandler = new DefaultErrorHandler();

	public RestRouterServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		pathPrefix = config.getInitParameter("pathPrefix");
		restPackageRoot = config.getInitParameter("restPackageRoot");
		EndpointResolver ep = new EndpointResolver();
		routes = ImmutableSortedSet.copyOf(ep.resolveEndpoints(pathPrefix, restPackageRoot));

		if (routes == null)
			throw new ServletException(
					"EndpointResolver returned a null TreeSet");
		if (routes.isEmpty()) {
			logger.warn("On init no routes established. This servlet will not respond to any requests while in this state.");
		} else {
			logger.debug(String.format("Identified %d routes.", routes.size()));
		}

		String methodInvokerClass = config
				.getInitParameter("methodInvokerClass");
		if ((methodInvokerClass == null)
				|| (methodInvokerClass.trim().isEmpty())) {
			methodInvoker = new DefaultInvoker();
		} else {
			String methodInvokerFactoryMethod = config
					.getInitParameter("methodInvokerFactoryMethod");
			initMethodInvoker(methodInvokerClass, methodInvokerFactoryMethod);
		}

		String dynamicEndpointResolverClass = config
				.getInitParameter("dynamicEndpointResolver");
		if (!StringUtils.isEmpty(dynamicEndpointResolverClass)) {
			try {
				Class<?> resolverClass = Class
						.forName(dynamicEndpointResolverClass);
				dynamicEndpointResolver = (DynamicEndpointResolver) resolverClass
						.newInstance();
			} catch (Exception e) {
				throw new ServletException(
						"Error instantiating dynamicEndpointResolver", e);
			}
		}
	}

	private void initMethodInvoker(String methodInvokerClass,
			String methodInvokerFactoryMethod) throws ServletException {
		try {
			Class<?> loadedClass = Class.forName(methodInvokerClass);
			Method factoryMethod = null;

			if (!StringUtils.isEmpty(methodInvokerFactoryMethod)) {
				@SuppressWarnings("unchecked")
				Set<Method> methods = ReflectionUtils.getMethods(loadedClass,
						ReflectionUtils.withName(methodInvokerFactoryMethod),
						ReflectionUtils.withParametersCount(0));
				if (!methods.isEmpty())
					factoryMethod = methods.iterator().next();
			}
			if (factoryMethod == null)
				methodInvoker = (MethodInvoker) loadedClass.newInstance();
			else
				methodInvoker = (MethodInvoker) factoryMethod
						.invoke(loadedClass);
		} catch (Exception e) {
			throw new ServletException("Unable to load methodInvoker.", e);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (!routeRequest(HttpMethod.GET, req, resp))
			super.doGet(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (!routeRequest(HttpMethod.HEAD, req, resp))
			super.doHead(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (!routeRequest(HttpMethod.POST, req, resp))
			super.doPost(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (!routeRequest(HttpMethod.PUT, req, resp))
			super.doPut(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (!routeRequest(HttpMethod.DELETE, req, resp))
			super.doDelete(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doOptions(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (!routeRequest(HttpMethod.OPTIONS, req, resp))
			super.doOptions(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doTrace(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (!routeRequest(HttpMethod.TRACE, req, resp))
			super.doTrace(req, resp);
	}

	@Override
	public String getServletInfo() {
		return "Servlet: RestRouterServlet; Copyright: 2013 Steve Owens (steve@doitnext.com); Licence: Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0";
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
