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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doitnext.http.router.PathMatch;
import com.doitnext.http.router.responseformatter.JacksonResponseFormatter;
import com.google.common.collect.ImmutableList;

public class DefaultSuccessHandler implements ResponseHandler {
	private static Logger logger = LoggerFactory.getLogger(DefaultSuccessHandler.class);
	
	private static JacksonResponseFormatter jacksonResponseFormatter = new JacksonResponseFormatter();
	private static final List<String> responseFormats =
			ImmutableList.copyOf(Arrays.asList(new String[] {"application/json"}));
			
	private static List<String> responseTypes = 
			ImmutableList.copyOf(new ArrayList<String>());
	
	public DefaultSuccessHandler() {
	}

	@Override
	public List<String> getResponseFormats() {
		return responseFormats;
	}

	@Override
	public List<String> getResponseTypes() {
		return responseTypes;
	}

	@Override
	public boolean handleResponse(PathMatch pathMatch,
			HttpServletRequest request, HttpServletResponse response,
			Object responseData) {
		try {
			if(responseData != null) {
				/*TODO: This code could be improved quite a bit.  Ideally it should 
				 * parse the request Accepts header and determine the allowed content encodings.
				 * However this is not an efficient place to do this.  Ideally when the
				 * pathMatch object is being constructed, the acceptable response encodings
				 * should be in a field contained in the Path subobject.  For now we will just
				 * return UTF-8 encoding.
				 */
				byte responseBytes[] = jacksonResponseFormatter.formatResponseUtf8(responseData, null, null);
				response.setContentType("application/json;charset=UTF-8");
				response.setContentLength(responseBytes.length);
				response.getOutputStream().write(responseBytes);
				response.getOutputStream().flush();
				response.getOutputStream().close();
			}
		} catch(Exception e) {
			logger.error("Unable to handle response.", e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
				logger.error("Exception thrown while sending error response to client", e1);
			}
		}
		return true;
	}
}
