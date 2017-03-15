package com.herath.corexo.core.exceptions;

public class BaseCorexoException extends Exception {

	private static final long serialVersionUID = 656891071985553077L;

	public BaseCorexoException() {
		super();
	}

	public BaseCorexoException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BaseCorexoException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseCorexoException(String message) {
		super(message);
	}

	public BaseCorexoException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
