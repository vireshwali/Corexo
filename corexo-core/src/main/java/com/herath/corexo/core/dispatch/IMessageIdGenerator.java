package com.herath.corexo.core.dispatch;

import com.herath.corexo.core.system.ICorexoComponentLifeCycle;

public interface IMessageIdGenerator extends ICorexoComponentLifeCycle {

	String getMessageId();

	String getMessageId(String messageIdKey);

}