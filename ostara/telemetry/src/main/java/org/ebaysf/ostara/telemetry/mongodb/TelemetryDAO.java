/*******************************************************************************
 * Copyright (c) 2014 eBay Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.ebaysf.ostara.telemetry.mongodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

public class TelemetryDAO {

	private static TelemetryDAO instance;

	public Datastore ds = null;

	public static TelemetryDAO getInstance() {
		return getInstance(null);
	}

	public static TelemetryDAO getInstance(String mongoHostOverride) {
		if(instance == null) {
			instance = new TelemetryDAO(mongoHostOverride);
		}
		return instance;
	}
	
	protected TelemetryDAO() {
		this(null);
	}

	protected TelemetryDAO(String mongoHostOverride) {
		try {
			ds = Database.getAppDatastore(mongoHostOverride);
			System.out.println("Using servers "
					+ ds.getDB().getMongo().getAllAddress());
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	
	protected Datastore getDataSource() {
		return ds;
	}

	public void insertData(TelemetryData td) {
		Datastore ds = getDataSource();
		ds.save(td);
	}
	
	public void insertData(ArtifactsData ad) {
		Datastore ds = getDataSource();
		ds.save(ad);
	}

	public List<TelemetryData> getAllData() {
		Datastore ds = getDataSource();
		Query<TelemetryData> q = ds.find(TelemetryData.class);
		List<TelemetryData> dataList = q.asList();
		return dataList;
	}

	public TelemetryData getData(String id) {
		Datastore ds = getDataSource();
		return ds.get(TelemetryData.class, id);
	}

	public List<TelemetryData> getDataByAttribute(String key, String value) {
		Datastore ds = getDataSource();
		Query<TelemetryData> q = ds.find(TelemetryData.class, key, value);
		List<TelemetryData> dataList = q.asList();
		return dataList;
	}

	public void updateData(String id, TelemetryData data) {
		Datastore ds = getDataSource();
		TelemetryData savedData = ds.get(TelemetryData.class, id);
		savedData.setAppName(data.getAppName());
	}
	
	public TelemetryData getDatabyTaskId(String taskId) {
		Datastore ds = getDataSource();
		Query<TelemetryData> q = ds.find(TelemetryData.class, "taskId", taskId);
		TelemetryData td = null;
		if(q.asList().size() > 0) {
			td = q.get();
		}
		return td;
	}
	
	public long getDocumentCount() {
		Datastore ds = getDataSource();
		return ds.getCount(TelemetryData.class);
	}
	
	public Collection<String> getUniqueMissingArtifacts() {
		Set<String> list = new HashSet<String>();
		Datastore ds = getDataSource();
		Query<ArtifactsData> q = ds.find(ArtifactsData.class);
		List<ArtifactsData> dataList = q.asList();
		
		for(ArtifactsData data : dataList) {
			list.add(data.getGroupId() + ":" + data.getArtifactId());
		}
		return list;
	}
	
	public List<ArtifactsData> getAllMissingArtifacts() {
		Datastore ds = getDataSource();
		Query<ArtifactsData> q = ds.find(ArtifactsData.class);
		List<ArtifactsData> dataList = q.asList();		
		return dataList;
	}
	
	public List<ArtifactsData> getMissingArtifactsByTaskId(String taskId) {
		Datastore ds = getDataSource();
		Query<ArtifactsData> q = ds.find(ArtifactsData.class, "taskId", taskId);
		return q.asList();
	}
}
