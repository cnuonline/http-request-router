package com.doitnext.http.router;

import com.doitnext.pathutils.Path;

/**
 * Associates a Matched path with a Route.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class PathMatch {

	final private Route route;
	final private Path matchedPath;
	/**
	 * @param route the Route part of the association
	 * @param matchedPath the Path part of the association
	 */
	public PathMatch(Route route, Path matchedPath) {
		this.route = route;
		this.matchedPath = matchedPath;
	}
	/**
	 * @return the route
	 */
	public Route getRoute() {
		return route;
	}
	/**
	 * @return the matchedPath
	 */
	public Path getMatchedPath() {
		return matchedPath;
	}

}
