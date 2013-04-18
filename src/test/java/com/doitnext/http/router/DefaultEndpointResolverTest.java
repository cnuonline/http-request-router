package com.doitnext.http.router;


import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.doitnext.http.router.responsehandlers.ResponseHandler;

public class DefaultEndpointResolverTest {

	@Before
	public void init() {
		
	}
	
	@Test
	public void testResolveEndpoints() {
		
		DefaultEndpointResolver resolver = new DefaultEndpointResolver();
		ResponseHandler errorHandlerJson = mock(ResponseHandler.class);
		ResponseHandler successHandlerJson = mock(ResponseHandler.class);
		MethodInvoker invoker = mock(MethodInvoker.class);
		Map<MethodReturnKey, ResponseHandler> errorHandlers = new HashMap<MethodReturnKey, ResponseHandler>();
		Map<MethodReturnKey, ResponseHandler> successHandlers = new HashMap<MethodReturnKey, ResponseHandler>();
		
		errorHandlers.put(new MethodReturnKey("","application/json"), errorHandlerJson);
		errorHandlers.put(new MethodReturnKey("","text/plain"), errorHandlerJson);
		successHandlers.put(new MethodReturnKey("", "application/json"), successHandlerJson);
		successHandlers.put(new MethodReturnKey("", ""), successHandlerJson);
		
		resolver.setErrorHandlers(errorHandlers);
		resolver.setSuccessHandlers(successHandlers);
		resolver.setMethodInvoker(invoker);
		
		SortedSet<Route> routes = resolver.resolveEndpoints("/gigi", "com.doitnext.http.router.exampleclasses");
		
		Assert.assertNotNull(routes);
		Assert.assertFalse(routes.isEmpty());
	}
}
