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

import com.google.common.collect.ImmutableSortedSet;

/**
 * This interface is used during initialization to resolve endpoints on startup of the
 * {@link RestRouterServlet}.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public interface EndpointResolver {

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
	public ImmutableSortedSet<Route> resolveEndpoints(String pathPrefix, String basePackage);

}