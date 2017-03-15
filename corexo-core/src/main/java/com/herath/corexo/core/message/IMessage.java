package com.herath.corexo.core.message;

import com.herath.corexo.core.workers.IWorker;

public interface IMessage<T> {
	String getMessageId();

	T getBody();

	void setBody(T body);

	Class<? extends IWorker> getToWorkerType();

	Class<? extends IWorker> getFromWorkerType();
}
