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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.doitnext.http.router.MethodInvoker.InvokeResult;
import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.exampleclasses.TestResourceImpl;
import com.doitnext.http.router.exampleclasses.TestTeamPojo;
import com.doitnext.http.router.responsehandlers.ResponseHandler;
import com.doitnext.pathutils.IdentifierTemplate;
import com.doitnext.pathutils.LiteralTemplate;
import com.doitnext.pathutils.Path;
import com.doitnext.pathutils.PathElementTemplate;

public class DefaultInvokerTest {
	static Random rnd = new Random();

	static final String cities[] = { "Los Angeles", "El Paso", "Hartford",
			"Dallas", "Tampa", "Daytona", "Seattle", "Tacoma" };

	static final String teamNames[] = { "Rockin Racoons", "Ball Busters",
			"Bears", "Cubs", "Badgers", "Eagles", "Hounds", "Wolverines",
			"Knights", "Cardinals", "Red Sox", "Nicks" };

	static final String leagues[] = { "DSL", "ADSL", "NFL", "WTFO", "FTN" };

	static final String teamTypes[] = {
			// Valid team types
			"BASEBALL", "FOOTBALL", "BASKETBALL", "ROLLERDERBY", "LACROSSE",
			"SWIM",

			// Invalid team types
			"ACROBATIC" };

	static final String contentTypes[] = { "application/json", // Known
			"application/xml", // Unknown
			"application/json" // Known
	};

	private TestResourceImpl testResourceImpl;
	ResponseHandler errorHandlerJson = mock(ResponseHandler.class);
	ResponseHandler successHandlerJson = mock(ResponseHandler.class);
	MethodInvoker invoker;
	ObjectMapper mapper = new ObjectMapper();
	List<PathMatch> happyCaseTestPathMatches = new ArrayList<PathMatch>();
	List<PathMatch> queryParamTestPathMatches = new ArrayList<PathMatch>();
	Map<String, PathMatch> namedPathMatches = new HashMap<String,PathMatch>();
	Map<String, Route> routesByName = new HashMap<String, Route>();
	SortedSet<Route> routes;

	enum QueryParamType {
		city(new RandomValueGenerator() {
			@Override
			public String getRandomValue() {
				return cities[rnd.nextInt(cities.length)];
			}
		}), teamName(new RandomValueGenerator() {
			@Override
			public String getRandomValue() {
				return teamNames[rnd.nextInt(teamNames.length)];
			}
		}), teamType(new RandomValueGenerator() {
			@Override
			public String getRandomValue() {
				return teamTypes[rnd.nextInt(teamTypes.length)];
			}
		});
		private final RandomValueGenerator generator;

		private QueryParamType(RandomValueGenerator generator) {
			this.generator = generator;
		}

		public String getRandomValue() {
			return generator.getRandomValue();
		}

		static interface RandomValueGenerator {
			String getRandomValue();
		}
	};

	@Before
	public void init() throws Exception {
		DefaultEndpointResolver resolver = new DefaultEndpointResolver();
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		resolver.setApplicationContext(applicationContext);
		testResourceImpl = new TestResourceImpl();

		when(
				applicationContext.getBean("testResource1",
						TestResourceImpl.class)).thenReturn(testResourceImpl);

		invoker = new DefaultInvoker();

		Map<MethodReturnKey, ResponseHandler> errorHandlers = new HashMap<MethodReturnKey, ResponseHandler>();
		Map<MethodReturnKey, ResponseHandler> successHandlers = new HashMap<MethodReturnKey, ResponseHandler>();

		errorHandlers.put(new MethodReturnKey("", "application/json"),
				errorHandlerJson);
		errorHandlers.put(new MethodReturnKey("", "text/plain"),
				errorHandlerJson);
		successHandlers.put(new MethodReturnKey("", "application/json"),
				successHandlerJson);
		successHandlers.put(new MethodReturnKey("", ""), successHandlerJson);

		resolver.setErrorHandlers(errorHandlers);
		resolver.setSuccessHandlers(successHandlers);
		resolver.setMethodInvoker(invoker);

		when(
				errorHandlerJson.handleResponse(any(PathMatch.class),
						any(HttpServletRequest.class),
						any(HttpServletResponse.class), any(Throwable.class)))
				.thenReturn(true);
		when(
				successHandlerJson.handleResponse(any(PathMatch.class),
						any(HttpServletRequest.class),
						any(HttpServletResponse.class), any(Object.class)))
				.thenReturn(true);

		routes = resolver.resolveEndpoints("/sports-api",
				"com.doitnext.http.router.exampleclasses");

		for(Route route : routes){
			routesByName.put(route.getImplMethod().getName(), route);
		}
		
		for (int x = 0; x < 100; x++) {
			for (Route route : routes) {
				if(route.getImplMethod().getName().startsWith("bad"))
					continue;
				String pathString = createRandomPath(route);
				Path path = route.getPathTemplate().match(pathString);
				if (path != null)
					happyCaseTestPathMatches.add(new PathMatch(route, path));
			}
		}
		
		// Now set up PathMatches for QueryParam testing
		Route route = routesByName.get("getTeams");
		String[] testUris = {
			"/sports-api/teams?city=Dallas",
			"/sports-api/teams?city=Dallas&teamType=FOOTBALL",
			"/sports-api/teams?city=Dallas&teamType=FOOTBALL&teamName=Cardinals",
			"/sports-api/teams?city=Dallas&bogusBooger",
			"/sports-api/teams?city=Dallas&city=Los+Angeles&city=Detroit",
			
		};
		for(String testUri : testUris) {
			Path path = route.getPathTemplate().match(testUri);
			Assert.assertNotNull(path);
			queryParamTestPathMatches.add(new PathMatch(route, path));
		}
	}

	@Test
	public void testGettersAndSetters() {
		DefaultInvoker defaultInvoker = new DefaultInvoker();
		defaultInvoker.setRequestDeserializers(null);
		Assert.assertNull(defaultInvoker.getRequestDeserializers());
		defaultInvoker.setStringConverter(null);
		Assert.assertNull(defaultInvoker.getStringConverter());
	}
	
	
	@Test
	public void testInvokeHappyCases() throws Exception {
		for (PathMatch pm : happyCaseTestPathMatches) {
			HttpMethod method = pm.getRoute().getHttpMethod();
			HttpServletRequest req = createHappyMockRequest(method, pm);
			HttpServletResponse resp = new MockHttpServletResponse();
			executePathMatchTest(createPathMatchTestCaseId(pm),pm, req, resp);
		}
	}

	@Test
	public void testQueryParamHandling() throws Exception {
		for(PathMatch pm : queryParamTestPathMatches) {
			HttpMethod method = pm.getRoute().getHttpMethod();
			HttpServletRequest req = createHappyMockRequest(method, pm);
			HttpServletResponse resp = new MockHttpServletResponse();
			executePathMatchTest(createPathMatchTestCaseId(pm),pm, req, resp);
		}
	}
	
	
	@Test(expected=ServletException.class)
	public void testBadTerminusArg() throws Exception {
		Route route = routesByName.get("badTerminusArg");
		Path path = route.getPathTemplate().match("/sports-api/teams/badTerminusArg?howdy=partner");
		Assert.assertNotNull(path);
		PathMatch pm = new PathMatch(route,path);
		HttpMethod method = route.getHttpMethod();
		HttpServletRequest req = createHappyMockRequest(method, pm);
		HttpServletResponse resp = new MockHttpServletResponse();
		executePathMatchTest(createPathMatchTestCaseId(pm),pm, req, resp);
	}

	@Test
	public void testRawCall() throws Exception {
		Route route = routesByName.get("rawCall");
		Path path = route.getPathTemplate().match("/sports-api/teams/rawCall?query=String");
		Assert.assertNotNull(path);
		PathMatch pm = new PathMatch(route,path);
		HttpMethod method = route.getHttpMethod();
		HttpServletRequest req = createHappyMockRequest(method, pm);
		HttpServletResponse resp = new MockHttpServletResponse();
		executePathMatchTest(createPathMatchTestCaseId(pm),pm, req, resp);
	}

	private String createPathMatchTestCaseId(PathMatch pm) {
		return String.format("%s %s --> %s.%s", pm.getRoute().getHttpMethod().name(),
				pm.getMatchedPath().getGivenPath(), pm.getRoute()
						.getImplClass().getName(), pm.getRoute()
						.getImplMethod().getName());
	}
	
	private InvokeResult executePathMatchTest(String testCaseId, 
			PathMatch pm, HttpServletRequest req, 
			HttpServletResponse resp) throws ServletException {
		InvokeResult result = null;
		HttpMethod method = pm.getRoute().getHttpMethod();
		result = invoker.invokeMethod(method, pm, req, resp);

		Assert.assertTrue(result.handled);
		if (result.success) {
			Assert.assertFalse(testCaseId, StringUtils
					.isEmpty(testResourceImpl.getLastHttpMethodCalled()));
			HttpMethod methodCalled = HttpMethod.valueOf(testResourceImpl
					.getLastHttpMethodCalled());
			Assert.assertEquals(testCaseId, pm.getRoute().getHttpMethod(),
					methodCalled);
			Assert.assertEquals(testCaseId, pm.getRoute().getImplMethod()
					.getName(), testResourceImpl.getLastMethodCalled());
			verify(successHandlerJson).handleResponse(eq(pm), eq(req),
					eq(resp), any(TestTeamPojo.class));
		} else {
			verify(errorHandlerJson).handleResponse(eq(pm), eq(req),
					eq(resp), any(Throwable.class));
		}
		
		return result;
	}
	
	private HttpServletRequest createHappyMockRequest(HttpMethod method, PathMatch pm)
			throws JsonGenerationException, JsonMappingException, IOException {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.setMethod(method.name());
		if (method == HttpMethod.POST || method == HttpMethod.PUT) {
			TestTeamPojo pojo = createRandomPojo();
			req.setContentType("application/json");
			req.setContent(mapper.writeValueAsBytes(pojo));
		}
		String terminus = pm.getMatchedPath().getTerminus();
		if(!StringUtils.isEmpty(terminus)){
			req.setQueryString(terminus);
			String parts[] = req.getQueryString().split("&");
			for(String part : parts) {
				if(!StringUtils.isEmpty(part)){
					String pieces[] = part.split("=");
					if(!StringUtils.isEmpty(pieces[0])) {
						String key = pieces[0];
						String value = "";
						if(pieces.length > 1){
							value = pieces[1].trim();
						}
						req.addParameter(key, value);
					}
				}
			}
		}
		return req;
	}

	private String createRandomPath(Route route) {
		StringBuilder pb = new StringBuilder();
		for (int x = 0; x < route.getPathTemplate().getLength(); x++) {
			PathElementTemplate matcher = route.getPathTemplate().getMatcher(x);
			pb.append("/");
			if (matcher instanceof LiteralTemplate) {
				pb.append(matcher.getName());

			} else {
				IdentifierTemplate t = (IdentifierTemplate) matcher;
				if (t.getName().equals("teamType")) {
					pb.append(teamTypes[rnd.nextInt(teamTypes.length)]);
				} else if (t.getName().equals("teamName")) {
					pb.append(teamNames[rnd.nextInt(teamNames.length)]);
				}
			}
		}

		// Append zero to n random query params
		List<QueryParamType> qpTypes = new ArrayList<QueryParamType>();
		qpTypes.addAll(Arrays.asList(QueryParamType.values()));
		for (int x = 1; x < rnd.nextInt(qpTypes.size()); x++) {
			if (x == 1)
				pb.append("?");
			else
				pb.append("&");

			int y = rnd.nextInt(qpTypes.size());
			QueryParamType qpType = qpTypes.get(y);
			qpTypes.remove(y);

			pb.append(qpType.name());
			pb.append("=");
			pb.append(qpType.getRandomValue());
		}
		return pb.toString();
	}

	private TestTeamPojo createRandomPojo() {
		TestTeamPojo pojo = new TestTeamPojo(
				TestTeamPojo.Type.values()[rnd.nextInt(TestTeamPojo.Type
						.values().length)],
				this.teamNames[rnd.nextInt(this.teamNames.length)]);
		pojo.setCity(cities[rnd.nextInt(cities.length)]);
		pojo.setLeague(leagues[rnd.nextInt(leagues.length)]);
		return pojo;
	}

}
