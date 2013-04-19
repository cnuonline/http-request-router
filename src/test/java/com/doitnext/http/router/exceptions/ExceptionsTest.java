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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.doitnext.http.router.annotations.enums.HttpMethod;

public class ExceptionsTest {

	@Test
	public void testHttp404Exception() {
		Exception e = new Http404Exception("/search");
		Assert.assertEquals("HTTP 404: The requested URL '/search' was not found on this server.", e.getMessage());
	}
	
	@Test
	public void testHttp405Exception() {
		Exception e = new Http405Exception(HttpMethod.TRACE, Arrays.asList("GET", "PUT", "POST", "DELETE", "OPTIONS"));
		Assert.assertEquals("HTTP 405: The requested method TRACE is now allowed for this resource. The allowed methods are [GET, PUT, POST, DELETE, OPTIONS]", e.getMessage());
		
	}
	
	@Test
	public void testHttp406Exception() {
		Exception e = new Http406Exception();
		Assert.assertEquals("HTTP 406: Not acceptable. The requested resource cannot be served as any of the content types specified in the HTTP request 'Accept' header.", e.getMessage());		
	}
	
	@Test
	public void testHttp415Exception() {
		Exception e = new Http415Exception("application/ion");
		Assert.assertEquals("HTTP 415: Unsupported media type.  The request Content-Type 'application/ion' is not a content type suitable for submission to the server resource.", e.getMessage());				
	}

	@Test
	public void testHttp500Exception() {
		Exception e = new Http500Exception(new IllegalArgumentException("Arguments are for wussies."));
		Assert.assertEquals("HTTP 500: Internal server error.", e.getMessage());				
	}

}
