package com.herath.corexo.core.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;

import info.jerrinot.subzero.SubZero;

public class AskTest1Producer1 {

	public static void main(String[] args) {
		Config hzConfig = new Config();
		// hzConfig.setInstanceName(instanceName);
		hzConfig.setProperty("hazelcast.logging.type", "log4j2");
		hzConfig.setProperty("hazelcast.jmx", "true");
		hzConfig.setProperty("hazelcast.io.thread.count", "15");
		SubZero.useAsGlobalSerializer(hzConfig);
		HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance(hzConfig);

		IMap<String, String> hzMap = hzInstance.getMap("sample-map");
		IQueue<String> hzQueue = hzInstance.getQueue("sample-queue");

		ExecutorService producerExecutorService = Executors.newFixedThreadPool(15);

		int max = 1000000;

		for (int i = 0; i < max; i++) {
			hzMap.put("key-" + i, "msg-" + i);
		}

		System.out.println("Submitted all producers . map size = " + hzMap.size());
	}

	static class Worker implements Runnable {

		IMap<String, String> hzMap;
		IQueue<String> hzQueue;

		public Worker(IQueue<String> hzQueue, IMap<String, String> hzMap) {
			this.hzQueue = hzQueue;
			this.hzMap = hzMap;
		}

		@Override
		public void run() {
			while (true) {
				try {
					String input = this.hzQueue.take();// thsi blocks
					if (null != input) {
						int intValue = Integer.valueOf(input);
						this.hzMap.put("key-" + intValue, "msg-" + intValue);
						System.out.println("put done for int " + intValue);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
}
