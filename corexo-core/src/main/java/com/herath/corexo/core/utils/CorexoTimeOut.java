package com.herath.corexo.core.utils;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class CorexoTimeOut implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4336183445475006376L;

	private long interval = 1;
	private TimeUnit timeUnit = TimeUnit.SECONDS;

	public CorexoTimeOut() {
		// TODO Auto-generated constructor stub
	}

	public CorexoTimeOut(long interval, TimeUnit timeUnit) {
		super();
		this.interval = interval;
		this.timeUnit = timeUnit;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

}
