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
