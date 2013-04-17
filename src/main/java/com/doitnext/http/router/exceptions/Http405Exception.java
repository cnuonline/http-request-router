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
package com.doitnext.http.router.exceptions;

import java.util.List;

import com.doitnext.http.router.annotations.enums.HttpMethod;

/**
 * Used to handle Http405 errors from within RestRouterServlet.
 * 
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
public class Http405Exception extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2301765097582380073L;

	public Http405Exception(HttpMethod method, List<String> allowedMethods) {
		super(String.format(
				"HTTP 405: The requested method %s is now allowed for this resource. "
						+ "The allowed methods are ", method.name(),
				allowedMethods));
	}
}
