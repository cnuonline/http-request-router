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
package com.doitnext.http.router.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.doitnext.http.router.annotations.enums.HttpMethod;

/**
 * Annotation used to identify a method that implements a single endpoint.
 * Typically the terms Resource.Method and endpoint are synonymous in this context.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestMethod {
	/**
	 * @return the URI template associated with this method.  
	 * <p>Good restful design would only apply this to methods that need specific path parameters
	 * in a resource.  For example '/{id:[0-9]{5,10}:ALERTID}' might be used as a template on the GET, DELETE, and PUT
	 *  method of a resource annotated with a pathPrefix of '/users'. But on the POST method you might
	 *  leave the template blank</p>
	 *  <p>Another restful use case for the method.template is in the case of Controller methods</p>
	 *  <p>For example take the controller method POST: '.../alerts/{id:[0-9]{5,10}:ALERTID}/resend' on an 'alerts' collection
	 *  the method template would be '/{id:[0-9]{5,10}:ALERTID}/resend' and the resource template would be '.../alerts'.</p>
	 */
	String template();
	/**
	 * @return the HTTP method that this method handles (e.g. DELETE, GET, HEAD, OPTIONS, POST, PUT, TRACE)
	 */
	HttpMethod method();
	/**
	 * @return
	 * the requestType that applies to this method.
	 * <p>The value of a request type should be the id of the schema which is used to 
	 * resolve the object model of the object sent in the request.</p>
	 * <p>For example given the http header:</p>
	 * <code><nobr>Content-Type: application/json; model=http://schemas.mycompany.com/models/account</nobr></code>
	 * <p>The requestType would be 'http://schemas.mycompany.com/models/account'</p>
	 */
	String requestType() default "";
	/**
	 * @return the returnType that applies this method.
	 * <p>The value of a return type should be the id of the schema which is used to 
	 * resolve the object model of the object sent in the request.</p>
	 * <p>For example given the http header:</p>
	 * <code><nobr>Accept: application/json; model=http://schemas.mycompany.com/models/account</nobr></code>
	 * <p>The returnType would be 'http://schemas.mycompany.com/models/account'</p>
	 */
	String returnType() default "";
	/**
	 * @return the requestFormat that applies to this method.
	 * <p>The value of a request format should be an "Content-type:" header value which is used to 
	 * resolve unmarshalling strategy for object sent in the request.</p>
	 * <p>For example given the http header:</p>
	 * <code><nobr>Content-Type: application/json; model=http://schemas.mycompany.com/models/account</nobr></code>
	 * <p>The requestFormat would be 'application/json'</p>
	 */
	String requestFormat() default "";
	/**
	 * @return the returnFormat that applies to this method.
	 * <p>The value of a request format should be an "Accept:" header value which is used to 
	 * resolve marshalling strategy for object returned from the request.</p>
	 * <p>For example given the http header:</p>
	 * <code><nobr>Accept: application/json; model=http://schemas.mycompany.com/models/account</nobr></code>
	 * <p>The returnFormat would be 'application/json'</p>
	 */
	String returnFormat() default "";
}
