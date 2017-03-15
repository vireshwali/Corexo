package com.herath.corexo.core.dispatch;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface IDispatcherThreadPool {

	void submit(Runnable runnable);

	<V> Future<V> submit(Callable<V> task);

}