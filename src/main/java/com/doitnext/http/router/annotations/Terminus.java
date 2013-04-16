package com.doitnext.http.router.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a {@link RestMethod} parameter that takes the
 *  {@link Path.terminus} of a {@link Path}.
 * 
 * @author Steve Owens (steve@doitnext.com)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Terminus {

}
