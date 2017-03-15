package com.herath.corexo.core.exceptions;

public class CorexoInitializationException extends BaseCorexoException {

	private static final long serialVersionUID = -8481546717629524555L;

	public CorexoInitializationException() {
		super();
	}

	public CorexoInitializationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CorexoInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public CorexoInitializationException(String message) {
		super(message);
	}

	public CorexoInitializationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
