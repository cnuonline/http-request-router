package com.doitnext.http.router.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation identifies a parameter in a {@link RestMethod} annotated method
 * which is bound to a path parameter in an HttpServletRequest.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathParameter {
	/**
	 * @return the name of the matched parameter as given by a {@link PathElement} in
	 * a {@link Path}.
	 */
	
	String name();
}
