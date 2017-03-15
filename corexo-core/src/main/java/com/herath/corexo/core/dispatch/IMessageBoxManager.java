/**
 * 
 */
package com.herath.corexo.core.dispatch;

import java.util.concurrent.Future;

import com.herath.corexo.core.exceptions.MessageBoxException;
import com.herath.corexo.core.message.IMessage;
import com.herath.corexo.core.system.ICorexoComponentLifeCycle;
import com.herath.corexo.core.utils.CorexoTimeOut;
import com.herath.corexo.core.workers.IWorker;

/**
 * @author vwali
 *
 */
public interface IMessageBoxManager<T> extends ICorexoComponentLifeCycle {

	public String getName();

	int getPendingMessageCount();
	
	Future<IMessage<Object>> ask(final Object messageBody, Class<? extends IWorker> toWorkerType,
			Class<? extends IWorker> fromWorkerType, CorexoTimeOut timeOut) throws MessageBoxException;
}
