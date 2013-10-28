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
package com.doitnext.http.router.exampleclasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doitnext.http.router.annotations.Description;
import com.doitnext.http.router.annotations.PathParameter;
import com.doitnext.http.router.annotations.QueryParameter;
import com.doitnext.http.router.annotations.RequestBody;
import com.doitnext.http.router.annotations.RestCollection;
import com.doitnext.http.router.annotations.RestMethod;
import com.doitnext.http.router.annotations.Terminus;
import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.exceptions.DuplicateKeyConstraintException;
import com.doitnext.http.router.exceptions.UnrecongizedKeyException;

/**
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
@RestCollection(value="testCollection1", pathprefix = "/teams")
public class TestCollectionImpl {
	private final static Logger logger = LoggerFactory.getLogger(TestCollectionImpl.class);
	
	private List<TestTeamPojo> teams = new ArrayList<TestTeamPojo>();
	private String lastMethodCalled = null;
	private String lastHttpMethodCalled = null;
	
	public TestCollectionImpl() {
		// TODO Auto-generated constructor stub
	}

	public String getLastMethodCalled() {
		return lastMethodCalled;
	}
	
	public String getLastHttpMethodCalled() {
		return lastHttpMethodCalled;
	}
	
	@RestMethod(method = HttpMethod.POST, template = "", requestFormat="application/json")
	public TestTeamPojo addTeam(@RequestBody TestTeamPojo newTeam) {
		if (teams.contains(newTeam))
			throw new DuplicateKeyConstraintException(String.format(
					"There is already a team in the collection with key %s",
					newTeam.getKey()));
		teams.add(newTeam);
		lastMethodCalled = "addTeam";
		lastHttpMethodCalled = "POST";
		return newTeam;
	}

	@RestMethod(method = HttpMethod.PUT, template = "", requestFormat="application/json")
	public TestTeamPojo updateTeam(@RequestBody TestTeamPojo teamToUpdate) {
		if (!teams.contains(teamToUpdate))
			throw new UnrecongizedKeyException(
					String.format("No team with key %s is in the collection.", teamToUpdate.getKey()));
		teams.remove(teamToUpdate);
		teams.add(teamToUpdate);
		lastMethodCalled = "updateTeam";
		lastHttpMethodCalled = "PUT";
		return teamToUpdate;
	}

	@RestMethod(method = HttpMethod.GET, template = "")
	public List<TestTeamPojo> getTeams(
			@QueryParameter(name = "teamType") @Description("Identifies the team type.") String teamType,
			@QueryParameter(name = "teamName") String teamName,
			@QueryParameter(name = "city") String cities[]) {

		List<TestTeamPojo> result = new ArrayList<TestTeamPojo>();
		for(TestTeamPojo team : teams) {
			boolean match = true;
			if(!StringUtils.isEmpty(teamType))
				if(!teamType.equals(team.getType().name()))
					match = false;
			if(!StringUtils.isEmpty(teamName))
				if(!teamName.equals(team.getName()))
					match = false;
			
			if(cities.length > 0) {
				boolean found = false;
				for(String city : cities) {
					if(!StringUtils.isEmpty(city))
						if(city.equals(team.getCity()))
							found = true;
					
				}
				if(!found)
					match = false;
			}
			if(match == true)
				result.add(team);
		}
			
		lastMethodCalled = "getTeams";
		lastHttpMethodCalled = "GET";
				
		return result;
	}

	@RestMethod(method = HttpMethod.GET, template = "/{teamType:[A-Z]{1,10}:TEAMTYPE}/{teamName:[a-zA-Z+'\\-0-9]{2,30}:TEXT}")
	public TestTeamPojo getTeam(
			@Nonnull @PathParameter(name = "teamType") String teamType,
			@PathParameter(name = "teamName") String teamName,
			@Terminus String terminus) {
		
		TestTeamPojo key = new TestTeamPojo(TestTeamPojo.Type.valueOf(teamType
				.toUpperCase()), teamName);
		logger.debug("Terminus = " + terminus);
		lastMethodCalled = "getTeam";
		lastHttpMethodCalled = "GET";

		if (teams.contains(key))
			return teams.get(teams.indexOf(key));
				
		return null;
	}

	@RestMethod(method = HttpMethod.DELETE, template = "/{teamType:[A-Z]{1,10}:TEAMTYPE}/{teamName:[a-zA-Z+'\\-0-9]{2,30}:TEXT}")
	@Description("Delete a team from the collection")
	public boolean deleteTeam(
			@Description("The type of team (e.g. FOOTBALL, BASEBALL, etc)") @PathParameter(name = "teamType") String teamType,
			@Description("Name of the team") @PathParameter(name = "teamName") String teamName) {
		TestTeamPojo key = new TestTeamPojo(TestTeamPojo.Type.valueOf(teamType
				.toUpperCase()), teamName);

		lastMethodCalled = "deleteTeam";
		lastHttpMethodCalled = "DELETE";

		return teams.remove(key);
	}

	@RestMethod(method = HttpMethod.DELETE, template = "/{teamType:[A-Z]{1,10}:TEAMTYPE}/{teamName:[a-zA-Z+'\\-0-9]{2,30}:TEXT}", returnFormat = "application/xml")
	public boolean deleteTeam2(
			@PathParameter(name = "teamType") String teamType,
			@PathParameter(name = "teamName") String teamName) {
		TestTeamPojo key = new TestTeamPojo(TestTeamPojo.Type.valueOf(teamType
				.toUpperCase()), teamName);
		lastMethodCalled = "deleteTeam2";
		lastHttpMethodCalled = "DELETE";
		
		return teams.remove(key);
	}
	
	@RestMethod(method = HttpMethod.DELETE, template = "/conflict/{teamType:[A-Z]{1,10}:TEAMTYPE}/{teamName:[a-zA-Z+'\\-0-9]{2,30}:TEXT}")
	public boolean deleteTeam3(
			@PathParameter(name = "teamType") String teamType,
			@PathParameter(name = "teamName") String teamName) {
		TestTeamPojo key = new TestTeamPojo(TestTeamPojo.Type.valueOf(teamType
				.toUpperCase()), teamName);

		lastMethodCalled = "deleteTeam3";
		lastHttpMethodCalled = "DELETE";

		return teams.remove(key);
	}
	@RestMethod(method = HttpMethod.DELETE, template = "/conflict/{teamType:[A-Z]{1,10}:TEAMTYPE}/{teamName:[a-zA-Z+'\\-0-9]{2,30}:TEXT}")
	public boolean deleteTeam4(
			@PathParameter(name = "teamType") String teamType,
			@PathParameter(name = "teamName") String teamName) {
		TestTeamPojo key = new TestTeamPojo(TestTeamPojo.Type.valueOf(teamType
				.toUpperCase()), teamName);
		lastMethodCalled = "deleteTeam4";
		lastHttpMethodCalled = "DELETE";

		return teams.remove(key);
	}

	@RestMethod(method = HttpMethod.GET, template = "/badTerminusArg")
	public void badTerminusArg(@Terminus int x) {
		throw new IllegalStateException("This method should never have been called.");
	}
	
	@RestMethod(method = HttpMethod.GET, template = "/queryAndTerminus")
	public void mixedTerminusArgs(@Terminus String terminus, @QueryParameter(name="item1") String item1) {
		if(!StringUtils.isEmpty(item1) && StringUtils.isEmpty(terminus))
			throw new IllegalStateException("Item1 is a query parameter but there seems to be no terminus.");
		lastMethodCalled = "mixedTerminusArgs";
		lastHttpMethodCalled = "GET";

	}

	@RestMethod(method = HttpMethod.GET, template = "/rawCall")
	public void rawCall(HttpServletRequest req, HttpServletResponse resp) {
		lastMethodCalled = "rawCall";
		lastHttpMethodCalled = "GET";
	}

	@RestMethod(method = HttpMethod.GET, template = "/_healthCheck")
	public void healthCheck() {
		lastMethodCalled = "healthCheck";
		lastHttpMethodCalled = "GET";
	}

	@RestMethod(method = HttpMethod.GET, template = "/unannotatedParameter")
	public void unannotatedParameter(String extraArg) {
		lastMethodCalled = "unannotatedParameter";
		lastHttpMethodCalled = "GET";
	}
	
	@RestMethod(method = HttpMethod.GET, template = "/favorites/{userId:[0-9a-z]+:USERID}/{*:storePath}", returnType="hashmap")
	public Map<String,String> getFavoritesForUser(@PathParameter(name="userId") String userId,
			@PathParameter(name="storePath") String storePath) {
		Map<String, String> result = new HashMap<String,String>();
		result.put("userId", userId);
		result.put("storePath", storePath);
		lastMethodCalled = "getFavoritesForUser";
		lastHttpMethodCalled = "GET";
		return result;
	}
}
