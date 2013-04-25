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

Here is an example of a resource. This example provides an example of a resource that has a many to one relationship
with a parent resource.  The MyResource class enables basic CRUD operations with a couple special getters for rapid
UI develpment using tools such as JSON Forms or Alpaca.js.

```java
package my.project.root.package.service.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import my.project.root.package.exceptions.InvalidModificationException;
import my.project.root.package.exceptions.InvalidRelationshipException;
import my.project.root.package.exceptions.ItemNotFoundException;
import my.project.root.package.domain.MyParentResource;
import my.project.root.package.domain.MyResource;
import my.project.root.package.mongo.repositories.MyParentResourceRepository;
import my.project.root.package.mongo.repositories.MyResourceRepository;

import com.doitnext.http.router.annotations.PathParameter;
import com.doitnext.http.router.annotations.RequestBody;
import com.doitnext.http.router.annotations.RestMethod;
import com.doitnext.http.router.annotations.RestResource;
import com.doitnext.http.router.annotations.enums.HttpMethod;

@Service(value="MyResource")
@RestResource(value="MyResource", pathprefix = "/my-resource")
public class MyResource {
	static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	MyResourceRepository myResourceRepository;
	
	@Autowired
	MyParentResourceRepository myParentResourceRepository;
	
	@RestMethod(method = HttpMethod.POST, template = "", requestFormat="application/json")
	public MyResource createCatalogEntry(@RequestBody MyResource myResource) {
		myResource.setId(null);
		
		MyParentResource parentResource = myParentResourceRepository.findOne(myResource.getParentResourceId());
		if(parentResource == null)
			throw new InvalidRelationshipException(String.format("A resource must be bound to an parent resource on creation.  No MyParentResource found for parentResourceId '%s'.", myResource.getParentResourceId()));
		
		return myResourceRepository.save(myResource);
	}
	
	@RestMethod(method = HttpMethod.PUT, template = "", requestFormat="application/json")
	public MyResource updateResource(@RequestBody MyResource myResource) {
		MyResource existing = myResourceRepository.findOne(myResource.getId());
				
		if(existing == null)
			throw new ItemNotFoundException(myResource.getId(), MyResource.class);
		
		// When updating a resource, make sure it remains bound to the same Parent Resource as
		// that it was bound to when first created.
		if(myResource.getParentResourceId().equals(existing.getParentResourceId())) {
			throw new InvalidModificationException(String.format("Changing the parentResourceId on a resource update is not permitted. Old id '%s', attempted new id '%s'.  Resource must be bound to an parent resource on creation."));
		}
			
			
		return myResourceRepository.save(myResource);
	}
	
	@RestMethod(method = HttpMethod.GET, template = "", returnFormat="application/json", returnType="json.schema")
	public void getResourceSchema(HttpServletRequest request, HttpServletResponse response) throws IOException {
		InputStream is = MyResource.class.getResourceAsStream("MyResource.schema.json");
		byte[] bytes = IOUtils.toByteArray(is);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setContentLength(bytes.length);
		response.setStatus(200);
		response.getOutputStream().write(bytes);
		response.getOutputStream().close();
	}
	
	@RestMethod(method = HttpMethod.GET, template = "/{id:[a-f0-9]{12,12}:OBJECTID}")
	public MyResource getResource(@PathParameter(name = "id") String id) {
		return myResourceRepository.findOne(id);
	}
	
	@RestMethod(method = HttpMethod.GET, template = "/{id:[a-f0-9]{12,12}:OBJECTID}",
			returnFormat="application/json", returnType="alpaca.form")
	public void getResourceAsAlpaca(@PathParameter(name = "id") String id,
			HttpServletRequest req, HttpServletResponse resp) throws IOException {
		MyResource myResource = getResource(id);
		JsonNode schemaNode = objectMapper.readTree(MyResource.class.getResourceAsStream("MyResource.schema.json"));
		JsonNode optionsNode = objectMapper.readTree(MyResource.class.getResourceAsStream("MyResource.options.json"));
		JsonNode dataNode = objectMapper.readTree("null");
		if(myResource != null) {
			dataNode = objectMapper.readTree(objectMapper.writeValueAsBytes(myResource));
		} 
		Map<String, JsonNode> result = new HashMap<String,JsonNode>();
		result.put("data", dataNode);
		result.put("options", optionsNode);
		result.put("schema", schemaNode);
		byte resultBytes[] = objectMapper.writeValueAsBytes(result);
		resp.setContentLength(resultBytes.length);
		resp.setContentType("application/json; model=alpaca.form");
		resp.setStatus(200);
		resp.getOutputStream().write(resultBytes);
		resp.getOutputStream().close();
		
	}

	@RestMethod(method = HttpMethod.DELETE, template = "/{id:[a-f0-9]{12,12}:OBJECTID}")
	public boolean deleteResource(@PathParameter(name = "id") String id) {
		boolean result = (myResourceRepository.findOne(id) != null);
		myResourceRepository.delete(id);
		return result;
	}

}

```
