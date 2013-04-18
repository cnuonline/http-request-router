package com.doitnext.http.router;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test MethodReturnKey object
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class MethodReturnKeyTest {

	@Before
	public void init() throws Exception {

	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(MethodReturnKey.class).verify();
	}

	@Test
	public void testEnpointResolverConstructor() {
		MethodReturnKey mrk = new MethodReturnKey("type", "format");
		Assert.assertEquals("type", mrk.getReturnType());
		Assert.assertEquals("format", mrk.getReturnFormat());
	}
}
