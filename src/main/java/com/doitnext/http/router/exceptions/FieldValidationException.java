package com.doitnext.http.router.exceptions;

public class FieldValidationException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8859343360579774436L;
	private final String implicatedFields[];
	
	public static final String errorCode = "BAD_INPUT_IN_REQUEST";
	
	public FieldValidationException(String implicatedFields[]) {
		super();
		this.implicatedFields = implicatedFields;
	}

	public FieldValidationException(String arg0, String implicatedFields[]) {
		super(arg0);
		this.implicatedFields = implicatedFields;
	}

	public FieldValidationException(Throwable arg0, String implicatedFields[]) {
		super(arg0);
		this.implicatedFields = implicatedFields;
	}

	public FieldValidationException(String arg0, Throwable arg1, String implicatedFields[]) {
		super(arg0, arg1);
		this.implicatedFields = implicatedFields;
	}

	/**
	 * @return the implicatedFields
	 */
	public String[] getImplicatedFields() {
		return implicatedFields;
	}
	
	

}
