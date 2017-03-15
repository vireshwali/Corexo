package com.herath.corexo.core.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.time.StopWatch;

public class TestMain4 {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		
		ScheduledExecutorService askResultTrackerExecutor = Executors.newScheduledThreadPool(10);
		
		ExecutorService askResultTrackerExecutor2 = Executors.newFixedThreadPool(10);
		
		//ExecutorService askResultTrackerExecutor2 = Executors.newCachedThreadPool();
		
		List<Future<String>> futureList = new ArrayList<Future<String>>();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		List<Long> timeouts = new ArrayList<>();
		
		int max = 1000000;
		
		timeouts.add(2L);
		timeouts.add(4L);
		timeouts.add(5L);
		timeouts.add(8L);
		timeouts.add(10L);
		timeouts.add(15L);
		
		int ti = 0;
		
		for(int i=0; i<max; i++) {
			//Thread.sleep(50);
			String msg = "msg_" + i;
			final int j  = i;
			BlockingQueue<String> resultQueue = new ArrayBlockingQueue<>(1);
			
			final ScheduledFuture<?> checkerFuture = askResultTrackerExecutor.scheduleWithFixedDelay(new Runnable() {
				
				private AtomicInteger ai = new AtomicInteger(0);
				private int target = j * 10;
				
				@Override
				public void run() {
					boolean done = (ai.incrementAndGet() == 3);
					int k = 0;
					for(int j=0; j<=target; j++) {
						k = j; // dummy operation.
					}
					try {
						if(done){
							resultQueue.put(msg + "--" + k);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
			}, 0, 10, TimeUnit.MILLISECONDS);
			
			
			Future<String> resultFuture = null;
			try {
				resultFuture = askResultTrackerExecutor2.submit(new Callable<String>() {

					@Override
					public String call() throws Exception {
						if ((null != checkerFuture) && (!checkerFuture.isDone())) {
							try {
								long startTime = System.nanoTime();
								long currentTime = 0;
								while (true) {
									String data = resultQueue.poll(10, TimeUnit.MILLISECONDS);
									if (null != data) {
										return data;
									} else {
										currentTime = System.nanoTime();
										long spentTime = currentTime - startTime;
										long timeInSec = TimeUnit.NANOSECONDS.toSeconds(spentTime);
										if (timeInSec >= 2) {
											System.out.println("time up in j=" + j);
											break;
										}
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								checkerFuture.cancel(true);
							}
						}
						return null;
					}
				});

			}catch (Exception e) {
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
				System.out.println("i = " + (i++) + ", got data as null");
			}
		}
		stopWatch.stop();
		System.out.println("done all...stopwatch time=" + stopWatch.getTime());
		askResultTrackerExecutor.shutdownNow();
		askResultTrackerExecutor2.shutdownNow();
		askResultTrackerExecutor.awaitTermination(2, TimeUnit.SECONDS);
		askResultTrackerExecutor2.awaitTermination(2, TimeUnit.SECONDS);
	}
}
