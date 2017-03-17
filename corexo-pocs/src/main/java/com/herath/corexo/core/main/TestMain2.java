package com.herath.corexo.core.main;

import java.util.ArrayList;
import java.util.List;
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

public class TestMain2 {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		
		ScheduledExecutorService askResultTrackerExecutor = Executors.newScheduledThreadPool(40);
		
		//ScheduledExecutorService askResultTrackerExecutor2 = Executors.newScheduledThreadPool(400);
		
		ExecutorService askResultTrackerExecutor2 = Executors.newCachedThreadPool();
		
		List<Future<String>> futureList = new ArrayList<Future<String>>();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		List<Long> timeouts = new ArrayList<>();
		
		int max = 20000;
		
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
			
			List<String> resultHolder = new ArrayList<>(1);
			
			ScheduledFuture<?> scheduledFuture = askResultTrackerExecutor.scheduleWithFixedDelay(new Runnable() {
				
				private AtomicInteger i = new AtomicInteger();
				
				@Override
				public void run() {
					try{
						// TODO fix a better logic to do this.
						i.getAndIncrement();
						//System.out.println("msg = "+ msg +" , i = " + i.get());
						if(i.get() == 800) {
							//System.out.println("added to blocking queue for msg " + msg);
							resultHolder.add(msg);
						}
					}catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}, 0, 20, TimeUnit.MILLISECONDS);
			
			Future<String> future = null;
			if(ti == 5) {
				ti = 0;
			} else {
				ti++;
			}
			final int k = ti;
			final int j = i;
			try{
				future = askResultTrackerExecutor2.submit(new Callable<String>() {

					@Override
					public String call() throws Exception {
						String data = null;
						
						if ((null != scheduledFuture) && (!scheduledFuture.isDone())) {
							StopWatch s = new StopWatch();
							// TODO: shoudl we force a cancel by passing true instead of
							// false?
							try {
								boolean broke = false;
								s.start();
								while(resultHolder.size() == 0) {
									//System.out.println("using k = " + k + " for i = " + j);
									try{
										Thread.sleep(100);
										long time = s.getTime(TimeUnit.SECONDS);
										if(time >= timeouts.get(k)) {
											//System.out.println("broke j = " + j + " time = " + time);
											broke = true;
											break;
										}
									}catch (Exception e) {
										e.printStackTrace();
									}
								}
								if(!broke) {
									data = resultHolder.get(0);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								s.stop();
								scheduledFuture.cancel(true);
							}
						}
						return data;
					}
				});
			}catch (Exception e) {
				e.printStackTrace();
			} 
			
			futureList.add(future);
		}
		System.out.println("Submitted all");
		int i = 0;
		for (Future<String> future2 : futureList) {
			System.out.println("i = " + (i++) + ", got data as " + future2.get());
		}
		stopWatch.stop();
		System.out.println("done all...stopwatch time=" + stopWatch.getTime());
	}
}
