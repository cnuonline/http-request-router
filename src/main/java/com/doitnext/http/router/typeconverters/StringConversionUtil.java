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
package com.doitnext.http.router.typeconverters;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.doitnext.http.router.exceptions.UnsupportedConversionException;

/**
 * Converts strings to a known set of convertible types.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class StringConversionUtil {

	Map<Class<?>, TypeConverter> converters = new HashMap<Class<?>,TypeConverter>();
	public StringConversionUtil() {
		converters.put(int.class, new TypeConverter(){
			@Override
			public Object convert(String value) {
				return Integer.parseInt(value);
			}
		});
		converters.put(long.class, new TypeConverter(){
			@Override
			public Object convert(String value) {
				return Long.parseLong(value);
			}
		});
		converters.put(boolean.class, new TypeConverter(){
			@Override
			public Object convert(String value) {
				return Boolean.parseBoolean(value);
			}
		});
		converters.put(byte.class, new TypeConverter(){
			@Override
			public Object convert(String value) {
				return Byte.parseByte(value);
			}
		});

		converters.put(short.class, new TypeConverter(){
			@Override
			public Object convert(String value) {
				return Short.parseShort(value);
			}
		});
		converters.put(float.class, new TypeConverter(){
			@Override
			public Object convert(String value) {
				return Float.parseFloat(value);
			}
		});
		converters.put(double.class, new TypeConverter(){
			@Override
			public Object convert(String value) {
				return Double.parseDouble(value);
			}
		});
		converters.put(char.class, new TypeConverter(){
			@Override
			public Object convert(String value) {
				if(value.length() != 1)
					throw new IndexOutOfBoundsException(String.format("'%s' not convertible to char", value));
				return value.charAt(0);
			}
		});
		converters.put(String.class, new TypeConverter(){
			@Override
			public Object convert(String value) throws ParseException {
				return value;
			}
		});
		converters.put(Date.class, new TypeConverter(){
			DateFormat formats[] = {
					DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL),
					DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG),
					DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM),
					DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT),
					
					DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL),
					DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG),
					DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM),
					DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT),
					
					DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL),
					DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG),
					DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM),
					DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT),

					DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL),
					DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG),
					DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM),
					DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT),

					DateFormat.getDateInstance(DateFormat.FULL),
					DateFormat.getDateInstance(DateFormat.LONG),
					DateFormat.getDateInstance(DateFormat.MEDIUM),
					DateFormat.getDateInstance(DateFormat.SHORT),
					
					DateFormat.getTimeInstance(DateFormat.FULL),
					DateFormat.getTimeInstance(DateFormat.LONG),
					DateFormat.getTimeInstance(DateFormat.MEDIUM),
					DateFormat.getTimeInstance(DateFormat.SHORT)
				};
			@Override
			public Object convert(String value) throws ParseException {
				for(int x = 0; x < formats.length; x++) {
					try {
						return formats[x].parse(value);
					} 
					catch(ParseException pe){}
				}
				throw new ParseException(String.format("Unable to parse '%s' as Date", value), 0);
			}
		});

	}
	
	public Object convert(String value, Class<?> classz) throws ParseException {
		if(converters.containsKey(classz)) {
			return converters.get(classz).convert(value);
		} else {
			throw new UnsupportedConversionException(String.class, classz);
		}
	}

}
