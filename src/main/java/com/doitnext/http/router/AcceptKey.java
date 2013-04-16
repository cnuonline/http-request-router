package com.doitnext.http.router;


/**
 * Used to index a set of routes by common easily matched Route attributes.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class AcceptKey {
	final private String returnType;
	final private String returnFormat;

	public AcceptKey(Route route) {
		this.returnType = route.getReturnType();
		this.returnFormat = route.getReturnFormat();
		if(this.returnFormat.equals("*/*")) {
			throw new IllegalArgumentException("'*/*' is not a valid return format for a Route.");
		}
	}
	
	public AcceptKey(String acceptHeader) {
		String parts[] = acceptHeader.split(";", 2);
		this.returnFormat = parts[0].trim();
		this.returnType = parts[1].trim();
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @return the returnFormat
	 */
	public String getReturnFormat() {
		return returnFormat;
	}

	public boolean matches(Route route) {
		if(returnFormat.equals("*/*")) {
			return true;
		} else if(returnFormat.equals(route.getReturnFormat())) {
			if(returnType.isEmpty())
				return true;
			else
				return returnType.equals(route.getReturnType());
		}
		return false;
	}
	
	
}
