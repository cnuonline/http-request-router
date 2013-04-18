package com.doitnext.http.router;

import java.lang.reflect.Method;

import nl.jqno.equalsverifier.EqualsVerifier;

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

public class AcceptKeyTest {
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
				"T", "T", "application/JSON", "application/JSON", pt, 
				TestResourceImpl.class, implMethod,
				invoker, implInstance, successHandler, errorHandler);
	}

	@Test
	public void testConstructor() throws Exception {
		AcceptKey key = new AcceptKey(
				" application/json; encoding=UTF-8; model=http://schemas.of.mine/schema1");
		Assert.assertEquals("application/json", key.getReturnFormat());
		Assert.assertEquals("http://schemas.of.mine/schema1",
				key.getReturnType());

		key = new AcceptKey(
				" application/json; model=http://schemas.of.mine/schema1 ; encoding=UTF-8; ");
		Assert.assertEquals("application/json", key.getReturnFormat());
		Assert.assertEquals("http://schemas.of.mine/schema1",
				key.getReturnType());

		key = new AcceptKey(" application/json; encoding=UTF-8; model=");
		Assert.assertEquals("application/json", key.getReturnFormat());
		Assert.assertEquals("", key.getReturnType());

		key = new AcceptKey(" application/json;  model=;encoding=UTF-8; ");
		Assert.assertEquals("application/json", key.getReturnFormat());
		Assert.assertEquals("", key.getReturnType());

		key = new AcceptKey(
				" application/json;  model=;encoding=UTF-8; model=http://schemas.of.mine/schema1");
		Assert.assertEquals("application/json", key.getReturnFormat());
		Assert.assertEquals("", key.getReturnType());

		key = new AcceptKey(" application/json;  encoding=UTF-8; ");
		Assert.assertEquals("application/json", key.getReturnFormat());
		Assert.assertNull(key.getReturnType());

		key = new AcceptKey((String) null);
		Assert.assertNull(key.getReturnFormat());
		Assert.assertNull(key.getReturnType());
	}

	@Test
	public void testMatches1() {
		Object[][] testCases = {//
				{// 0
				null, null, null, true },//
				{// 1
				null, "application/json", null, false },//
				{// 2
				null, null, "T", false },//
				{// 3
				"*/*", "application/json", "T", true },//
				{// 4
				"*/*; model=G", "application/json", "T", false },//
				{// 5
				"*/*; model=G", "application/json", "G", true },//
				{// 6
				"application/json", "application/json", "T", true },//
				{// 7
				"application/json; model=T", "application/json", "T", true },//
				{// 8
				"application/json; model=T", "application/json", "G", false },//
				{// 9
				"application/json; model=T", "application/xml", "T", false },//
		};
		for(int x = 0; x < testCases.length; x++) {
			String caseId = String.format("Test case %d", x);
			Object[] testCase = testCases[x];
			AcceptKey ak = new AcceptKey((String) testCase[0]);
			boolean result = ak.matches((String)testCase[1], (String)testCase[2]);
			Assert.assertEquals(caseId, (Boolean)testCase[3], result);		
		}
	}
	
	@Test
	public void testMatch2() {
		AcceptKey ak = new AcceptKey("application/json; model=T");
		Assert.assertTrue(ak.matches(route));
	}
}
