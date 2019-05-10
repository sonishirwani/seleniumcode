package com.eqtechnologic.exceptions;

@SuppressWarnings("serial")
public class ExcelConnectionException extends Exception {

	public ExcelConnectionException(){
		super();
	}
	
	public ExcelConnectionException(String message){
		super(message);
	}
	
	public ExcelConnectionException(Throwable cause){
		super(cause);
	}
	
	public ExcelConnectionException(String message, Throwable cause){
		super(message,cause);
	}
}
