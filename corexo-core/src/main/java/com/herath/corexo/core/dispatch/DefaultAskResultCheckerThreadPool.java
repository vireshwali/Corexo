package com.herath.corexo.core.dispatch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.herath.corexo.core.message.DefaultMessage;
import com.herath.corexo.core.message.IMessage;
import com.herath.corexo.core.utils.CorexoTimeOut;

public class DefaultAskResultCheckerThreadPool<V> {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultAskResultCheckerThreadPool.class);

	private ThreadFactory threadFactory;

	private IMessageBox<V> messageBox;
	
	// this executor checks the message box for the answer to an ask operation.
	// this executor also cancels the future that checks the message box for
	// result
	// of an ask operation.
	private ScheduledExecutorService askResultTrackerExecutor;

	public DefaultAskResultCheckerThreadPool(IMessageBox<V> messageBox) {
		this.threadFactory = new CorexoThreadFactory("corexo-ask-result-checker");
		this.askResultTrackerExecutor = Executors.newScheduledThreadPool(20, threadFactory);
		this.messageBox = messageBox;
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.herath.corexo.core.dispatch.IDispatcherThreadPool#submit(java.util.
	 * concurrent.Callable)
	 */
	@SuppressWarnings("unchecked")
	public Future<IMessage<V>> submitResultChecker(IMessage<V> message, final CorexoTimeOut timeOut) {
		BlockingQueue<V> blockingQueue = new LinkedBlockingQueue<>(1);
		
		this.askResultTrackerExecutor.submit(new Runnable() {
			
			@Override
			public void run() {
				//add the mesage to the message box
				try {
					messageBox.addAskMessage(message);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});		
		
		
		// submit the checker for the result
		ScheduledFuture<IMessage<V>> future = (ScheduledFuture<IMessage<V>>) this.askResultTrackerExecutor
				.scheduleAtFixedRate(new Runnable() {
					
					AtomicInteger i = new AtomicInteger();
					
					@Override
					public void run() {
						// TODO fix a better logic to do this.
						for(int i=0; i<20 ;i++) {
							//System.out.println("i=" + i);
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("---------------------");
						i.getAndIncrement();
						if(i.get() == 10000) {
							blockingQueue.add((V) "dd");
						}
					}
				}, 10, 20, TimeUnit.MILLISECONDS);

		// submit a time tracker to cancel the future if it didn't finish within
		// timeout
		try{
			this.askResultTrackerExecutor.schedule(new Runnable() {
	
				@Override
				public void run() {
					if ((null != future) && (!future.isDone())) {
						// TODO: shoudl we force a cancel by passing true instead of
						// false?
						try {
							V data = blockingQueue.take();
							System.out.println("data: " + data);
							System.out.println("done:");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							future.cancel(false);
						}
					}
				}
			}, timeOut.getInterval(), timeOut.getTimeUnit());
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return future;
	}
}
