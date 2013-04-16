package com.doitnext.http.router;


/**
 * Used to index a set of routes by common easily matched Route attributes.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class ContentTypeKey {
	final private String requestType;
	final private String requestFormat;

	public ContentTypeKey(Route route) {
		this.requestType = route.getRequestType();
		this.requestFormat = route.getRequestFormat();
	}
	
	public ContentTypeKey(String contentTypeHeader) {
		if(contentTypeHeader != null) {
			String parts[] = contentTypeHeader.split(";", 2);
			this.requestFormat = parts[0].trim();
			if(parts.length > 1)
				this.requestType = parts[1].trim();
			else
				this.requestType = "";
		} else {
			requestType = null;
			requestFormat = null;
		}
	}

	/**
	 * @return the requestType
	 */
	public String getRequestType() {
		return requestType;
	}

	
	/**
	 * @return the requestFormat
	 */
	public String getRequestFormat() {
		return requestFormat;
	}

	/**
	 * Matches a given route.
	 * 
	 * @param route the route to match
	 * @return the result of the match <code>true</code> is a match <code>false</code> is not a match.
	 */
	public boolean matches(Route route) {
		if(route.getRequestFormat().equals("*/*"))
			return true;
		if(requestFormat == null) {
			// Only match routes with an empty requestFormat
			if(route.getRequestFormat().isEmpty()) {
				return true;
			}
		} if(requestFormat.matches(route.getRequestFormat())) {
			if(requestType == null)
				return true;
			if(requestType.isEmpty())
				return true;
			return requestType.equals(route.getRequestType());
		}
		return false;
	}
	
}
