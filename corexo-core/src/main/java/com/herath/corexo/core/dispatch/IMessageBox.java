package com.herath.corexo.core.dispatch;

import com.herath.corexo.core.message.IMessage;
import com.herath.corexo.core.system.ICorexoComponentLifeCycle;

public interface IMessageBox<T> extends ICorexoComponentLifeCycle {

	int getPendingMessageCount();

	void addMessage(IMessage<T> message) throws Exception;

	void addAskMessage(IMessage<T> message) throws Exception;
}