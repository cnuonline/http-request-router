package com.doitnext.http.router;

/**
 * Used by {@link DefaultEndpointResolver} to provide a hash key into a HashMap of Resolvers.
 * 
 * @author Steve Owens (steve@doitnext.com)
 */
public final class MethodReturnKey {
	/**
	 * The type or model of a method response (such as '/mymodels/team/roster')
	 */
	final private String returnType;
	/**
	 * The format of a method response (such as application/json)
	 */
	final private String returnFormat;

	/**
	 * This constuctor is used by {@link DefaultEndpointResolver} in order to 
	 * provide a map key for collections of Response Handlers.  This enables
	 * the endpoint resolver to properly wire an error and success handler to
	 * a route.
	 * 
	 * @param returnType the type (or model) of the response body
	 * @param returnFormat the format of the response body
	 */
	public MethodReturnKey(String returnType, String returnFormat) {
		this.returnType = returnType;
		this.returnFormat = returnFormat;
	}

	/**
	 * @return the {@link #returnType}
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @return the {@link #returnFormat}
	 */
	public String getReturnFormat() {
		return returnFormat;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((returnFormat == null) ? 0 : returnFormat.hashCode());
		result = prime * result
				+ ((returnType == null) ? 0 : returnType.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodReturnKey other = (MethodReturnKey) obj;
		if (returnFormat == null) {
			if (other.returnFormat != null)
				return false;
		} else if (!returnFormat.equals(other.returnFormat))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		return true;
	}

}
