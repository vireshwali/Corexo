package com.herath.corexo.core.system;

import com.herath.corexo.core.exceptions.BaseCorexoException;

public interface ICorexoComponentLifeCycle {

	public void initialize(ICorexoSystem corexoSystem) throws BaseCorexoException;

	public void postConstruct() throws BaseCorexoException;

	public void preShutDown() throws BaseCorexoException;

	public void shutDown() throws BaseCorexoException;

	/*public BaseCorexoComponent activate() throws BaseCorexoException;
	
	public <T> void passivate(Kryo kryo, Output output) throws BaseCorexoException;*/
}
