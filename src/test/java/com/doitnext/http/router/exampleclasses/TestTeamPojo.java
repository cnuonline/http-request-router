package com.doitnext.http.router.exampleclasses;

/**
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class TestTeamPojo {
	public enum Type { BASEBALL, FOOTBALL, BASKETBALL };
	
	// Keys
	private final Type type;
	private final String name;
	
	// Derived values
	private final String key;
	
	// Mutable values
	private  String league;
	private String city;
	
	public TestTeamPojo (Type type, String name) {
		this.type = type;
		this.name = name;
		key = String.format("[%s, %s]", name, type.name());
	}
	
	public void copyFrom(TestTeamPojo updater) {
		league = updater.league;
		city = updater.city;
	}
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the league
	 */
	public String getLeague() {
		return league;
	}

	/**
	 * @param league the league to set
	 */
	public void setLeague(String league) {
		this.league = league;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		TestTeamPojo other = (TestTeamPojo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
	
	
}
