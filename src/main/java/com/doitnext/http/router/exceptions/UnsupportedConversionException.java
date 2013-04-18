package com.doitnext.http.router.exceptions;

/**
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class UnsupportedConversionException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2042410592768091253L;
	
	public UnsupportedConversionException(Class<?> from, Class<?> to) {
		super(String.format("Unable to convert from %s to %s.", from.getName(), to.getName()));
	}

	public UnsupportedConversionException(Class<?> from, Class<?> to, Throwable cause) {
		super(String.format("Unable to convert from %s to %s.", from.getName(), to.getName()), cause);		
	}

}
