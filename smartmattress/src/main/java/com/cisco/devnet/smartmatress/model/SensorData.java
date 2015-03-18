package com.cisco.devnet.smartmatress.model;

import java.util.Date;

public class SensorData {

	private Date date;
	private long port0;
	private long port1;
	
	public SensorData(Date date, long port0, long port1) {
		this.date = date;
		this.port0 = port0;
		this.port1 = port1;
	}
	
	public Date getDate() {
		return date;
	}
	public long getPort0() {
		return port0;
	}
	public long getPort1() {
		return port1;
	}
}
