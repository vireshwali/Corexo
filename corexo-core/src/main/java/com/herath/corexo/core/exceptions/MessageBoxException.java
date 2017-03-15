package com.herath.corexo.core.exceptions;

public class MessageBoxException extends BaseCorexoException {

	private static final long serialVersionUID = -4912388384850642975L;

	public MessageBoxException() {
		super();
	}

	public MessageBoxException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MessageBoxException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageBoxException(String message) {
		super(message);
	}

	public MessageBoxException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
