http-request-router
==========

A restful dispatcher servlet.  This router can be used as a 
substitute for a dispatcher servlet such as Spring DispatcherServlet.
It provides a richer set of annotations and route resolution to be more
in compliance with Restful web services.  It was designed with Rest in mind
from the ground up.

To use:

Create a Maven style WAR file project and add this project as a Maven dependency.
```xml
<dependency>
	<groupId>com.doitnext</groupId>
	<artifactId>http-request-router</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

In your WAR file project web.xml add a Servlet and Servlet mapping such as:

```xml
<servlet>
	<servlet-name>RestRouterServlet</servlet-name>
    <servlet-class>com.doitnext.http.router.RestRouterServlet</servlet-class>
    
  <!-- The restPackageRoot identifies the root package under which the 
       @RestResource annotated classes are to be found.  Any class under
       this package annotated with @RestResource and having methods with
       @RestMethod annotations will be added as endpoints to the service. --> 
  <init-param>
    <param-name>restPackageRoot</param-name> 
    <param-value>net.mycoolapps.api.impl</param-value> 
  </init-param>

  <!-- The pathPrefix argument designates a prefix that comes before the paths
       that are constructed by processing the @RestResource and @RestMethod 
       annotations.  This prefix could be left empty if you don't
       want to add elements to your URI's. --> 
  <init-param>
    <param-name>pathPrefix</param-name> 
    <param-value>/restapi/v1</param-value> 
  </init-param>
  
  <!-- The methodInvokerClass identifies a custom method invocation strategy.
     it is entirely optional and if not present then the DefaultInvoker class
     provided by http-request-router will be used. 
     
     You can either specify a no argument constructor style implementation of 
     MethodInvoker, or you can specify a class factory that instantiates
     a MethodInvoker implementation.  If you specify a class factory you will
     also need to provide a methodInvokerFactoryMethod.
     -->
  <init-param>
  	<param-name>methodInvokerClass</param-name>
  	<param-value>net.mycoolapps.api.invoker.MyCustomInvokerFactory</param-value>
  </init-param>
  
  <!-- Use this parameter to identify a zero argument factory method on the
       class identified by methodInvokerClass when using the factory approach
       to instantiating a custom invoker -->
  <init-param>
  	<param-name>methodInvokerFactoryMethod</param-name>
  	<param-value>createCustomInvoker</param-value>
  </init-param>
  
  <!--
  	 Use this optional param if you wish to dynamically update endpoints that the
  	 service is aware of. This feature can be used for example when creating mock
  	 services which allow people to iteratively design an api.
  -->
  <init-param>
  	<param-name>dynamicEndpointResolver</param-name>
  	<param-value>net.mycoolapps.api.endpointmanagement.MyEndpointResolver</param-value>
  </init-param>
  
</servlet>

<servlet-mapping>
    <servlet-name>RestRouterServlet</servlet-name>
    <url-pattern>/*</url-pattern>
</servlet-mapping>
```
