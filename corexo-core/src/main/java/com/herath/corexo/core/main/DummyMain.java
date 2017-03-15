package com.herath.corexo.core.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

public class DummyMain {

	public static void main(String[] args) throws InterruptedException {
		BlockingQueue<String> askQueue = new LinkedBlockingQueue<String>();

		ExecutorService askResultTrackerExecutor2 = Executors.newFixedThreadPool(15);

		int max = 1000000;
		// add ask messages to queue
		for (int i = 0; i < max; i++) {
			askQueue.add("msg-" + i);
		}

		System.out.println("queue add finished, queue size = " + askQueue.size());

		List<Future<String>> futureList = new ArrayList<Future<String>>();
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for (int i = 0; i < max; i++) {
			// Thread.sleep(50);
			String key = "key-" + i;
			int j = i;
			Future<String> resultFuture = null;
			try {
				resultFuture = askResultTrackerExecutor2.submit(new Callable<String>() {

					@Override
					public String call() throws Exception {
						try {
							StopWatch sw = new StopWatch();
							sw.start();
							while (true) {
								String data = askQueue.poll(10, TimeUnit.MILLISECONDS);
								if (null != data) {
									return data;
								} else {
									long spentTimeInMillies = sw.getTime(TimeUnit.MILLISECONDS);
									if (spentTimeInMillies >= 500) {
										System.out.println("time up in j=" + j);
										break;
									}
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						return null;
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}

			futureList.add(resultFuture);
		}
		System.out.println("Submitted all");
		int i = 0;
		for (Future<String> future2 : futureList) {
			try {
				System.out.println("i = " + (i++) + ", got data as " + future2.get());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("i = " + (i++) + ", got data as 'null'");
			}
		}
		stopWatch.stop();
		System.out.println("done all...stopwatch time=" + stopWatch.getTime());
	}

}
