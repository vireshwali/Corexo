package com.herath.corexo.core.system;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.herath.corexo.core.dispatch.DefaultMessageBoxManager;
import com.herath.corexo.core.dispatch.DefaultMessageIdGenerator;
import com.herath.corexo.core.dispatch.IMessageBoxManager;
import com.herath.corexo.core.dispatch.IMessageIdGenerator;
import com.herath.corexo.core.exceptions.CorexoInitializationException;
import com.herath.corexo.core.utils.CorexoCoreConstants;

import info.jerrinot.subzero.SubZero;

public final class CorexoSystem implements ICorexoSystem {

	private static final Logger LOG = LoggerFactory.getLogger(CorexoSystem.class);

	private CorexoSystemContext corexoSystemContext;

	private static final CorexoSystem COREXO_SYSTEM = new CorexoSystem();

	private CorexoSystem() {
		try {
			this.initialize();
		} catch (CorexoInitializationException e) {
			throw new RuntimeException(e);
		}
	}

	public static CorexoSystem getInstance() {
		return COREXO_SYSTEM;
	}

	public void initialize() throws CorexoInitializationException {
		LOG.info("Initializing CorexoMessageSystem");
		try {
			// create the Corexo System Context
			this.corexoSystemContext = new CorexoSystemContext();

			Config hzConfig = new Config();
			// hzConfig.setInstanceName(instanceName);
			hzConfig.setProperty("hazelcast.logging.type", "log4j2");
			hzConfig.setProperty("hazelcast.jmx", "true");
			hzConfig.setProperty("hazelcast.io.thread.count", "15");
			SubZero.useAsGlobalSerializer(hzConfig);
			HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance(hzConfig);

			this.corexoSystemContext.putToRegistry("HZ-INSTANCE", hzInstance);

			// create and initialize the DefaultMessageIdGenerator
			IMessageIdGenerator defaultMessageIdGenerator = new DefaultMessageIdGenerator();
			defaultMessageIdGenerator.initialize(this);
			this.corexoSystemContext.putToRegistry("DEFAULT_MESSAGE_ID_GENERATOR", defaultMessageIdGenerator);

			IMessageBoxManager<Object> defaultMessageBoxManager = new DefaultMessageBoxManager();
			defaultMessageBoxManager.initialize(this);// initialize it
			this.corexoSystemContext.addMessageBoxManager(CorexoCoreConstants.DEFAULT_SEND_MESSAGE_BOX, defaultMessageBoxManager);
			
			
			// TODO: read configuration and initialize all message boxes and
			// executors

			LOG.info("CorexoMessageSystem started successfully.");
		} catch (Throwable e) {
			throw new CorexoInitializationException(e);
		}
	}

	public CorexoSystemContext getCorexoSystemContext() {
		return corexoSystemContext;
	}

	public void postConstruct() {
		// TODO Auto-generated method stub

	}

	public void preShutDown() {
		// TODO Auto-generated method stub

	}

	public void shutDown() {
		LOG.info("Shuttingdown CorexoMessageSystem gracefully.");
		this.corexoSystemContext.getFromregistry("HZ-INSTANCE", HazelcastInstance.class).shutdown();
		LOG.info("CorexoMessageSystem successfully shutdown.");
	}
}
