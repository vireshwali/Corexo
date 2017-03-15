package com.herath.corexo.core.dispatch;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import com.herath.corexo.core.utils.CorexoCoreConstants;

public class CorexoThreadFactory implements ThreadFactory {

	private AtomicLong threadCount;

	private String threadNamePrefix;

	public CorexoThreadFactory(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix;
		this.threadCount = new AtomicLong(1);
	}

	public Thread newThread(Runnable runnable) {
		String threadName = this.threadNamePrefix + CorexoCoreConstants.HYPHEN + this.threadCount.getAndIncrement();
		return new Thread(runnable, threadName);
	}

	

}
