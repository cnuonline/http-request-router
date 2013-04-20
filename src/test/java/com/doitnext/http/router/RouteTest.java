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

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.exampleclasses.TestResourceImpl;
import com.doitnext.http.router.responsehandlers.DefaultErrorHandler;
import com.doitnext.http.router.responsehandlers.DefaultSuccessHandler;
import com.doitnext.http.router.responsehandlers.ResponseHandler;
import com.doitnext.pathutils.PathTemplate;
import com.doitnext.pathutils.PathTemplateParser;

public class RouteTest {
	MethodInvoker invoker = new DefaultInvoker();
	ResponseHandler errorHandler = new DefaultErrorHandler();
	ResponseHandler successHandler = new DefaultSuccessHandler();
	TestResourceImpl implInstance = new TestResourceImpl();
	PathTemplate pt;
	PathTemplate pt2;
	Method implMethod;

	@Before
	public void init() throws Exception {
		PathTemplateParser parser = new PathTemplateParser("/", "?");
		pt = parser.parse("/teams/baseball/players?city=Atlanta");
		pt2 = parser.parse("/teams/football/players");
		implMethod = TestResourceImpl.class.getMethod("getTeam", String.class,
				String.class, String.class);
	}

	@Test
	public void testRouteConstructor() throws Exception {
		Route route = new Route(HttpMethod.OPTIONS,
				"http://fubar.schemas/baseball-team.xml",
				"http://fubar.schemas/baseball-team.json", "application/xml",
				"application/json", pt, TestResourceImpl.class, implMethod,
				invoker, implInstance, successHandler, errorHandler);
		Assert.assertEquals(HttpMethod.OPTIONS, route.getHttpMethod());
		Assert.assertEquals(implInstance, route.getImplInstance());
		Assert.assertEquals(errorHandler, route.getErrorHandler());
		Assert.assertEquals(TestResourceImpl.class, route.getImplClass());
		Assert.assertEquals(implMethod, route.getImplMethod());
		Assert.assertEquals(invoker, route.getInvoker());
		Assert.assertEquals(pt, route.getPathTemplate());
		Assert.assertEquals("application/xml", route.getRequestFormat());
		Assert.assertEquals("http://fubar.schemas/baseball-team.xml",
				route.getRequestType());
		Assert.assertEquals("application/json", route.getReturnFormat());
		Assert.assertEquals("http://fubar.schemas/baseball-team.json",
				route.getReturnType());
		Assert.assertEquals(successHandler, route.getSuccessHandler());
	}

	@Test
	public void testRoutEqualsHashCode() throws Exception {
		Route route = new Route(HttpMethod.OPTIONS,
				"http://fubar.schemas/baseball-team",
				"http://fubar.schemas/baseball-team", "application/xml",
				"application/json", pt, TestResourceImpl.class, implMethod,
				invoker, implInstance, errorHandler, successHandler);

		// Testcase layout
		// {routeB, route.equals(routeB)}
		Object[][] testCases = {
				{ // 0
						new Route(HttpMethod.OPTIONS,
								"http://fubar.schemas/baseball-team",
								"http://fubar.schemas/baseball-team",
								"application/xml", "application/json", pt,
								TestResourceImpl.class, implMethod, invoker,
								implInstance, errorHandler, successHandler), //
						0, 0 //
				},//
				{// 1
						new Route(HttpMethod.OPTIONS,
								"http://fubar.schemas/baseball-team",
								"http://fubar.schemas/baseball-team", "gg/gg",
								"application/json", pt, TestResourceImpl.class,
								implMethod, invoker, implInstance,
								errorHandler, successHandler), //
						-6, 6 //
				},//
				{// 2
						new Route(HttpMethod.OPTIONS,
								"http://fubar.schemas/baseball-team",
								"http://fubar.schemas/baseball-team", null,
								"application/json", pt, TestResourceImpl.class,
								implMethod, invoker, implInstance,
								errorHandler, successHandler), //
						-1, 1 //
				},//
				{// 3
						new Route(HttpMethod.OPTIONS,
								"http://fubar.schemas/baseball-team",
								"http://fubar.schemas/baseball-team",
								"application/xml", "gg/gg", pt,
								TestResourceImpl.class, implMethod, invoker,
								implInstance, errorHandler, successHandler), //
						-6, 6 //
				},//
				{// 4
						new Route(HttpMethod.OPTIONS,
								"http://fubar.schemas/baseball-team",
								"http://fubar.schemas/baseball-team",
								"application/xml", null, pt,
								TestResourceImpl.class, implMethod, invoker,
								implInstance, errorHandler, successHandler), //
						-1, 1 //
				},//
				{// 5
						new Route(HttpMethod.OPTIONS,
								"http://fubar.schemas/baseball-team",
								"ggg/ggg", "application/xml",
								"application/json", pt, TestResourceImpl.class,
								implMethod, invoker, implInstance,
								errorHandler, successHandler), //
						1, -1 //
				}, //
				{// 6
						new Route(HttpMethod.OPTIONS,
								"http://fubar.schemas/baseball-team", null,
								"application/xml", "application/json", pt,
								TestResourceImpl.class, implMethod, invoker,
								implInstance, errorHandler, successHandler), //
						-1, 1 //
				}, //
				{// 7
						new Route(HttpMethod.OPTIONS, "ggg/ggg",
								"http://fubar.schemas/baseball-team",
								"application/xml", "application/json", pt,
								TestResourceImpl.class, implMethod, invoker,
								implInstance, errorHandler, successHandler), //
						1, -1 //
				}, //
				{// 8
						new Route(HttpMethod.OPTIONS, null,
								"http://fubar.schemas/baseball-team",
								"application/xml", "application/json", pt,
								TestResourceImpl.class, implMethod, invoker,
								implInstance, errorHandler, successHandler), //
						-1, 1 //
				}, //
				{// 9
						new Route(HttpMethod.OPTIONS,
								"http://fubar.schemas/baseball-team",
								"http://fubar.schemas/baseball-team",
								"application/xml", "application/json", null,
								TestResourceImpl.class, implMethod, invoker,
								implInstance, errorHandler, successHandler), //
						-1, 1 //
				}, //
				{// 10
						new Route(HttpMethod.OPTIONS,
								"http://fubar.schemas/baseball-team",
								"http://fubar.schemas/baseball-team",
								"application/xml", "application/json", pt2,
								TestResourceImpl.class, implMethod, invoker,
								implInstance, errorHandler, successHandler), //
						-4, 4 //
				}, //
				{// 11
						new Route(null, "http://fubar.schemas/baseball-team",
								"http://fubar.schemas/baseball-team",
								"application/xml", "application/json", pt,
								TestResourceImpl.class, implMethod, invoker,
								implInstance, errorHandler, successHandler), //
						-1, 1 //
				} //
		};

		for (int x = 0; x < testCases.length; x++) {
			Object[] testCase = testCases[x];
			String caseId = String.format("Test case %d", x);
			Route routeB = (Route) testCase[0];
			int expectedCompareA = (Integer) testCase[1];
			int expectedCompareB = (Integer) testCase[2];
			Assert.assertEquals(caseId, expectedCompareA,
					route.compareTo(routeB));
			Assert.assertEquals(caseId, expectedCompareB,
					routeB.compareTo(route));

			Assert.assertEquals(caseId, 0, routeB.compareTo(routeB));

		}
	}
}
