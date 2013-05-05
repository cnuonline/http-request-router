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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.doitnext.http.router.exampleclasses.TestCollectionImpl;
import com.doitnext.http.router.responsehandlers.DefaultErrorHandler;
import com.doitnext.http.router.responsehandlers.DefaultSuccessHandler;
import com.doitnext.http.router.responsehandlers.ResponseHandler;

public class ApiDocumentationTest {
	private TestCollectionImpl resourceImp = new TestCollectionImpl();
	private DefaultEndpointResolver endpointResolver = new DefaultEndpointResolver();
	private ApplicationContext applicationContext = mock(ApplicationContext.class);
	private MethodInvoker methodInvoker = new DefaultInvoker();
	private ResponseHandler errorHandler = new DefaultErrorHandler();
	private RestRouterServlet servlet;
	
	@Before
	public void init() throws Exception {
		Map<MethodReturnKey, ResponseHandler> successHandlers = new HashMap<MethodReturnKey, ResponseHandler>();
		ResponseHandler successHandlerJson = new DefaultSuccessHandler();
		successHandlers.put(new MethodReturnKey("", "application/json"),
				successHandlerJson);
		successHandlers.put(new MethodReturnKey("", ""), successHandlerJson);	
		successHandlers.put(new MethodReturnKey("hashmap", "application/json"), successHandlerJson);
		
		endpointResolver.setSuccessHandlers(successHandlers);
		endpointResolver.setApplicationContext(applicationContext);
		when(applicationContext.getBean("testCollection1",TestCollectionImpl.class)).thenReturn(resourceImp);
		servlet = new RestRouterServlet();
		servlet.setPathPrefix("/sports-api");
		servlet.setRestPackageRoot("com.doitnext.http.router.exampleclasses");
		servlet.setEndpointResolver(endpointResolver);
		servlet.setMethodInvoker(methodInvoker);
		servlet.setErrorHandler(errorHandler);
		servlet.setDynamicEndpointResolver(null);
		servlet.afterPropertiesSet();
	}
	
	@Test
	public void testDocumentor() throws Exception {
		SortedSet<Route> routes = servlet.getRoutes();
		ApiDocumentation docs = new ApiDocumentation("Test API", "Used to test features of Rest Router Servlet", routes);
		ObjectMapper mapper = new ObjectMapper();
		String docsAsString = mapper.writeValueAsString(docs);
		System.out.println(docsAsString);
		InputStream is = this.getClass().getResourceAsStream("expectedApiDocumentationValue.dat");
		String expected = IOUtils.toString(is, "UTF-8");
		Assert.assertEquals(expected, docsAsString);
		
	}
}
