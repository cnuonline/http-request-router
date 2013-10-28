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
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

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
public class DefaultSuccessHandlerTest {
	Logger logger = LoggerFactory.getLogger(DefaultSuccessHandlerTest.class);
	@Test
	public void testGettersAndSetters() {
		DefaultSuccessHandler h = new DefaultSuccessHandler();
		Assert.assertTrue(h.getResponseTypes().isEmpty());
		Assert.assertTrue(h.getResponseFormats().contains("application/json"));
	}
	
	@Test
	public void testHandleResponseYes() throws UnsupportedEncodingException {
		DefaultSuccessHandler h = new DefaultSuccessHandler();
		String responseData = "Hidey Ho!!";
		MockHttpServletResponse response = new MockHttpServletResponse();
		boolean handled = h.handleResponse(null, null, response, responseData);
		Assert.assertTrue(handled);
		
		Assert.assertEquals("\"Hidey Ho!!\"", response.getContentAsString());
	}

	@Test 
	public void testHandleResponseNo() {
		DefaultSuccessHandler h = new DefaultSuccessHandler();
		String responseData = "Hidey Ho!!";
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		Exception e = new RuntimeException();
		Mockito.doThrow(e).when(response).setContentType(Mockito.anyString());
		logger.info("Testing DefaultSuccessHandler ability to handle exceptions while interacting with HttpServletResponse");
		boolean handled = h.handleResponse(null, null, response, responseData);
		logger.info("Awesome!! DefaultSuccessHandler handled the exception appropriately.");
		Assert.assertTrue(handled);
	}

	@Test 
	public void testHandleResponseFail() {
		DefaultSuccessHandler h = new DefaultSuccessHandler();
		String responseData = "Hidey Ho!!";
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		Exception e = new RuntimeException();
		IOException ioe = new IOException();
		Mockito.doThrow(e).when(response).setContentType(Mockito.anyString());
		try {
			Mockito.doThrow(ioe).when(response).sendError(500);
			logger.info("Testing DefaultSuccessHandler ability to handle exceptions while interacting with HttpServletResponse");
			boolean handled = h.handleResponse(null, null, response, responseData);
			Assert.assertTrue(handled);
			logger.info("Awesome!! DefaultSuccessHandler handled the exception appropriately.");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	@Test
	public void testHandleVoidResponses() {
		DefaultSuccessHandler h = new DefaultSuccessHandler();
		String responseData = null;
		MockHttpServletResponse response = new MockHttpServletResponse();
		boolean handled = h.handleResponse(null, null, response, responseData);
		Assert.assertTrue(handled);
	}

}
