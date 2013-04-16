package com.doitnext.http.router.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to provide information on how the {@link RestRouterServlet} should handle
 * an exception thrown from a {@link RestMethod} endpoint.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandler {
	Class<?> returnFormatter();
	
}
