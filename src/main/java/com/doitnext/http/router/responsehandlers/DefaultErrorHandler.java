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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doitnext.http.router.PathMatch;
import com.doitnext.http.router.annotations.ExceptionHandler;
import com.doitnext.http.router.annotations.OnException;
import com.doitnext.http.router.exceptions.FieldValidationException;
import com.doitnext.http.router.responseformatter.JacksonResponseFormatter;
import com.google.common.collect.ImmutableList;

public class DefaultErrorHandler implements ResponseHandler {
	private static Logger logger = LoggerFactory.getLogger(DefaultErrorHandler.class);
	
	private static JacksonResponseFormatter jacksonResponseFormatter = new JacksonResponseFormatter();
	private static final List<String> responseFormats =
			ImmutableList.copyOf(Arrays.asList(new String[] {"application/json"}));
			
	private static final List<String> responseTypes = 
			ImmutableList.copyOf(Arrays.asList(new String[] {"http://com.doitnext.http.router/exception"}));
	
	public DefaultErrorHandler() {
	}

	@Override
	public List<String> getResponseFormats() {
		return responseFormats;
	}

	@Override
	public List<String> getResponseTypes() {
		return responseTypes;
	}

	private OnException mapErrorToResponseCode(PathMatch pm, Throwable t) {
		if(pm != null && t != null) {
			ExceptionHandler eh = pm.getRoute().getImplMethod().getAnnotation(ExceptionHandler.class);
			if(eh != null){
				for(OnException oe : eh.value()) {
					if(oe.exceptionClass().isAssignableFrom(t.getClass()))
						return oe;
				}
				
			}
		}
		return null;
	}
	
	@Override
	public boolean handleResponse(PathMatch pathMatch,
			HttpServletRequest request, HttpServletResponse response,
			Object responseData) {
		try {
			OnException oe = null;
			if(response.getStatus() < 400) {
				oe = mapErrorToResponseCode(pathMatch, (Throwable)responseData);
				if(oe != null){
					response.setStatus(oe.statusCode());
				} else {
					response.setStatus(500);
				}
			}
			ErrorWrapper error = new ErrorWrapper((Throwable)responseData, oe);
			
			
			/*TODO: This code could be improved quite a bit.  Ideally it should 
			 * parse the request Accepts header and determine the allowed content encodings.
			 * However this is not an efficient place to do this.  Ideally when the
			 * pathMatch object is being constructed, the acceptable response encodings
			 * should be in a field contained in the Path subobject.  For now we will just
			 * return UTF-8 encoding.
			 */
			byte responseBytes[] = jacksonResponseFormatter.formatResponseUtf8(error, null, null);
			response.setContentType("application/json;charset=UTF-8");
			response.setContentLength(responseBytes.length);
			response.getOutputStream().write(responseBytes);
			response.getOutputStream().flush();
			response.getOutputStream().close();
			return true;
		} catch(Exception e) {
			logger.error("Unable to handle response.", e);
		}
		return false;
	}
	
	public  static class ErrorWrapper {
		public final List<String> errorMessages = new ArrayList<String>();
		public final String errorType;
		public final String errorCode;
		public final String implicatedFields[];
		ErrorWrapper(Throwable t, OnException oe) {
			if(oe != null) {
				errorType = oe.exceptionClass().getName();
				errorCode = oe.errorCode();
			} else { 
				errorType = t.getClass().getName();
				errorCode = null;
			}
			if(t instanceof FieldValidationException){
				implicatedFields = ((FieldValidationException)t).getImplicatedFields();
			} else 
				implicatedFields = new String[] {};
			errorMessages.add(String.format("%s", t.getMessage()));
			Throwable cause = t.getCause();
			while(cause != null) {
				errorMessages.add(String.format("%s: %s", cause.getClass().getName(), cause.getMessage()));
				cause = cause.getCause();
			}
		}
	}

}
