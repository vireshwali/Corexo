package com.herath.corexo.core.system;

import com.hazelcast.core.HazelcastInstance;
import com.herath.corexo.core.dispatch.IMessageIdGenerator;
import com.herath.corexo.core.exceptions.BaseCorexoException;

public class BaseCorexoComponent implements ICorexoComponentLifeCycle {

	protected CorexoSystemContext corexoSystemContext;
	
	protected ICorexoSystem corexoSystem;
	
	public final HazelcastInstance getHazelcastInstance() {
		return this.corexoSystemContext.getHazelcastInstance();
	}
	
	public final IMessageIdGenerator getDefaultMessageIdGenerator() {
		return this.corexoSystemContext.getDefaultMessageIdGenerator();
	}
	
	public void initialize(ICorexoSystem corexoSystem) throws BaseCorexoException {
		this.corexoSystem = corexoSystem;
		this.corexoSystemContext = this.corexoSystem.getCorexoSystemContext();
	}

	public void postConstruct() throws BaseCorexoException {

	}

	public void preShutDown() throws BaseCorexoException {

	}

	public void shutDown() throws BaseCorexoException {

	}
}
