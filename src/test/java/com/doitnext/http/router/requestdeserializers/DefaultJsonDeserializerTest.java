package com.doitnext.http.router.requestdeserializers;

import org.junit.Test;
import org.junit.Assert;

import com.doitnext.http.router.exceptions.DeserializationException;

/**
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class DefaultJsonDeserializerTest {

	@Test
	public void testGettersAndSetters() {
		DefaultJsonDeserializer ds = new DefaultJsonDeserializer();
		Assert.assertTrue(ds.getRequestTypes().isEmpty());
		Assert.assertTrue(ds.allowedSupertypes().isEmpty());
	}
	
	@Test(expected=DeserializationException.class)
	public void testNullInputStream() throws DeserializationException {
		DefaultJsonDeserializer ds = new DefaultJsonDeserializer();
		ds.deserialize(null, null, null, null);
	}
}
