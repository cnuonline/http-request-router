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
	<groupId>com.thetransactioncompany</groupId>
	<artifactId>cors-filter</artifactId>
	<version>1.5.1</version>
</dependency>
<dependency>
	<groupId>com.doitnext</groupId>
	<artifactId>http-request-router</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

In your WAR file project web.xml add a Servlet and Servlet mapping such as:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
    http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
           version="3.0">
 
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/servletContext.xml</param-value>
    </context-param>
	<context-param>
        <param-name>log4jConfigLocation</param-name>
        <param-value>classpath:log4j-my-service.properties</param-value>
    </context-param>    
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
   <listener>
        <listener-class>org.springframework.web.util.Log4jConfigListener</listener-class>
    </listener>    
    
    <filter>
		<filter-name>CORS</filter-name>
		<filter-class>com.thetransactioncompany.cors.CORSFilter</filter-class>
		<init-param>
                <param-name>cors.allowOrigin</param-name>
                <param-value>*</param-value>
        </init-param>
		<init-param>
                <param-name>cors.supportedMethods</param-name>
                <param-value>DELETE GET HEAD PUT POST TRACE</param-value>
        </init-param>
        <init-param>
        	<param-name>cors.supportedHeaders</param-name>
        	<param-value>Accept, Authorization, Content-Type, X-Requested-With</param-value>
        </init-param>
	</filter>
    
    <!--  org.springframework.web.context.support.HttpRequestHandlerServlet
    	  delegates calls to a bean identified in the servletcontext which implements
    	  org.springframework.web.HttpRequestHandler with a bean name that
    	  matches the servlet-name -->
    <servlet>
        <servlet-name>restRouter</servlet-name>
        <servlet-class>org.springframework.web.context.support.HttpRequestHandlerServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>restRouter</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>
    <filter-mapping>
        <filter-name>CORS</filter-name>
        <servlet-name>restRouter</servlet-name>
	</filter-mapping>
</web-app>

```

In your webapp/WEB-INF/servletContext.xml file you can do something like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:mongo="http://www.springframework.org/schema/data/mongo"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
		http://www.springframework.org/schema/beans/spring-beans.xsd
       	http://www.springframework.org/schema/context 
       	http://www.springframework.org/schema/context/spring-context.xsd
       	http://www.springframework.org/schema/data/mongo
  		http://www.springframework.org/schema/data/mongo/spring-mongo.xsd">

	<context:component-scan base-package="my.project.root.package" />

	<!-- MongoFactoryBean instance -->
	<bean id="mongo" class="org.springframework.data.mongodb.core.MongoFactoryBean">
		<property name="host" value="localhost" />
	</bean>

	<!-- MongoTemplate instance -->
	<bean id="mongoTemplate" class="org.springframework.data.mongodb.core.MongoTemplate">
		<constructor-arg name="mongo" ref="mongo" />
		<constructor-arg name="databaseName" value="myDatabase" />
	</bean>

	<mongo:repositories base-package="my.project.root.package.mongo.repositories" />

	<bean id="defaultInvoker" class="com.doitnext.http.router.DefaultInvoker">
	</bean>

	<bean id="jsonReturnKey" class="com.doitnext.http.router.MethodReturnKey">
		<constructor-arg index="0" value="" />
		<constructor-arg index="1" value="application/json" />
	</bean>

	<bean id="alpacaFormReturnKey" class="com.doitnext.http.router.MethodReturnKey">
		<constructor-arg index="0" value="alpaca.form" />
		<constructor-arg index="1" value="application/json" />
	</bean>
	
	<bean id="jsonSchemaReturnKey" class="com.doitnext.http.router.MethodReturnKey">
		<constructor-arg index="0" value="json.schema" />
		<constructor-arg index="1" value="application/json" />
	</bean>
	
	<bean id="defaultEndpointResolver" class="com.doitnext.http.router.DefaultEndpointResolver">
		<property name="methodInvoker" ref="defaultInvoker" />
		<property name="successHandlers">
			<map>
				<entry key-ref="jsonReturnKey">
					<bean
						class="com.doitnext.http.router.responsehandlers.DefaultSuccessHandler" />
				</entry>
				<entry key-ref="alpacaFormReturnKey">
					<bean
						class="com.doitnext.http.router.responsehandlers.DefaultSuccessHandler" />
				</entry>
				<entry key-ref="jsonSchemaReturnKey">
					<bean
						class="com.doitnext.http.router.responsehandlers.DefaultSuccessHandler" />
				</entry>
			</map>
		</property>
		<property name="errorHandlers">
			<map>
				<entry key-ref="jsonReturnKey">
					<bean
						class="com.doitnext.http.router.responsehandlers.DefaultErrorHandler" />
				</entry>
				<entry key-ref="alpacaFormReturnKey">
					<bean
						class="com.doitnext.http.router.responsehandlers.DefaultErrorHandler" />
				</entry>
				<entry key-ref="jsonSchemaReturnKey">
					<bean
						class="com.doitnext.http.router.responsehandlers.DefaultErrorHandler" />
				</entry>
			</map>
		</property>
	</bean>

	<bean id="restRouter" class="com.doitnext.http.router.RestRouterServlet">
		<property name="restPackageRoot" value="my.project.root.package.service.resources" />
		<property name="pathPrefix" value="/myapi" />
		<property name="endpointResolver" ref="defaultEndpointResolver" />
	</bean>

</beans>
```
