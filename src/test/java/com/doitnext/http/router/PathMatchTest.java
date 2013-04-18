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
				String.class);		
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
}
