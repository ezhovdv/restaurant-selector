package ru.edv.largecode.restaurant.error;

import lombok.Data;

@Data
public class ErrorDetail {
	public static final String INTEGRITY_VIOLATION = "Integrity violation";
	public static final String EMPTY_REQUEST_DATA = "Data is not found";
	public static final String CONSTRAINT_VIOLATION = "Errors in request data";

	private int status;
	private String description;
	private String message;
	private String url;
}
