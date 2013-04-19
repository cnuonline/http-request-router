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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.doitnext.http.router.MethodInvoker.InvokeResult;
import com.doitnext.http.router.annotations.RestMethod;
import com.doitnext.http.router.annotations.RestResource;
import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.exampleclasses.TestResourceImpl;
import com.doitnext.http.router.exampleclasses.TestTeamPojo;
import com.doitnext.http.router.responsehandlers.ResponseHandler;
import com.doitnext.pathutils.Path;

public class DefaultInvokerTest {
	private TestResourceImpl testResourceImpl;
	ResponseHandler errorHandlerJson = mock(ResponseHandler.class);
	ResponseHandler successHandlerJson = mock(ResponseHandler.class);
	MethodInvoker invoker;
	
	List<PathMatch> pathMatches = new ArrayList<PathMatch>();
	
	@Before
	public void init() throws Exception {
		DefaultEndpointResolver resolver = new DefaultEndpointResolver();
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		resolver.setApplicationContext(applicationContext);
		testResourceImpl = new TestResourceImpl();
		
		when(applicationContext.getBean("testResource1", TestResourceImpl.class)).thenReturn(testResourceImpl);
		
		invoker = new DefaultInvoker();
		
		Map<MethodReturnKey, ResponseHandler> errorHandlers = new HashMap<MethodReturnKey, ResponseHandler>();
		Map<MethodReturnKey, ResponseHandler> successHandlers = new HashMap<MethodReturnKey, ResponseHandler>();
		
		errorHandlers.put(new MethodReturnKey("","application/json"), errorHandlerJson);
		errorHandlers.put(new MethodReturnKey("","text/plain"), errorHandlerJson);
		successHandlers.put(new MethodReturnKey("", "application/json"), successHandlerJson);
		successHandlers.put(new MethodReturnKey("", ""), successHandlerJson);
		
		resolver.setErrorHandlers(errorHandlers);
		resolver.setSuccessHandlers(successHandlers);
		resolver.setMethodInvoker(invoker);

		SortedSet<Route> routes;
		
		when(errorHandlerJson.handleResponse(any(PathMatch.class), any(HttpServletRequest.class),
				any(HttpServletResponse.class), any(Throwable.class))).thenReturn(true);
		when(successHandlerJson.handleResponse(any(PathMatch.class), any(HttpServletRequest.class),
				any(HttpServletResponse.class), any(Object.class))).thenReturn(true);

		routes = resolver.resolveEndpoints("/home-cities", "com.doitnext.http.router.exampleclasses");

		// /{teamType:[A-Z]{1,10}:TEAMTYPE}/{teamName:[a-zA-Z+'\\-0-9]{2,30}:TEXT}
		String samplePaths[] = {
				"/home-cities/teams",
				"/home-cities/teams/BOXING/Rockem+Sockem+Robots",
				"/home-cities/teams/ACROBATIC/Flying+Squirrels"
		};
		
		
		for(Route route : routes) {
			RestResource rr = route.getImplClass().getAnnotation(RestResource.class);
			RestMethod rm = route.getImplMethod().getAnnotation(RestMethod.class);
			for(String pathString : samplePaths) {
				Path path = route.getPathTemplate().match(pathString);
				if(path != null)
					pathMatches.add(new PathMatch(route, path));
			}
		}

	}
	
	
	
	@Test
	public void testInvokeHappyCases() throws Exception {
		DefaultInvoker invoker = new DefaultInvoker();
		int x = 0;

		for(PathMatch pm : pathMatches) {
			x++;
			for(HttpMethod method : HttpMethod.values()) {
				String testCaseId = String.format("%s %s --> %s.%s", 
						method.name(), pm.getMatchedPath().getGivenPath(),
						pm.getRoute().getImplClass().getName(),
						pm.getRoute().getImplMethod().getName());
				HttpServletRequest req = mock(HttpServletRequest.class);
				HttpServletResponse resp = mock(HttpServletResponse.class);
				InvokeResult result = invoker.invokeMethod(method, pm, req, resp);
				
				Assert.assertTrue(result.handled);
				if(result.success){
					Assert.assertFalse(testCaseId, StringUtils.isEmpty(testResourceImpl.getLastHttpMethodCalled()));
					HttpMethod methodCalled = HttpMethod.valueOf(testResourceImpl.getLastHttpMethodCalled());
					Assert.assertEquals(testCaseId, pm.getRoute().getHttpMethod(), methodCalled);
					Assert.assertEquals(testCaseId, pm.getRoute().getImplMethod().getName(), testResourceImpl.getLastMethodCalled());
					verify(successHandlerJson).handleResponse(eq(pm), eq(req), eq(resp), any(TestTeamPojo.class));
				} else {
					verify(errorHandlerJson).handleResponse(eq(pm), eq(req), eq(resp), any(Throwable.class));
				} 
			}
		}
	}
}
