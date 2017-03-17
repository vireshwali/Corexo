package com.herath.corexo.core.main;

import java.util.concurrent.TimeUnit;

import com.hazelcast.core.HazelcastInstance;
import com.herath.corexo.core.dispatch.IMessageBoxManager;
import com.herath.corexo.core.dispatch.IMessageIdGenerator;
import com.herath.corexo.core.exceptions.BaseCorexoException;
import com.herath.corexo.core.system.BaseCorexoComponent;
import com.herath.corexo.core.system.CorexoSystem;
import com.herath.corexo.core.system.ICorexoSystem;
import com.herath.corexo.core.utils.CorexoCoreConstants;
import com.herath.corexo.core.utils.CorexoTimeOut;
import com.herath.corexo.core.workers.IWorker;

public class BootStrap extends BaseCorexoComponent {
	
	public static void main(String[] args) throws BaseCorexoException, InterruptedException {
		ICorexoSystem corexoMessageSystem = CorexoSystem.getInstance();
		BootStrap b = new BootStrap();
		b.initialize(corexoMessageSystem);
		//corexoMessageSystem.initialize();
		HazelcastInstance hzInstance = b.getHazelcastInstance();
		IMessageIdGenerator messageIdGenerator = b.getDefaultMessageIdGenerator();
		System.out.println(messageIdGenerator.getMessageId());
		IMessageBoxManager<Object> messageBoxManager = b.corexoSystemContext.getMessageBox(CorexoCoreConstants.DEFAULT_SEND_MESSAGE_BOX, Object.class);
		
		for(int i=0;  i<1; i++) {
			String msg = "message1_" + i;
			messageBoxManager.ask(msg, Worker.class, Worker.class, new  CorexoTimeOut(4, TimeUnit.SECONDS));
		}
		
		Thread.sleep(30000);
		System.out.println(messageBoxManager.getPendingMessageCount());
		//corexoMessageSystem.shutDown();
	}

	private class Worker implements IWorker {
		
	}
}
