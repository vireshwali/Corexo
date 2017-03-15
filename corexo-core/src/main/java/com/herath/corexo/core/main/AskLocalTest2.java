package com.herath.corexo.core.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

public class AskLocalTest2 {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Map<String, String> resultsMap = new ConcurrentHashMap<>();
		BlockingQueue<String> askQueue = new LinkedBlockingQueue<String>();
		
		ScheduledExecutorService askResultTrackerExecutor = Executors.newScheduledThreadPool(20);
		ExecutorService askResultTrackerExecutor2 = Executors.newFixedThreadPool(20);
		
		//ExecutorService askResultTrackerExecutor2 = Executors.newCachedThreadPool();
		
		List<Future<String>> futureList = new ArrayList<Future<String>>();

		ExecutorService producerExecutorService = Executors.newFixedThreadPool(20);
		
		int max = 1000000;
		
		//producer's workers to pick form queue and add to map
		/*for (int i = 0; i < max; i++) {
			Worker w = new Worker(askQueue, resultsMap);
			producerExecutorService.execute(w);
			//resultsMap.put("key-" + i, "msg-" + i);
		}*/
		
		
		//add ask messages to queue
		for(int i=0; i<max; i++) {
			askQueue.add(""+i);
		}
		
		System.out.println("queue add finished, queue size = " + askQueue.size());

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for(int i=0; i<max; i++) {
			//Thread.sleep(50);
			
			//submit the task
			Worker w = new Worker(askQueue, resultsMap);
			producerExecutorService.execute(w);
			
			String key = "key-" + i;
			
			final int j  = i;
			final BlockingQueue<String> resultQueue = new LinkedBlockingQueue<>(1);
			
			final ScheduledFuture<?> checkerFuture = askResultTrackerExecutor.scheduleWithFixedDelay(new Runnable() {
				
				@Override
				public void run() {
					String value = resultsMap.get(key);
					//System.out.println("trying in i = " + j + " count is " + count);
					if(null != value) {
						this.putOnResultQueue(value, 1);
					}
				}
				
				private void putOnResultQueue(String value, int attempt) {
					if(attempt <=3) {
						try {
							if(attempt > 1) {
								System.out.println("Attept " + attempt + " for i = " + j);
							}
							//System.out.println("Got Value as " + value + " in i = " + j);
							resultQueue.put(value);
							//System.out.println("Done Value as " + value + " in i = " + j);
							//throw new CompletedException();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							System.err.println("exception in put for attempt = " + attempt + " for i = " + j);
							e.printStackTrace();
							this.putOnResultQueue(value, ++attempt);
						}
					}
				}
				
			}, 0, 20, TimeUnit.MILLISECONDS);
			
			//System.out.println("df");
			
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
										if (spentTimeInMillies >= 2000) {
											sw.stop();
											System.out.println("time ("+ spentTimeInMillies +") up in j = " + j);
											
											break;
										}
									} else {
										//System.out.println("Got data as " + data);
										//return data;
									}
								}
								//System.out.println("broke in j = " + j + " data = "  + data);
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
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
		System.out.println("Submitted all futureList size is " + futureList.size());
		int i = 0;
		int nullCount = 0;
		for (Future<String> future2 : futureList) {
			try {
				String data = future2.get();
				if(null == data) {
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
		/*askResultTrackerExecutor.shutdownNow();
		askResultTrackerExecutor2.shutdownNow();
		askResultTrackerExecutor.awaitTermination(2, TimeUnit.SECONDS);
		askResultTrackerExecutor2.awaitTermination(2, TimeUnit.SECONDS);*/
	}
	
	static class Worker implements Runnable {

		Map<String, String> resultsMap;
		BlockingQueue<String> askQueue;

		public Worker(BlockingQueue<String> askQueue, Map<String, String> resultsMap) {
			this.askQueue = askQueue;
			this.resultsMap = resultsMap;
		}

		@Override
		public void run() {
			while (true) {
				try {
					String input = this.askQueue.take();//thsi blocks
					if (null != input) {
						int intValue = Integer.valueOf(input);
						this.resultsMap.put("key-" + intValue, "msg-" + intValue);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
}


