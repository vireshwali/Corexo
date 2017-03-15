package com.herath.corexo.core.message;

import com.herath.corexo.core.workers.IWorker;

public class DefaultMessage implements IMessage<Object> {

	private String messageId;

	private Class<? extends IWorker> toWorkerType;

	private Class<? extends IWorker> fromWorkerType;

	private Object body;

	public DefaultMessage() {
		super();
	}

	public DefaultMessage(Object body, Class<? extends IWorker> toWorkerType,
			Class<? extends IWorker> fromWoClassType) {
		super();
		this.body = body;
	}

	public DefaultMessage(String messageId, Object body, Class<? extends IWorker> toWorkerType,
			Class<? extends IWorker> fromWoClassType) {
		super();
		this.messageId = messageId;
		this.body = body;
	}

	public String getMessageId() {
		return this.messageId;
	}

	public Object getBody() {
		return this.body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public Class<? extends IWorker> getToWorkerType() {
		return toWorkerType;
	}

	public void setToWorkerType(Class<? extends IWorker> toWorkerType) {
		this.toWorkerType = toWorkerType;
	}

	public Class<? extends IWorker> getFromWorkerType() {
		return fromWorkerType;
	}

	public void setFromWorkerType(Class<? extends IWorker> fromWorkerType) {
		this.fromWorkerType = fromWorkerType;
	}

}
