package com.doitnext.http.router.typeconverters;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;

import org.junit.Test;

import com.doitnext.http.router.exceptions.UnsupportedConversionException;

public class StringConversionUtilTest {

	public StringConversionUtilTest() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testPrimitives() throws Exception {
		StringConversionUtil converter = new StringConversionUtil();
		int df = DateFormat.FULL;
		DateFormat dateFormat = DateFormat.getDateTimeInstance(df,df);
		Date now = new Date();
		// Test Case Layout
		// {expected value, expected class, expectedException, inputValue}
		Object[][] testCases = {
				{ (int)3 , int.class, null, "3", null },
				{ (long)5, long.class, null, "5", null },
				{ (float)3.0 , float.class, null, "3.0", null },
				{ (double)5.0, double.class, null, "5.0", null },
				{ (boolean)true, boolean.class, null, "true", null },
				{ dateFormat.parse(dateFormat.format(now)), Date.class, null, dateFormat.format(now), null },
				{ "foo", String.class, null, "foo", null},
				{ 'c', char.class, null, "c", null},
				{ (short)2, short.class, null, "2", null},
				{ (byte)0x20, byte.class, null, "32", null},
				{ null, char.class, IndexOutOfBoundsException.class, "hi", "'hi' not convertible to char"},
				{ null, Date.class, ParseException.class, "hey fred", "Unable to parse 'hey fred' as Date"},
				{ null, StringBuffer.class, UnsupportedConversionException.class, "Hi", "Unable to convert from java.lang.String to java.lang.StringBuffer."}
		};
		
		for(int x = 0; x < testCases.length; x++) {
			Object testCase[] = testCases[x];
			try {
				Object result = converter.convert((String)testCase[3], (Class<?>)testCase[1]);
				Assert.assertNull("Expected an exception one was not thrown.", testCase[2]);
				if(result instanceof Date){
					Date expected = (Date) testCase[0];
					Date actual = (Date) result;
					Assert.assertEquals("Date Compare", 0 , expected.compareTo(actual));
				} else
					Assert.assertEquals(testCase[0], result);
			} catch(Throwable t) {
				Assert.assertEquals(testCase[2], t.getClass());
				Assert.assertEquals(testCase[4], t.getMessage());
			}
		}
	}
}
