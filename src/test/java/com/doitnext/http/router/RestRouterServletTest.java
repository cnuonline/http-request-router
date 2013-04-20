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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.exampleclasses.TestResourceImpl;
import com.doitnext.http.router.exampleclasses.TestTeamPojo;
import com.doitnext.http.router.responsehandlers.DefaultErrorHandler;
import com.doitnext.http.router.responsehandlers.ResponseHandler;

public class RestRouterServletTest {

	public RestRouterServletTest() {
		// TODO Auto-generated constructor stub
	}
	
	private TestResourceImpl resourceImp = new TestResourceImpl();
	private DefaultEndpointResolver endpointResolver = new DefaultEndpointResolver();
	private ApplicationContext applicationContext = mock(ApplicationContext.class);
	private MethodInvoker methodInvoker = new DefaultInvoker();
	private ResponseHandler errorHandler = new DefaultErrorHandler();
	private Map<String, TestTeamPojo> pojos = new HashMap<String, TestTeamPojo>();
	
	@Before
	public void init() {
		endpointResolver.setApplicationContext(applicationContext);
		when(applicationContext.getBean("testResource1",TestResourceImpl.class)).thenReturn(resourceImp);
	}

	@Test
	public void testHandleRequestHappyCases() throws Exception {
		RestRouterServlet servlet = new RestRouterServlet();
		servlet.setPathPrefix("/sports-api");
		servlet.setRestPackageRoot("com.doitnext.http.router.exampleclasses");
		servlet.setEndpointResolver(endpointResolver);
		servlet.setMethodInvoker(methodInvoker);
		servlet.setErrorHandler(errorHandler);
		servlet.setDynamicEndpointResolver(null);
		servlet.afterPropertiesSet();
		
		MockHttpServletRequest request;
		MockHttpServletResponse response;
		
		Object[][] testCases = {
			{"GET", "/mocker", "/sports-api/teams","city=Atlanta", "application/json, application/xml", null, null},
			{"GET", "/mocker", "/teams","city=Atlanta", "application/json, application/xml", null, null},
			{"POST", "/mocker", "/sports-api/teams","city=Atlanta", "application/json, application/xml", null, pojos.get("Cardinals-FOOTBALL")},
			{"POST", "/mocker", "/sports-api/teams","city=Atlanta", "application/json, application/xml", "application/xml", pojos.get("Cardinals-FOOTBALL")},
			{"POST", "/mocker", "/sports-api/teams","city=Atlanta", "application/json, application/xml", "application/json", pojos.get("Cardinals-FOOTBALL")},
		};
		
		for(Object[] testCase : testCases) {
			request = new MockHttpServletRequest();
			response = new MockHttpServletResponse();
			setUpRequest(testCase, request);
			servlet.handleRequest(request, response);
		}
	}
	
	private void setUpRequest(Object[] testCase, MockHttpServletRequest request) {
		String httpMethod = (String)testCase[0];
		String pathPrefix = (String)testCase[1];
		String pathInfo = (String)testCase[2];
		String queryString = (String)testCase[3];
		String parts[] = queryString.split("&");
		String acceptHeader = (String) testCase[4];
		String contentTypeHeader = (String) testCase[5];
		
		request.setServletPath("");
		request.setContextPath(pathPrefix);
		request.setPathInfo(pathInfo);
		request.setMethod(httpMethod);
		request.setQueryString(queryString);
		for(String part : parts) {
			String pieces[] = part.split("=");
			if(pieces.length > 1)
				request.addParameter(pieces[0], pieces[1]);
		}
		if(acceptHeader != null)
			request.addHeader("Accept", acceptHeader);
		if(contentTypeHeader != null)
			request.setContentType(contentTypeHeader);
		HttpMethod mthd = HttpMethod.valueOf(httpMethod);
		if(mthd == HttpMethod.POST || mthd == HttpMethod.PUT ) {
			
		}
	}
	
	private void initPojos() {
		pojos.put("Cardinals-FOOTBALL",
				new TestTeamPojo(TestTeamPojo.Type.FOOTBALL, "Cardinals"));
		pojos.put("RedSox-BASEBALL",
				new TestTeamPojo(TestTeamPojo.Type.BASEBALL, "RedSox"));
		
		pojos.get("Cardinals-FOOTBALL").setCity("Cincinatti");
		pojos.get("RedSox-BASEBALL").setCity("Boston");
	}
	
}
