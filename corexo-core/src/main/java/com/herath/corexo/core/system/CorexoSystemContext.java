package com.herath.corexo.core.system;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hazelcast.core.HazelcastInstance;
import com.herath.corexo.core.dispatch.IMessageBoxManager;
import com.herath.corexo.core.dispatch.IMessageIdGenerator;

public class CorexoSystemContext {
	private Map<String, Object> registry;

	private Map<String, IMessageBoxManager<? extends Object>> messageBoxManagers;

	public CorexoSystemContext() {
		this.registry = new ConcurrentHashMap<String, Object>();
		this.messageBoxManagers = new ConcurrentHashMap<String, IMessageBoxManager<? extends Object>>();
	}

	public final HazelcastInstance getHazelcastInstance() {
		return this.getFromregistry("HZ-INSTANCE", HazelcastInstance.class);
	}

	public final IMessageIdGenerator getDefaultMessageIdGenerator() {
		return this.getFromregistry("DEFAULT_MESSAGE_ID_GENERATOR", IMessageIdGenerator.class);
	}

	public void addMessageBoxManager(String messageBoxManagerName,
			IMessageBoxManager<? extends Object> messageBoxManager) {
		this.messageBoxManagers.put(messageBoxManagerName, messageBoxManager);
	}

	@SuppressWarnings("unchecked")
	public final <T> IMessageBoxManager<T> getMessageBox(String messageBoxName, Class<T> clazz) {
		return (IMessageBoxManager<T>) this.messageBoxManagers.get(messageBoxName);
	}

	@SuppressWarnings("unchecked")
	public <T> T getFromregistry(String key, Class<T> clazz) {
		return (T) registry.get(key);
	}

	public void putToRegistry(String key, Object value) {
		this.registry.put(key, value);
	}

	public void putToRegistryIfAbscent(String key, Object value) {
		this.registry.putIfAbsent(key, value);
	}
}
