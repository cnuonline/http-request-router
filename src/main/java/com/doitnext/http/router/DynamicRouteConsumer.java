package com.doitnext.http.router;

import java.util.TreeSet;

import com.google.common.collect.ImmutableSortedSet;

public interface DynamicRouteConsumer {

	/**
	 * This method is called in order to initiate a callback to the
	 * {@link #dynamicEndpointResolver} implementation of
	 * {@link DynamicEndpointResolver#updateRoutes(RestRouterServlet, TreeSet) 
	 */
	public void fireUpdateRoutes();

	public void routesUpdated(ImmutableSortedSet<Route> newRoutes);

}