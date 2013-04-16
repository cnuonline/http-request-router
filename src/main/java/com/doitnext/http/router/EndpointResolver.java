package com.doitnext.http.router;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doitnext.http.router.annotations.RestMethod;
import com.doitnext.http.router.annotations.RestResource;
import com.doitnext.pathutils.PathTemplate;


/**
 * Uses Reflections to locate @RestResource annotated classes to build routes from bind
 * their @RestMethod definitions. These Routes are returned as a TreeSet which
 * can be used to map an HttpRequest to an implementation.
 * 
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
public class EndpointResolver {

	private static Logger logger = LoggerFactory.getLogger(EndpointResolver.class);
	public EndpointResolver() {
	}

	/**
	 * Scans a collection of java packages for classes annotated with the
	 * EndpointSet annotation. For each EndpointSet found, the resolver looks
	 * for methods annotated with the EndpointImpl annotation, and constructs a
	 * template using the concatenation of the given pathPrefix +
	 * EndpointSet.pathPrefix + EndpointImpl.template().
	 * 
	 * @param pathPrefix a common URI path prefix to all endpoints loaded by this class
	 * @param basePackage the base package to scan for annotated classes
	 * @return an ordered set of Route objects suitable for use by an HTTP router.
	 */
	public TreeSet<Route> resolveEndpoints(String pathPrefix, String basePackage) {
		TreeSet<Route> result = new TreeSet<Route>();
		logger.debug(String.format("Scanning %s for @EndpointImpl classes."));
		Reflections reflections = new Reflections(basePackage);
		Set<Class<?>> endpointSets = reflections.getTypesAnnotatedWith(RestResource.class);
		for(Class<?> classz : endpointSets) {
			addEndpointsToResult(pathPrefix, classz, result);
		}
		return result;
	}
	
	private void addEndpointsToResult(String pathPrefix, Class<?> classz, TreeSet<Route> routes) {
		RestResource resource = classz.getAnnotation(RestResource.class);
		StringBuilder pathBuilder = new StringBuilder(pathPrefix);
		pathBuilder.append(resource.pathprefix());
		
		String resourcePathPrefix = pathBuilder.toString();
		@SuppressWarnings("unchecked")
		Set<Method> methods = ReflectionUtils.getAllMethods(classz, ReflectionUtils.withAnnotation(RestMethod.class));
		
		for(Method method : methods) {
			RestMethod methodImpl = method.getAnnotation(RestMethod.class);
			pathBuilder = new StringBuilder(resourcePathPrefix);
			pathBuilder.append(methodImpl.template());
			PathTemplate pathTemplate = new PathTemplate("/", "?", null);
			Route route = new Route(methodImpl.method(),
					resolveRequestType(resource, methodImpl), 
					resolveReturnType(resource,methodImpl),
					resolveRequestFormat(resource, methodImpl),
					resolveReturnFormat(resource, methodImpl),
					pathTemplate, classz, method);
			if(routes.contains(route)) {
				logger.debug(String.format("An equivalent route to %s is already in routes. Ignoring.",
						route.toString()));
			} else {
				logger.debug(String.format("Adding route %s to routes.", route.toString()));
				routes.add(route);
			}
		}
	}

	private String resolveReturnFormat(RestResource res, RestMethod mthd) {
		if(mthd.requestFormat().trim().isEmpty())
			return res.returnFormat();
		else
			return mthd.returnFormat();
	}

	private String resolveRequestFormat(RestResource res, RestMethod mthd) {
		if(mthd.requestFormat().trim().isEmpty())
			return res.requestFormat();
		else
			return mthd.requestFormat();
	}

	private String resolveReturnType(RestResource res, RestMethod mthd) {
		if(mthd.returnType().trim().isEmpty())
			return res.returnType();
		else
			return mthd.returnType();
	}
	
	private String resolveRequestType(RestResource res, RestMethod mthd) {
		if(mthd.requestType().trim().isEmpty())
			return res.requestType();
		else
			return mthd.requestType();
	}

}
