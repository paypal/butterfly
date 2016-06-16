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

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Arrays;

import javax.naming.NamingException;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.LoggerFactory;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

public enum Database {
	instance;

	private static final int DEFAULT_MONGO_PORT = 27017;

	public Datastore getMongoDatastore(String mongoHostOverride)
			throws UnknownHostException, MongoException, NamingException,
			SQLException {
		
		Mongo mongoClient;
		
		String tmpOverride = System.getenv("RMIGWEB_MONGO_HOST");
		
		if(tmpOverride != null) {
			LoggerFactory.getLogger(Database.class).info("Using mongo host overridden by system property: " + tmpOverride);
			mongoHostOverride = tmpOverride;
		}
		
		if(mongoHostOverride == null) {
			mongoClient = new MongoClient(Arrays.asList(
				new ServerAddress("localhost", DEFAULT_MONGO_PORT)));
		} else {
			mongoClient = new MongoClient(new ServerAddress(mongoHostOverride, DEFAULT_MONGO_PORT));
		}
		Morphia morphia = new Morphia();
		Datastore ds = morphia.createDatastore(mongoClient, "UpdateLog");
		ds.ensureIndexes();
		return ds;
	}
	
	public static Datastore getAppDatastore(String mongoHostOverride) throws UnknownHostException, MongoException, NamingException, SQLException {
		return Database.instance.getMongoDatastore(mongoHostOverride);
	}

}