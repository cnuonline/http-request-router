/**
 * Copyright (C) 2013 Steve Owens (DoItNext.com) http://www.doitnext.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doitnext.http.router;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.doitnext.http.router.annotations.Description;
import com.doitnext.http.router.annotations.PathParameter;
import com.doitnext.http.router.annotations.QueryParameter;
import com.doitnext.http.router.annotations.RequestBody;
import com.doitnext.http.router.annotations.RequestBodyDoc;
import com.doitnext.http.router.annotations.ResponseBodyDoc;
import com.doitnext.http.router.annotations.RestMethod;
import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.exceptions.UnsupportedConversionException;
import com.doitnext.pathutils.GreedyTemplate;
import com.doitnext.pathutils.IdentifierTemplate;
import com.doitnext.pathutils.PathElementTemplate;
import com.google.common.collect.ImmutableList;

/**
 * Documents annotated collections as a POJO suitable for conversion to JSON.
 * 
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class ApiDocumentation {
	private static Logger logger = LoggerFactory.getLogger(ApiDocumentation.class);
	
	public static class TemplateArg {
		
		public final String name;
		public final String regEx;
		public final String logicalType;
		public final String description;
		
		
		public TemplateArg(PathElementTemplate pet, Route route) throws UnsupportedConversionException{
			if(pet instanceof IdentifierTemplate) {
				name =pet.getName();
				regEx = ((IdentifierTemplate) pet).getMatcher();
				logicalType = ((IdentifierTemplate) pet).getIdentifierType();
			} else if(pet instanceof GreedyTemplate){
				name = pet.getName();
				regEx = "([/][^/]+)+";
				logicalType = "GREEDY_PATH_MATCH";
			} else 
				throw new UnsupportedConversionException(PathElementTemplate.class, TemplateArg.class);
			
			description = getDescriptionForParameter(pet.getName(), route.getImplMethod());
		}
		
		private String getDescriptionForParameter(String parameterName, Method method){
			Annotation[][] annotationsz = method.getParameterAnnotations();
			for(Annotation[] annotations : annotationsz) {
				for(Annotation annotation : annotations){
					if(annotation instanceof PathParameter){
						if(((PathParameter) annotation).name().equals(parameterName)){
							for(Annotation annotation2 : annotations){
								if(annotation2 instanceof Description){
									return ((Description) annotation2).value();
								}
							}
							return null;
						}
					}
				}
			}
			return null;
		}
	}
	
	public static class QueryArg {
		public final String name;
		public final String description;
		
		public QueryArg(String name, String description){
			this.name = name;
			this.description = description;
		}
		
	}
	
	public static class RequestBodyDocumentation {
		public final String requestFormat;
		public final String requestType;
		public final String className;
		public final String description;
		public final String contentTypeHeaderExample;
		
		public RequestBodyDocumentation(String requestFormat, String requestType,
				String className, String description){
			this.requestFormat = requestFormat;
			this.requestType = requestType;
			this.className = className;
			this.description = description;
			
			if(StringUtils.isEmpty(requestType)){
				contentTypeHeaderExample = String.format("Content-Type: %s", requestFormat);
			} else
				contentTypeHeaderExample = String.format("Content-Type: %s; model=%s", requestFormat, requestType);
		}
		
	}
	
	public static class ResponseBodyDocumentation {
		public final String responseFormat;
		public final String responseType;
		public final String className;
		public final String description;
		public final String acceptHeaderExample;
		
		public ResponseBodyDocumentation(String responseFormat, String responseType,
				String className, String description){
			this.responseFormat = responseFormat;
			this.responseType = responseType;
			this.className = className;
			this.description = description;
			if(StringUtils.isEmpty(responseType)){
				acceptHeaderExample = String.format("Accept: %s", responseFormat);
			} else
				acceptHeaderExample = String.format("Accept: %s; model=%s", responseFormat, responseType);

		}
		
	}
	
	public static class CollectionDocumentation {
		public final String collectionName;
		public final String collectionDescription;
		public final List<RouteDocumentation> routes = new ArrayList<RouteDocumentation>();
		
		public CollectionDocumentation(String collectionName, String collectionDescription){
			this.collectionDescription = collectionDescription;
			this.collectionName = collectionName;
		}
		
	}
	
	public static class RouteDocumentation {
		public final String pathTemplate;
		public final List<TemplateArg> templateArguments = new ArrayList<TemplateArg>();
		public final List<QueryArg> queryParameters = new ArrayList<QueryArg>();
		public final RequestBodyDocumentation requestBody;
		public final ResponseBodyDocumentation responseBody;
		public final HttpMethod httpMethod;
		public final String extendedHttpMethod;
		public final String implMethodName;
 		
		public RouteDocumentation(Route route) {
			RequestBodyDocumentation requestBody = null;
			ResponseBodyDocumentation responseBody = null;
			pathTemplate = route.getPathTemplate().getLexicalPath();
			for(int x = 0; x < route.getPathTemplate().getLength(); x++){
				PathElementTemplate pet = route.getPathTemplate().getMatcher(x);
				try {
					if(pet instanceof IdentifierTemplate)
						templateArguments.add(new TemplateArg(pet, route));
					else if(pet instanceof GreedyTemplate)
						templateArguments.add(new TemplateArg(pet, route));
				} catch(UnsupportedConversionException e){
					logger.error("Exception caught converting PathElementTemplate to TemplateArg", e);
				}
			}
			
			httpMethod = route.getHttpMethod();
			extendedHttpMethod = route.getExtendedHttpMethod();
			implMethodName = route.getImplMethod().getName();
			
			Annotation[][] annotationsz = route.getImplMethod().getParameterAnnotations();
			for(int x = 0; x < annotationsz.length; x++){
				Annotation[] annotations = annotationsz[x];
				for(Annotation annotation : annotations){
					if(annotation instanceof QueryParameter){
						String name = ((QueryParameter) annotation).name();
						String description = null;
						for(Annotation annotation2 : annotations){
							if(annotation2 instanceof Description) {
								description = ((Description) annotation2).value();
								break;
							}
						}
						queryParameters.add(new QueryArg(name,description));
					} else if(annotation instanceof RequestBody) {
						Class<?> classz = route.getImplMethod().getParameterTypes()[x];
						String description = null;
						for(Annotation annotation2 : annotations){
							if(annotation2 instanceof Description) {
								description = ((Description) annotation2).value();
								break;
							}
						}
						requestBody = new RequestBodyDocumentation(route.getRequestFormat(), 
								route.getRequestType(), classz.getName(), description);
					}
				}
			}
			
			RequestBodyDoc rqbDoc= route.getImplMethod().getAnnotation(RequestBodyDoc.class); 
			if(rqbDoc != null){
				requestBody = new RequestBodyDocumentation(route.getRequestFormat(), 
						route.getRequestType(), rqbDoc.className(), rqbDoc.value()); 
			}
			
			ResponseBodyDoc rsbDoc= route.getImplMethod().getAnnotation(ResponseBodyDoc.class); 
			if(rsbDoc != null){
				responseBody = new ResponseBodyDocumentation(route.getReturnFormat(), 
						route.getReturnType(), rsbDoc.className(), rsbDoc.value()); 
			}
			
		
			this.requestBody = requestBody;
			this.responseBody =responseBody;
		}
	}
	
	private final String apiName;
	private final String apiDescription;
	private final ImmutableList<CollectionDocumentation> collections;
	
	public ApiDocumentation(String name, String description, SortedSet<Route> routes){
		
		Map<Class<?>, CollectionDocumentation> collDocs = new HashMap<Class<?>,CollectionDocumentation>();
		
		for(Route route : routes){
			Class<?> key = route.getImplClass();
			if(!collDocs.containsKey(key)){
				String collectionName = key.getName();
				String collectionDescription = null;
				Description d = route.getImplClass().getAnnotation(Description.class);
				if(d != null)
					collectionDescription = d.value();
				CollectionDocumentation cd=new CollectionDocumentation(collectionName, collectionDescription);
				collDocs.put(key, cd);
			}
			collDocs.get(key).routes.add(new RouteDocumentation(route));
		}
		
		this.collections = ImmutableList.copyOf(collDocs.values());
		this.apiName = name;
		this.apiDescription = description;
	}
	
	

	/**
	 * @return the collections
	 */
	public ImmutableList<CollectionDocumentation> getCollections() {
		return collections;
	}



	/**
	 * @return the apiName
	 */
	public String getApiName() {
		return apiName;
	}

	/**
	 * @return the apiDescription
	 */
	public String getApiDescription() {
		return apiDescription;
	}
	
	
}
