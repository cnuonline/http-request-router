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


import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.context.ApplicationContext;

import com.doitnext.http.router.exampleclasses.TestCollectionImpl;
import com.doitnext.http.router.responsehandlers.ResponseHandler;

public class DefaultEndpointResolverTest {

	@Before
	public void init() {
		
	}
	
	@Test
	public void testResolveEndpoints() {
		
		DefaultEndpointResolver resolver = new DefaultEndpointResolver();
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		resolver.setApplicationContext(applicationContext);
		TestCollectionImpl testCollectionImpl = new TestCollectionImpl();
		
		when(applicationContext.getBean("testCollection1", TestCollectionImpl.class)).thenReturn(testCollectionImpl);
		
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
		
		verify(applicationContext, atLeastOnce()).getBean(eq("testCollection1"), eq(TestCollectionImpl.class));
		Assert.assertNotNull(routes);
		Assert.assertFalse(routes.isEmpty());
	}
}
