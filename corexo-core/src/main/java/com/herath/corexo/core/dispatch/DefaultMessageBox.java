package com.herath.corexo.core.dispatch;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;
import com.herath.corexo.core.exceptions.BaseCorexoException;
import com.herath.corexo.core.message.IMessage;
import com.herath.corexo.core.system.BaseCorexoComponent;
import com.herath.corexo.core.system.ICorexoSystem;
import com.herath.corexo.core.utils.CorexoCoreConstants;

final class DefaultMessageBox extends BaseCorexoComponent implements IMessageBox<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageBox.class);

	//tell queue is clustered so that anyone can pick messages off it
	private IQueue<IMessage<Object>> sendMessageQueue;
	
	//ask queue is always local to facilitate speed over clustering
	private Queue<IMessage<Object>> askMessageQueue;

	private String messageBoxName;

	public DefaultMessageBox(String messageBoxName) {
		this.messageBoxName = messageBoxName;
	}

	public void initialize(ICorexoSystem corexoSystem) throws BaseCorexoException {
		if (LOG.isInfoEnabled()) {
			LOG.info("Initializing {}", this.messageBoxName);
		}
		super.initialize(corexoSystem);
		this.sendMessageQueue = this.getHazelcastInstance().getQueue(CorexoCoreConstants.DEFAULT_SEND_MESSAGE_BOX);
		this.askMessageQueue = new LinkedBlockingQueue<IMessage<Object>>();
		if (LOG.isInfoEnabled()) {
			LOG.info("{} initialized sucessfully.", this.messageBoxName);
		}
	}

	public int getPendingMessageCount() {
		return this.sendMessageQueue.size();
	}
	
	public void addMessage(IMessage<Object> message) throws Exception {
		try {
			this.sendMessageQueue.add(message);
		} catch (Exception e) {
			throw e;
		}
	}

	public void addAskMessage(IMessage<Object> message) throws Exception {
		try {
			this.askMessageQueue.add(message);
		} catch (Exception e) {
			throw e;
		}
	}
}
