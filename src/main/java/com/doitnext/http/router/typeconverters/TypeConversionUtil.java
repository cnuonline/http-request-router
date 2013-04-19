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

import java.text.ParseException;

import com.doitnext.http.router.exceptions.TypeConversionException;

/**
 * A generic interface for data type conversion utilities.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 * @param <T> the type converted from
 */
public interface TypeConversionUtil<T> {

	/**
	 * Performs a type conversion from one type to another.
	 * 
	 * @param value the value to be converted
	 * @param classz the class of the return type
	 * @return an object of type classz that is semantically equal to value
	 * @throws ParseException
	 */
	public Object convert(T value, Class<?> classz) throws TypeConversionException;

	/**
	 * Enables the caller to determine whether the implementation supports conversions
	 * to this type. Implementors of this class should respect immutability of the class 
	 * from the time this method is called to the time that convert is called using
	 * the same classz value.
	 * 
	 * @param classz the type to test. Implementors of this class should respect immutability of the class 
	 * from the time this method is called to the time that convert is called using
	 * the same classz value.
	 * @return true if the implementation supports conversions to this type
	 */
	public boolean supports(Class<?> classz);
}