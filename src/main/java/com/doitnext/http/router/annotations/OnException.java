package com.doitnext.http.router.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface OnException {
	public Class<? extends Throwable> exceptionClass();
	public int statusCode();
}
