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
package com.doitnext.http.router.exceptions;

/**
 * This exception is thrown when there is an error performing a type conversion 
 * from one data type to another.
 * 
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
public class TypeConversionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 107299195229275323L;

	public TypeConversionException() {
		super();
	}

	public TypeConversionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public TypeConversionException(String arg0) {
		super(arg0);
	}

	public TypeConversionException(Throwable arg0) {
		super(arg0);
	}

}
