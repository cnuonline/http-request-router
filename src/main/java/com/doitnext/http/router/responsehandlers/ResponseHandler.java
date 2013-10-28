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
package com.doitnext.http.router.responsehandlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doitnext.http.router.PathMatch;
import com.doitnext.http.router.RestRouterServlet;

/**
 * The interface for response handler implementations.  A response handler 
 * implementation is a strategy for mediating the response
 * from a method invocation back to the client via HttpServletResponse.
 * 
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
public interface ResponseHandler {

	/**
	 * @return a list of strings that indicates the response formats that this 
	 * handler can deal with.
	 */
	public List<String> getResponseFormats();
	
	
	public List<String> getResponseTypes();
	
	/**
	 * @param pathMatch
	 *            the {@link PathMatch} that identifies the invoked resource.
	 * @param request
	 *            the {@link HttpServletRequest} passed to the
	 *            {@link RestRouterServlet}.
	 * @param response
	 *            the {@link HttpServletResponse} passed to the
	 *            {@link RestRouterServlet}
	 * @param responseData
	 * 	   the object containing the data to be returned in the response.
	 * @return <code>true</code> if the error response handler handled the
	 *         response <code>false</code> otherwise.
	 */
	public boolean handleResponse(PathMatch pathMatch,
			HttpServletRequest request, HttpServletResponse response,
			Object responseData);
}
