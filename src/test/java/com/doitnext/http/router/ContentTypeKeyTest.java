package com.doitnext.http.router;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.exampleclasses.TestResourceImpl;
import com.doitnext.http.router.responsehandlers.DefaultErrorHandler;
import com.doitnext.http.router.responsehandlers.DefaultSuccessHandler;
import com.doitnext.http.router.responsehandlers.ResponseHandler;
import com.doitnext.pathutils.Path;
import com.doitnext.pathutils.PathTemplate;
import com.doitnext.pathutils.PathTemplateParser;

public class ContentTypeKeyTest {
	MethodInvoker invoker = new DefaultInvoker();
	ResponseHandler errorHandler = new DefaultErrorHandler();
	ResponseHandler successHandler = new DefaultSuccessHandler();
	TestResourceImpl implInstance = new TestResourceImpl();
	PathTemplate pt;
	PathTemplate pt2;
	Method implMethod;
	Path path;
	Route route;
	Route routeWildcardFormat;
	Route routeNullFormat;
	Route routeNullType;

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
		routeWildcardFormat = new Route(HttpMethod.OPTIONS,
				"http://fubar.schemas/baseball-team",
				"http://fubar.schemas/baseball-team.json", "*/*",
				"application/json", pt, TestResourceImpl.class, implMethod,
				invoker, implInstance, successHandler, errorHandler);

		routeNullFormat = new Route(HttpMethod.OPTIONS,
				"http://fubar.schemas/baseball-team",
				"http://fubar.schemas/baseball-team.json", null,
				"application/json", pt, TestResourceImpl.class, implMethod,
				invoker, implInstance, successHandler, errorHandler);

		routeNullType = new Route(HttpMethod.OPTIONS,
				null,
				"http://fubar.schemas/baseball-team.json", "*/*",
				"application/json", pt, TestResourceImpl.class, implMethod,
				invoker, implInstance, successHandler, errorHandler);

	}

	@Test
	public void testConstructor() throws Exception {
		ContentTypeKey key = new ContentTypeKey(
				" application/json; encoding=UTF-8; model=http://schemas.of.mine/schema1");
		Assert.assertEquals("application/json", key.getRequestFormat());
		Assert.assertEquals("http://schemas.of.mine/schema1",
				key.getRequestType());

		key = new ContentTypeKey(
				" application/json; model=http://schemas.of.mine/schema1 ; encoding=UTF-8; ");
		Assert.assertEquals("application/json", key.getRequestFormat());
		Assert.assertEquals("http://schemas.of.mine/schema1",
				key.getRequestType());

		key = new ContentTypeKey(" application/json; encoding=UTF-8; model=");
		Assert.assertEquals("application/json", key.getRequestFormat());
		Assert.assertEquals("", key.getRequestType());

		key = new ContentTypeKey(" application/json;  model=;encoding=UTF-8; ");
		Assert.assertEquals("application/json", key.getRequestFormat());
		Assert.assertEquals("", key.getRequestType());

		key = new ContentTypeKey(
				" application/json;  model=;encoding=UTF-8; model=http://schemas.of.mine/schema1");
		Assert.assertEquals("application/json", key.getRequestFormat());
		Assert.assertEquals("", key.getRequestType());

		key = new ContentTypeKey(" application/json;  encoding=UTF-8; ");
		Assert.assertEquals("application/json", key.getRequestFormat());
		Assert.assertNull(key.getRequestType());

		key = new ContentTypeKey((String) null);
		Assert.assertNull(key.getRequestFormat());
		Assert.assertNull(key.getRequestType());
	}

	@Test
	public void testMatches() throws Exception {
		Object testCases[][] = { 
				{//0
					route,"application/xml; model=http://fubar.schemas/baseball-team.xml",
				true },
				{//1
					route,"application/json; model=http://fubar.schemas/baseball-team.xml",
				false },
				{//2
					route,"application/xml; model=http://fubar.schemas/baseball-team.jsonl",
				false },
				{//3
					route,"application/xml;",
				false },
				{//4
					route,null,
				false },
				{//5
					routeWildcardFormat,"application/hhh; model=http://fubar.schemas/baseball-team",
				true },
				{//6
					routeNullFormat,null,
				true },
				{//7
					routeWildcardFormat,"application/hhh; model=http://fubar.schemas/baseball-team.fff",
				false },
				{//8
					routeWildcardFormat,"application/hhh;",
				false },
		};
		for(int x = 0; x < testCases.length; x++) {
			String caseId = String.format("Testcase %d", x);
			ContentTypeKey key = new ContentTypeKey((String)testCases[x][1]);
			boolean matches = key.matches((Route)testCases[x][0]);
			Assert.assertEquals(caseId, (Boolean)testCases[x][2], matches);
		}
	}
}
