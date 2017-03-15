package com.herath.corexo.core.system;

import com.herath.corexo.core.exceptions.BaseCorexoException;

public interface ICorexoSystemLifeCycle {
	public void initialize() throws BaseCorexoException;

	public void postConstruct() throws BaseCorexoException;

	public void preShutDown() throws BaseCorexoException;

	public void shutDown() throws BaseCorexoException;
}