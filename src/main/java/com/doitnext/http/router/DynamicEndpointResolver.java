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

import java.util.TreeSet;

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
	 * @param knownRoutes the list of routes known to the RestRouterServlet at the time
	 * of the call to this method.  
	 * @param eTag a string value that can be used by the implementation of this interface
	 * to decide whether the knownRoutes tree should be updated.  The strategy for
	 * computing this eTag is left to the implementor.
	 * 
	 * @return the same or a new eTag value depending on what the implementation decided
	 * to do with the knownRoutes.
	 */
	String updateRoutes(String eTag, TreeSet<Route> knownRoutes); 
}
