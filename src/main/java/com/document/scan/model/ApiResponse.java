package com.document.scan.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private String status;
    private String message;
    private String error;
    private T data;
    
    
	public ApiResponse(String status, String message, String error, T data) {
		super();
		this.status = status;
		this.message = message;
		this.error = error;
		this.data = data;
	}
    
    
    

    // Constructors, Getters and Setters
}
