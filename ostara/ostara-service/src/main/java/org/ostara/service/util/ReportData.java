package org.ostara.service.util;

import java.util.List;

import org.ebaysf.ostara.telemetry.mongodb.TelemetryData;

public class ReportData {

	private List<TelemetryData> data;
	private List<TelemetryData> uniqueData;
	private List<TelemetryData> allData;
	private int count;
	private int failCount;
	private int successCount;
	private int uniqueCount;
	
	public List<TelemetryData> getData() {
		return data;
	}
	public void setData(List<TelemetryData> data) {
		this.data = data;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getFailCount() {
		return failCount;
	}
	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}
	public int getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}
	public int getUniqueCount() {
		return uniqueCount;
	}
	public void setUniqueCount(int uniqueCount) {
		this.uniqueCount = uniqueCount;
	}
	public List<TelemetryData> getUniqueData() {
		return uniqueData;
	}
	public void setUniqueData(List<TelemetryData> uniqueData) {
		this.uniqueData = uniqueData;
	}
	public List<TelemetryData> getAllData() {
		return allData;
	}
	public void setAllData(List<TelemetryData> allData) {
		this.allData = allData;
	}
	
}
