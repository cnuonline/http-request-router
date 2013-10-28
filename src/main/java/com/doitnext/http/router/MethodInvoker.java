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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doitnext.http.router.annotations.enums.HttpMethod;

/**
 * Interface to be impemented by the invoker of methods.  Different web
 * applicatoins may choose to handle invocation differently.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public interface MethodInvoker {
	
	enum InvokeResult { 
		METHOD_SUCCESS(true, true), METHOD_ERROR(false, true),
		METHOD_SUCCESS_UNHANDLED(true, false), METHOD_ERROR_UNHANDLED(false, false);
		
		public final boolean handled;
		public final boolean success;
		private InvokeResult(boolean success, boolean handled){
			this.handled = handled;
			this.success = success;
		}
	};
	
	
	/**
	 * Invokes a method on behalf of an HttpServletRequest
	 * @param method the HttpMethod specified by the HttpServletRequest
	 * @param pm the PathMatch object containing the matched path and the route.
	 * @param req the HttpServletRequest
	 * @param resp the HttpServletResponse
	 * @return one of 4 possible values [METHOD_SUCCESS, METHOD_ERROR, METHOD_SUCCESS_UNHANDLED, METHOD_ERROR_UNHANDLED]
	 * <dl>
	 * <dt><code>METHOD_SUCCESS*</code></dt><dd> if method invoked returned normally</dd> 
	 * <dt><code>METHOD_ERROR*</code></dt><dd> if the method invoked threw an exception.</dd>
	 * <dt><code>*_UNHANDLED</code></dt><dd> if the result handler failed to handle the result.<dd>
	 * </dl>
	 * @throws ServletException
	 * 		on invocation error.
	 * @see java.lang.reflect.Method#invoke
	 */
	InvokeResult invokeMethod(HttpMethod method, PathMatch pm,
			HttpServletRequest req, HttpServletResponse resp) throws ServletException;
}
