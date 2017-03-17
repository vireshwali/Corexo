package com.herath.corexo.core.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;

import info.jerrinot.subzero.SubZero;

public class AskTest1Consumer1 {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Config hzConfig = new Config();
		// hzConfig.setInstanceName(instanceName);
		hzConfig.setProperty("hazelcast.logging.type", "log4j2");
		hzConfig.setProperty("hazelcast.jmx", "true");
		hzConfig.setProperty("hazelcast.io.thread.count", "15");
		SubZero.useAsGlobalSerializer(hzConfig);
		HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance(hzConfig);
		IMap<String, String> hzMap = hzInstance.getMap("sample-map");
		IQueue<String> hzQueue = hzInstance.getQueue("sample-queue");

		ScheduledExecutorService askResultTrackerExecutor = Executors.newScheduledThreadPool(20);
		ExecutorService askResultTrackerExecutor2 = Executors.newFixedThreadPool(20);

		// ExecutorService askResultTrackerExecutor2 =
		// Executors.newCachedThreadPool();

		List<Future<String>> futureList = new ArrayList<Future<String>>();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		int max = 1000000;

		for (int i = 0; i < max; i++) {
			// Thread.sleep(50);
			String key = "key-" + i;

			// hzQueue.put("" + i);

			final int j = i;
			final BlockingQueue<String> resultQueue = new LinkedBlockingQueue<>(1);

			final ScheduledFuture<?> checkerFuture = askResultTrackerExecutor.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {
					String value = hzMap.get(key);
					// System.out.println("trying in i = " + j + " count is " +
					// count);
					if (null != value) {
						this.putOnResultQueue(value, 1);
					}
				}

				private void putOnResultQueue(String value, int attempt) {
					if (attempt <= 3) {
						try {
							if (attempt > 1) {
								System.out.println("Attept " + attempt + " for i = " + j);
							}
							//System.out.println("Got Value as " + value + " in i = " + j);
							resultQueue.put(value);
							// System.out.println("Done Value as " + value + "
							// in i = " + j);
							// throw new CompletedException();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							System.err.println("exception in put for attempt = " + attempt + " for i = " + j);
							e.printStackTrace();
							this.putOnResultQueue(value, ++attempt);
						}
					}
				}

			}, 0, 20, TimeUnit.MILLISECONDS);

			try {
				Future<String> resultFuture = askResultTrackerExecutor2.submit(new Callable<String>() {

					@Override
					public String call() throws Exception {
						String data = null;
						if ((null != checkerFuture) && (!checkerFuture.isDone())) {
							try {
								StopWatch sw = new StopWatch();
								sw.start();

								data = resultQueue.poll(20, TimeUnit.MILLISECONDS);
								while (data == null) {
									//System.out.println("in j = " + j + " " +resultQueue.size());
									data = resultQueue.poll(20, TimeUnit.MILLISECONDS);

									if (null == data) {
										long spentTimeInMillies = sw.getTime(TimeUnit.MILLISECONDS);
										if (spentTimeInMillies >= 4000) {
											sw.stop();
											System.out.println("time (" + spentTimeInMillies + ") up in j = " + j);

											break;
										}
									} else {
										// System.out.println("Got data as " +
										// data);
										// return data;
									}
								}
								//System.out.println("broke in j = " + j + " data = " + data);
							} catch (Throwable e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								checkerFuture.cancel(true);
							}
						}
						return data;
					}
				});

				futureList.add(resultFuture);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("Submitted all futureList size is " + futureList.size());

		int i = 0;
		int nullCount = 0;
		for (Future<String> future2 : futureList) {
			try {
				String data = future2.get();
				if (null == data) {
					nullCount++;
					System.out.println("i = " + (i++) + ", got data as " + data);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("i = " + (i++) + ", got data as 'null'");
			}
		}
		System.out.println("Total nulls = " + nullCount);
		stopWatch.stop();
		System.out.println("done all...stopwatch time=" + stopWatch.getTime());
	}
}
