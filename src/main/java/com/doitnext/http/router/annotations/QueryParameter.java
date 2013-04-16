package com.doitnext.http.router.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation identifies a parameter in a {@link RestMethod} annotated method
 * which is bound to a query string parameter in an HttpServletRequest.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParameter {
	/**
	 * @return the name of the matched parameter as given by a query parameter
	 * as parsed using standard URI syntax from the {@link Path.terminus} in
	 * a {@link Path}.
	 */
	String name();
}
