package com.herath.corexo.core.dispatch;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.herath.corexo.core.exceptions.BaseCorexoException;
import com.herath.corexo.core.exceptions.MessageBoxException;
import com.herath.corexo.core.message.DefaultMessage;
import com.herath.corexo.core.message.IMessage;
import com.herath.corexo.core.system.BaseCorexoComponent;
import com.herath.corexo.core.system.ICorexoSystem;
import com.herath.corexo.core.utils.CorexoCoreConstants;
import com.herath.corexo.core.utils.CorexoTimeOut;
import com.herath.corexo.core.workers.IWorker;

public class DefaultMessageBoxManager extends BaseCorexoComponent implements IMessageBoxManager<Object> {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageBoxManager.class);

	private static final String SEND_PUBLISH_ERROR_MESSAGE = "Stopped 'send' message publish. Following errors were discovered. ";
	private static final String ASK_PUBLISH_ERROR_MESSAGE = "Stopped 'ask' message publish. Following errors were discovered. ";

	private IMessageBox<Object> messageBox;

	private IDispatcherThreadPool dispatcherThreadPool;

	private DefaultAskResultCheckerThreadPool<Object> askResultCheckScheduledExecutor;
	
	public DefaultMessageBoxManager() {
		this.messageBox = new DefaultMessageBox(CorexoCoreConstants.DEFAULT_SEND_MESSAGE_BOX);
	}

	public void initialize(ICorexoSystem corexoSystem) throws BaseCorexoException {
		LOG.info("Initializing DefaultMessageBoxManager.");
		super.initialize(corexoSystem);
		this.messageBox.initialize(corexoSystem);
		this.dispatcherThreadPool = new DefaultDispatcherThreadPool();
		this.askResultCheckScheduledExecutor = new DefaultAskResultCheckerThreadPool<>(messageBox);
		LOG.info("DefaultMessageBoxManager created successfully.");
	}

	public void postConstruct() throws BaseCorexoException {
		this.messageBox.postConstruct();
	}

	public void preShutDown() throws BaseCorexoException {
		this.messageBox.preShutDown();
	}

	public void shutDown() throws BaseCorexoException {
		this.messageBox.shutDown();
	}

	public String getName() {
		return CorexoCoreConstants.DEFAULT_SEND_MESSAGE_BOX;
	}

	public int getPendingMessageCount() {
		return this.messageBox.getPendingMessageCount();
	}

	// send overloaded messages
	public void send(final Object messageBody, Class<? extends IWorker> toWorkerType,
			Class<? extends IWorker> fromWorkerType) throws MessageBoxException {
		StringBuilder errorMessages = null;
		// first validate the inputs.
		if (null == toWorkerType) {
			if (errorMessages == null) {
				errorMessages = new StringBuilder("Error Messages: ");
			}
			errorMessages.append("['toWorkerType' cannot be null.]");
		}

		if (null != errorMessages) {
			throw new MessageBoxException(SEND_PUBLISH_ERROR_MESSAGE + errorMessages.toString());
		} else {
			// publish the message
			this.dispatcherThreadPool.submit(new Runnable() {

				public void run() {
					String messageId = getDefaultMessageIdGenerator().getMessageId();
					IMessage<Object> message = new DefaultMessage(messageId, messageBody, toWorkerType, fromWorkerType);
					publishSendMessage(message);
				}
			});
		}
	}

	public void send(final Object messageBody, IWorker toWorker, IWorker fromWorker) throws MessageBoxException {
		StringBuilder errorMessages = null;
		// first validate the inputs.
		if (null == toWorker) {
			if (errorMessages == null) {
				errorMessages = new StringBuilder("Error Messages: ");
			}
			errorMessages.append("['toWorker' cannot be null.]");
		}

		if (null != errorMessages) {
			throw new MessageBoxException(SEND_PUBLISH_ERROR_MESSAGE + errorMessages.toString());
		} else {
			this.dispatcherThreadPool.submit(new Runnable() {

				public void run() {
					String messageId = getDefaultMessageIdGenerator().getMessageId();
					Class<? extends IWorker> toWorkerType = toWorker.getClass();
					Class<? extends IWorker> fromWorkerType = fromWorker.getClass();
					IMessage<Object> message = new DefaultMessage(messageId, messageBody, toWorkerType, fromWorkerType);
					publishSendMessage(message);
				}
			});
		}
	}

	public void send(final IMessage<Object> message) throws MessageBoxException {
		StringBuilder errorMessages = null;
		// first validate the inputs.
		if (null == message) {
			if (errorMessages == null) {
				errorMessages = new StringBuilder("Error Messages: ");
			}
			errorMessages.append("['message' cannot be null.] ");
		} else if (null == message.getToWorkerType()) {
			if (errorMessages == null) {
				errorMessages = new StringBuilder("Error Messages: ");
			}
			errorMessages.append("['message.ToWorkerType' cannot be null.] ");
		}

		if (null != errorMessages) {
			throw new MessageBoxException(SEND_PUBLISH_ERROR_MESSAGE + errorMessages.toString());
		} else {
			// publish the message
			this.dispatcherThreadPool.submit(new Runnable() {

				public void run() {
					publishSendMessage(message);
				}
			});
		}
	}

	// ask overloaded methods
	public Future<IMessage<Object>> ask(final Object messageBody, Class<? extends IWorker> toWorkerType,
			Class<? extends IWorker> fromWorkerType, CorexoTimeOut timeOut) throws MessageBoxException {
		Future<IMessage<Object>> future = null; 
		StringBuilder errorMessages = null;
		// first validate the inputs.
		if (null == toWorkerType) {
			if (errorMessages == null) {
				errorMessages = new StringBuilder("Error Messages: ");
			}
			errorMessages.append("['toWorkerType' cannot be null.]");
		}
		if (null == fromWorkerType) {
			if (errorMessages == null) {
				errorMessages = new StringBuilder("Error Messages: ");
			} else {
				errorMessages.append(", ");
			}
			errorMessages.append("['fromWorkerType' cannot be null.]");
		}

		if (null != errorMessages) {
			throw new MessageBoxException(ASK_PUBLISH_ERROR_MESSAGE + errorMessages.toString());
		} else {
			
			String messageId = getDefaultMessageIdGenerator().getMessageId();
			IMessage<Object> message = new DefaultMessage(messageId, messageBody, toWorkerType, fromWorkerType);
			//messageBox.addAskMessage(message);
			
			future = this.askResultCheckScheduledExecutor.submitResultChecker(message, new CorexoTimeOut(10, TimeUnit.SECONDS));
			
			return future;
		}
	}

	private void publishSendMessage(final IMessage<Object> message) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Adding mesage with id -> {} to Message box -> {}", message.getMessageId(),
					CorexoCoreConstants.DEFAULT_SEND_MESSAGE_BOX);
		}
		try {
			messageBox.addMessage(message);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Message with id -> {} added successfully to Message box -> {}", message.getMessageId(),
						CorexoCoreConstants.DEFAULT_SEND_MESSAGE_BOX);
			}
		} catch (Exception e) {
			LOG.error("Failed to add mesage with id -> {} to Message box -> {}", message.getMessageId(),
					CorexoCoreConstants.DEFAULT_SEND_MESSAGE_BOX, e);
		}
	}

	private IMessage<Object> getResultFromFuture(Future<IMessage<Object>> future, CorexoTimeOut timeOut)
			throws MessageBoxException {
		IMessage<Object> result = null;
		try {
			result = future.get(timeOut.getInterval(), timeOut.getTimeUnit());
		} catch (InterruptedException e) {
			LOG.warn("An InterruptedException occured while waiting for result.");
			throw new MessageBoxException(e);
		} catch (ExecutionException e) {
			LOG.warn("An ExecutionException occured while waiting for result.");
			throw new MessageBoxException(e);
		} catch (TimeoutException e) {
			LOG.warn("Timed out waiting for result.");
		} catch (CancellationException e) {
			LOG.warn("An CancellationException occured while waiting for result.");
			throw new MessageBoxException(e);
		} catch (Throwable e) {
			LOG.warn("An " + e.getClass().getName() + " occured while waiting for result.");
			throw new MessageBoxException(e);
		}
		return result;
	}
}
