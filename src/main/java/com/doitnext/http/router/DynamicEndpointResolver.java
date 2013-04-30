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
 * This interface may be implemented by web applications that utilize the RestRouterServlet.
 * This allows the web application to add routes through mechanisms other than the compile
 * time annotation method.  For example if implementing a data driven API mock service, 
 * the web app may load the core API methods for Creating/Updating and deleting Mock API's but
 * then delegate routes to the created Mocks via the DynamicEndpointResolver.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public interface DynamicEndpointResolver {
	/**
	 * This method is called by the RestRouterService in response to a call to the RestRouterServlet's
	 * fireUpdateRoutes() method.
	 * 
	 * <p>The implementation of this method is expected to call {@link RestRouterServlet#routesUpdated}
	 * when finished updating the known routes.  When RestRouterServlet calls this method, 
	 * it does so with a defensive copy of the internal {@link RestRouterServlet#routes} field
	 * which remains unmodified until <code>routesUpdated</code> is called with a new
	 * set of routes being provided by the implementation of this method in the call.</p>
	 * @param knownRoutes the list of routes known to the RestRouterServlet at the time
	 * of the call to this method.  
	 * @param servlet 
	 * the {@link RestRouterServlet} invoking this method.
	 * 
	 */
	void updateRoutes(DynamicRouteConsumer servlet, ImmutableSortedSet<Route> knownRoutes); 
}
