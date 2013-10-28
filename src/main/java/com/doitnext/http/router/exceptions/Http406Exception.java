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

/**
 * Used to handle Http406 errors from within RestRouterServlet.
 * 
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
public class Http406Exception extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2301765097582380073L;

	public Http406Exception() {
		super(
				String.format("HTTP 406: Not acceptable. The requested resource cannot be served as any of"
						+ " the content types specified in the HTTP request 'Accept' header."));
	}

}
