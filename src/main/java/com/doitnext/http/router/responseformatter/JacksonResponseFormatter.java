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

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Used to format an object into JSON a simple POJO.  This particular implementation
 * disregards the schemaUri and templateUri arguments to {@link ResponseFormatter#formatResponse(Object, URI, URI)}
 * 
 * <p>This class is thread safe</p>
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class JacksonResponseFormatter implements ResponseFormatter {
	Logger logger = LoggerFactory.getLogger(JacksonResponseFormatter.class);
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	 
	public JacksonResponseFormatter() {
		objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
	}

	@Override
	public String formatResponse(Object response, URI schemaUri, URI templateUri) throws Exception {
		return objectMapper.writeValueAsString(response);
	}

	@Override
	public byte[] formatResponseUtf8(Object response, URI schemaUri, URI templateUri) throws Exception {
		return objectMapper.writeValueAsBytes(response);
	}

}
