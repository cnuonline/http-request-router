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

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.JsonMappingException;

/**
 * Used to dump the endpoints in response to an endpoint dump request.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public interface EndpointDumper {

	/**
	 * @return the formats supported for the response (e.g. "application/json")
	 */
	List<String> getReturnFormats();
	
	/**
	 * @param req the HttpServletRequest being responded to
	 * @param resp the HttpServletResponse on which the endpoints will be dumped.
	 * @throws JsonMappingException 
	 * @throws IOException 
	 */
	void dumpEndpoints(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
