package com.doitnext.http.router.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Restful Resource implementation class.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestResource {
	/**
	 * @return the URI path to the resource.
	 * @see RestMethod#template()
	 */
	String pathprefix();
	/**
	 * @return 
	 * the requestType that applies to any method not explicitly annotated with a request type.
	 * <p>The value of a request type should be the id of the schema which is used to 
	 * resolve the object model of the object sent in the request.</p>
	 * <p>For example given the http header:</p>
	 * <code><nobr>Content-Type: application/json; model=http://schemas.mycompany.com/models/account</nobr></code>
	 * <p>The requestType would be 'http://schemas.mycompany.com/models/account'</p>
	 * 
	 */
	String requestType() default "";
	/**
	 * @return the returnType that applies to any method not explicitly annotated with a request type.
	 * <p>The value of a return type should be the id of the schema which is used to 
	 * resolve the object model of the object sent in the request.</p>
	 * <p>For example given the http header:</p>
	 * <code><nobr>Accept: application/json; model=http://schemas.mycompany.com/models/account</nobr></code>
	 * <p>The returnType would be 'http://schemas.mycompany.com/models/account'</p>
	 */
	String returnType() default "";
	/** 
	 * @return the requestFormat that applies to any method not explicitly annotated with a request type.
	 * <p>The value of a request format should be an "Content-type:" header value which is used to 
	 * resolve unmarshalling strategy for object sent in the request.</p>
	 * <p>For example given the http header:</p>
	 * <code><nobr>Content-Type: application/json; model=http://schemas.mycompany.com/models/account</nobr></code>
	 * <p>The requestFormat would be 'application/json'</p>
	 */
	String requestFormat() default "";
	/**
	 * @return the returnFormat that applies to any method not explicitly annotated with a request type.
	 * <p>The value of a request format should be an "Accept:" header value which is used to 
	 * resolve marshalling strategy for object returned from the request.</p>
	 * <p>For example given the http header:</p>
	 * <code><nobr>Accept: application/json; model=http://schemas.mycompany.com/models/account</nobr></code>
	 * <p>The returnFormat would be 'application/json'</p>
	 */
	String returnFormat() default "application/json";
}
