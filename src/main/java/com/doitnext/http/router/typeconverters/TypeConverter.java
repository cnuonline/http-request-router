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


/**
 * Interface for converting a String to another type.
 * 
 * @author Stephen Owens (steve@doitnext.com)
 *
 * @param <T> the from type that this converter supports
 */
public interface TypeConverter<T> {
	/**
	 * Converts the input value of type T into another data type representing the
	 * same value.  Data type of the object returned is up to the implemetor of
	 * this interface and as such this interface is designed to be used within the
	 * context of a TypeConversionUtil implementation.
	 * 
	 * @param value the input value to be converted
	 * @return an object representing the value but of a data type of the implementations choosing.
	 * @throws Exception
	 */
	Object convert(T value) throws Exception;
}
