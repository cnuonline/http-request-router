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
