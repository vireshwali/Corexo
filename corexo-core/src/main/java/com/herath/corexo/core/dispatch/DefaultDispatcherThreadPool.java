package com.herath.corexo.core.dispatch;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDispatcherThreadPool implements IDispatcherThreadPool {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultDispatcherThreadPool.class);

	private ThreadFactory threadFactory;

	private ExecutorService executorService;

	public DefaultDispatcherThreadPool() {
		this.threadFactory = new CorexoThreadFactory("corexo-default-dispatcher");
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 20, 60, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(), this.threadFactory);
		threadPoolExecutor.allowCoreThreadTimeOut(true);
		this.executorService = threadPoolExecutor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.herath.corexo.core.dispatch.IDispatcherThreadPool#submit(java.lang.
	 * Runnable)
	 */
	public void submit(Runnable runnable) {
		this.executorService.execute(runnable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.herath.corexo.core.dispatch.IDispatcherThreadPool#submit(java.util.
	 * concurrent.Callable)
	 */
	public <V> Future<V> submit(Callable<V> task) {
		return this.executorService.submit(task);
	}
}
