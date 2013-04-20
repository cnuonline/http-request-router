package com.doitnext.http.router;

import org.junit.Test;
import org.junit.Assert;

import com.doitnext.http.router.MethodInvoker.InvokeResult;
/**
 * Due dilligence tests on InvokeResult enum.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class MethodInvoker_InvokeResultTest {

	@Test
	public void testValues(){
		for(InvokeResult r : InvokeResult.values()) {
			Assert.assertEquals(r, InvokeResult.valueOf(r.name()));
		}
	}
}
