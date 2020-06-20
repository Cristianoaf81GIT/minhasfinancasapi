package com.cristiano.exception;

public class ErroAutenticacao extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6562016570773403006L;

	public ErroAutenticacao() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ErroAutenticacao(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ErroAutenticacao(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ErroAutenticacao(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ErroAutenticacao(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	

}
