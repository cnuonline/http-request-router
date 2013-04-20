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
import com.doitnext.pathutils.Path;
import com.doitnext.pathutils.PathTemplate;
import com.doitnext.pathutils.PathTemplateParser;

public class PathMatchTest {
	MethodInvoker invoker = new DefaultInvoker();
	ResponseHandler errorHandler = new DefaultErrorHandler();
	ResponseHandler successHandler = new DefaultSuccessHandler();
	TestResourceImpl implInstance = new TestResourceImpl();
	PathTemplate pt;
	PathTemplate pt2;
	Method implMethod;
	Path path;
	Route route;
	
	@Before
	public void init() throws Exception {
		PathTemplateParser parser = new PathTemplateParser("/", "?");
		pt = parser.parse("/teams/baseball/players?pageSize=30&page=4");
		pt2 = parser.parse("/teams/football/players");
		implMethod = TestResourceImpl.class.getMethod("getTeam", String.class,
				String.class, String.class);		
		path = pt.match("/teams/baseball/players");
		route = new Route(HttpMethod.OPTIONS,
				"http://fubar.schemas/baseball-team.xml",
				"http://fubar.schemas/baseball-team.json", "application/xml",
				"application/json", pt, TestResourceImpl.class, implMethod,
				invoker, implInstance, successHandler, errorHandler);

	}
	@Test
	public void testConstructor() {
		PathMatch pm = new PathMatch(route, path);
		Assert.assertEquals(route, pm.getRoute());
		Assert.assertEquals(path, pm.getMatchedPath());
	}
	
	@Test
	public void testToString() {
		PathMatch pm = new PathMatch(route, path);
		String pmAsString = pm.toString();

		StringBuilder sb = new StringBuilder("{\"PathMatch\": {\"route\":\"{OPTIONS: \"/teams/baseball/players\",");
		sb.append(" ReturnFormat: \"application/json; ReturnType:\"http://fubar.schemas/baseball-team.json\",");
		sb.append(" RequestFormat: \"application/xml\", RequestType: \"http://fubar.schemas/baseball-team.xml\"}");
		sb.append(" --> [com.doitnext.http.router.exampleclasses.TestResourceImpl.getTeam(String, String, String)]\", ");
		sb.append("matchedPath\":\"{\"Path\": {\"givenPath\":\"/teams/baseball/players\", terminus\":\"null}}");
		Assert.assertEquals(sb.toString(), pmAsString);
	}
}
