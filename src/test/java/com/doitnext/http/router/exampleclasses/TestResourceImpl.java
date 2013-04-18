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
import java.util.List;

import com.doitnext.http.router.annotations.PathParameter;
import com.doitnext.http.router.annotations.RestMethod;
import com.doitnext.http.router.annotations.RestResource;
import com.doitnext.http.router.annotations.enums.HttpMethod;
import com.doitnext.http.router.exceptions.DuplicateKeyConstraintException;
import com.doitnext.http.router.exceptions.UnrecongizedKeyException;

/**
 * @author Steve Owens (steve@doitnext.com)
 * 
 */
@RestResource(value="testResource1", pathprefix = "/teams")
public class TestResourceImpl {

	private List<TestTeamPojo> teams = new ArrayList<TestTeamPojo>();
	private String lastMethodCalled = null;
	private String lastHttpMethodCalled = null;
	
	public TestResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	public String getLastMethodCalled() {
		return lastMethodCalled;
	}
	
	public String getLastHttpMethodCalled() {
		return lastHttpMethodCalled;
	}
	
	@RestMethod(method = HttpMethod.POST, template = "")
	public TestTeamPojo addTeam(TestTeamPojo newTeam) {
		if (teams.contains(newTeam))
			throw new DuplicateKeyConstraintException(String.format(
					"There is already a team in the collection with key %s",
					newTeam.getKey()));
		teams.add(newTeam);
		lastMethodCalled = "addTeam";
		lastHttpMethodCalled = "POST";
		return newTeam;
	}

	@RestMethod(method = HttpMethod.PUT, template = "")
	public TestTeamPojo updateTeam(TestTeamPojo teamToUpdate) {
		if (!teams.contains(teamToUpdate))
			throw new UnrecongizedKeyException(
					String.format("No team with key %s is in the collection."));
		teams.remove(teamToUpdate);
		teams.add(teamToUpdate);
		lastMethodCalled = "updateTeam";
		lastHttpMethodCalled = "PUT";
		return teamToUpdate;
	}

	@RestMethod(method = HttpMethod.GET, template = "/{teamType:[A-Z]{1,10}:TEAMTYPE}/{teamName:[a-zA-Z+'\\-0-9]{2,30}:TEXT}")
	public TestTeamPojo getTeam(
			@PathParameter(name = "teamType") String teamType,
			@PathParameter(name = "teamName") String teamName) {
		TestTeamPojo key = new TestTeamPojo(TestTeamPojo.Type.valueOf(teamType
				.toUpperCase()), teamName);
		if (teams.contains(key))
			return teams.get(teams.indexOf(key));
		lastMethodCalled = "getTeam";
		lastHttpMethodCalled = "GET";
				
		return null;
	}

	@RestMethod(method = HttpMethod.DELETE, template = "/{teamType:[A-Z]{1,10}:TEAMTYPE}/{teamName:[a-zA-Z+'\\-0-9]{2,30}:TEXT}")
	public boolean deleteTeam(
			@PathParameter(name = "teamType") String teamType,
			@PathParameter(name = "teamName") String teamName) {
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
		return teams.remove(key);
	}
	
	@RestMethod(method = HttpMethod.DELETE, template = "/{teamType:[A-Z]{1,10}:TEAMTYPE}/{teamName:[a-zA-Z+'\\-0-9]{2,30}:TEXT}")
	public boolean deleteTeam3(
			@PathParameter(name = "teamType") String teamType,
			@PathParameter(name = "teamName") String teamName) {
		TestTeamPojo key = new TestTeamPojo(TestTeamPojo.Type.valueOf(teamType
				.toUpperCase()), teamName);
		return teams.remove(key);
	}
}
