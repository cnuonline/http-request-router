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
