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
package com.doitnext.http.router.requestdeserializers;

import java.io.InputStream;
import java.util.List;

import com.doitnext.http.router.exceptions.DeserializationException;

/**
 * Interface for request deserializers.  Request deserializers read HttpRequest bodies and return
 * java objects.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public interface RequestDeserializer {
	/**
	 * @return the request format that this deserializer supports 
	 * (such as application/json or application/xml)
	 * <p>To be selected as a deserializer for a request this value must match the Content-type of
	 * the request.
	 */
	String getRequestFormat();
	/**
	 * @return this represents the request type that this serializer supports. If the list returned is empty
	 * then it is presumed that this serializer can support any domain model type.  <p>These values are
	 * strictly used in the selection of deserializers registered with the system for the same format.</p>
	 * <p>The selection process will always choose the more constrained deserializer over the more general
	 * deserializer</p>
	 */
	List<String> getRequestTypes();
	/**
	 * @return this is used to constrain the possible classes that this serializer knows how to read.
	 * This serializer will only be used if the object to be deserialzied is one of the classes returned, 
	 * or a subclass thereof.
	 * <p>Normally this is left empty in which case the deserializer can handle any java object type.
	 */
	List<Class<?>> allowedSupertypes();
	
	
	/**
	 * This method is invoked to actually deserialize the object.
	 * 
	 * @param inputStream input stream containing the bytes to be read.
	 * @param returnType this is the actual java class to be returned.  The return value will be assigned
	 * to a method parameter and thus the object returned must be assignable to this class.
	 * @param requestType this provides detail about the domain model being deserialized.  This parameter
	 * is only required for deserializers that care about the request type.
	 * @param encoding 
	 * 			this optional parameter provides detail about the character encoding of the 
	 * data on the stream.  For deserializers that require this information it should assume that
	 * null values on this parameter indicate UTF-8
	 * @return
	 * @throws DeserializationException 
	 */
	Object deserialize(InputStream inputStream, Class<?> returnType, String requestType, String encoding) throws DeserializationException;
		
}
