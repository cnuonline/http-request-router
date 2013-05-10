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
import java.util.ArrayList;
import java.util.List;

import com.doitnext.http.router.exceptions.DeserializationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class DefaultJsonDeserializer implements RequestDeserializer {

	private List<String> requestTypes = new ArrayList<String>();
	private List<Class<?>> allowedSuperTypes = new ArrayList<Class<?>>();
	private ObjectMapper objectMapper = new ObjectMapper();
	private String requestFormat = "application/json";
	
	public DefaultJsonDeserializer(){
		objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
	}
	
	@Override
	public String getRequestFormat() {
		return requestFormat;
	}

	@Override
	public List<String> getRequestTypes() {
		return requestTypes;
	}

	@Override
	public List<Class<?>> allowedSupertypes() {
		return allowedSuperTypes;
	}

	@Override
	public Object deserialize(InputStream inputStream, Class<?> returnType,
			String requestType, String encoding) throws DeserializationException {
		try {
			return objectMapper.readValue(inputStream, returnType);
		} catch(Exception e) {
			throw new DeserializationException(e);
		}
	}

}
