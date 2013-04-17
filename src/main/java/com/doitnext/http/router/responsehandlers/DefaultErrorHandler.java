package com.doitnext.http.router.responsehandlers;

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

	@Override
	public boolean handleResponse(PathMatch pathMatch,
			HttpServletRequest request, HttpServletResponse response,
			Object responseData) {
		try {
			ErrorWrapper error = new ErrorWrapper((Throwable)responseData);
			if(response.getStatus() < 400)
				response.setStatus(500);
			
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
	
	private static class ErrorWrapper {
		private final List<String> errorMessages = new ArrayList<String>();
		ErrorWrapper(Throwable t) {
			errorMessages.add(String.format("%s: %s", t.getClass().getName(), t.getMessage()));
			Throwable cause = t.getCause();
			while(cause != null) {
				errorMessages.add(String.format("%s: %s", cause.getClass().getName(), cause.getMessage()));
				cause = cause.getCause();
			}
		}
		/**
		 * @return the errorMessages
		 */
		public List<String> getErrorMessages() {
			return errorMessages;
		}
		
		
	}

}
