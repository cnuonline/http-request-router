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
package com.doitnext.http.router.responseformatter;

import java.net.URI;

/**
 * Formats a java object to a specific format with amplifying details provided by a 
 * Schema resource.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public interface ResponseFormatter {
	/**
	 * Formats a java object to a specific format with amplifying details provided by a 
	 * schema resource.
	 * @param response 
	 * the object model containing the data for the response.
	 * @param schemaUri 
	 * a URI that identifies a resource that the ResponseFormatter
	 * implementation can use verify that the {@link #response} conforms to a pre-determined
	 * set of constraints.
	 * @param templateUri 
	 * a URI that identifies a resource that the ResponseFormatter can use to 
	 * template the response for output.
	 * @return 
	 * a string representation of the formatted response or null if an error occured while formatting.
	 */
	String formatResponse(Object response, URI schemaUri, URI templateUri);

	/**
	 * Formats a java object to a specific format with amplifying details provided by a 
	 * schema resource.
	 * @param response 
	 * the object model containing the data for the response.
	 * @param schemaUri 
	 * a URI that identifies a resource that the ResponseFormatter
	 * implementation can use verify that the {@link #response} conforms to a pre-determined
	 * set of constraints.
	 * @param templateUri 
	 * a URI that identifies a resource that the ResponseFormatter can use to 
	 * template the response for output.
	 * @return 
	 * a byte array UTF-8 representation of the formatted response or null if an error occured while formatting.
	 */
	byte[] formatResponseUtf8(Object response, URI schemaUri, URI templateUri);
}
