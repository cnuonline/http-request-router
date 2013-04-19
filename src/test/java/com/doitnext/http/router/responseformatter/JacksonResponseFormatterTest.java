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
package com.doitnext.http.router.responseformatter;

import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

public class JacksonResponseFormatterTest {

	private static class TestPojo {
		public String strVal;
		public Date dateVal;
		public boolean boolVal;
		public long longVal;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (boolVal ? 1231 : 1237);
			result = prime * result
					+ ((dateVal == null) ? 0 : dateVal.hashCode());
			result = prime * result + (int) (longVal ^ (longVal >>> 32));
			result = prime * result
					+ ((strVal == null) ? 0 : strVal.hashCode());
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestPojo other = (TestPojo) obj;
			if (boolVal != other.boolVal)
				return false;
			if (dateVal == null) {
				if (other.dateVal != null)
					return false;
			} else if (!dateVal.equals(other.dateVal))
				return false;
			if (longVal != other.longVal)
				return false;
			if (strVal == null) {
				if (other.strVal != null)
					return false;
			} else if (!strVal.equals(other.strVal))
				return false;
			return true;
		}
		
		
	}
	
	private TestPojo testPojo;
	
	public JacksonResponseFormatterTest() {
		testPojo = new TestPojo();
		testPojo.strVal = "Happy";
		testPojo.dateVal = new Date();
		testPojo.longVal = 42l;
		testPojo.boolVal = true;
	}
	
	@Test
	public void testToString() throws Exception {
		ResponseFormatter formatter = new JacksonResponseFormatter();
		String json = formatter.formatResponse(testPojo, null, null);
		ObjectMapper mapper = new ObjectMapper();
		TestPojo actual = mapper.readValue(json, TestPojo.class);
		Assert.assertEquals(testPojo, actual);
	}

	@Test
	public void testToBytes() throws Exception {
		ResponseFormatter formatter = new JacksonResponseFormatter();
		byte json[] = formatter.formatResponseUtf8(testPojo, null, null);
		ObjectMapper mapper = new ObjectMapper();
		TestPojo actual = mapper.readValue(json, TestPojo.class);
		Assert.assertEquals(testPojo, actual);		
	}

}
