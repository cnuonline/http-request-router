package com.doitnext.http.router.responsehandlers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;
/**
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class DefaultErrorHandlerTest {
	Logger logger = LoggerFactory.getLogger(DefaultErrorHandlerTest.class);
	@Test
	public void testGettersAndSetters() {
		DefaultErrorHandler h = new DefaultErrorHandler();
		Assert.assertTrue(h.getResponseTypes().contains("http://com.doitnext.http.router/exception"));
		Assert.assertTrue(h.getResponseFormats().contains("application/json"));
	}
	
	@Test
	public void testHandleResponseYes() throws IOException {
		DefaultErrorHandler h = new DefaultErrorHandler();
		Exception responseData = new IllegalArgumentException("Hidey Ho!!", new NumberFormatException("3r3"));
		MockHttpServletResponse response = new MockHttpServletResponse();
		boolean handled = h.handleResponse(null, null, response, responseData);
		Assert.assertTrue(handled);
		
		String expectedContent = IOUtils.toString(this.getClass().getResourceAsStream("error1.txt"), "UTF-8");
		Assert.assertEquals(expectedContent, response.getContentAsString());
	}

	@Test
	public void testHandleValidStatusCode() throws IOException {
		DefaultErrorHandler h = new DefaultErrorHandler();
		Exception responseData = new IllegalArgumentException("Hidey Ho!!", new NumberFormatException("3r3"));
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(403);
		boolean handled = h.handleResponse(null, null, response, responseData);
		Assert.assertTrue(handled);
		Assert.assertEquals(403, response.getStatus());
	}
	
	@Test 
	public void testHandleResponseFail() {
		DefaultErrorHandler h = new DefaultErrorHandler();
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		Exception responseData = new IllegalArgumentException("Hidey Ho!!", new NumberFormatException("3r3"));
		Exception e = new RuntimeException();
		Mockito.doThrow(e).when(response).setContentType(Mockito.anyString());
		logger.info("Testing DefaultErrorHandler ability to handle exceptions while interacting with HttpServletResponse");
		boolean handled = h.handleResponse(null, null, response, responseData);
		Assert.assertFalse(handled);
		logger.info("Awesome!! DefaultErrorHandler handled the exception appropriately.");
	}

}
