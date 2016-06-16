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
package org.ebaysf.ostara.upgrade.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class GitUtils {

	protected static final Logger LOG = Logger.getLogger(GitUtils.class);

	public static Collection<String> getListOfGitCommitters(String gitUrl) {
		Set<String> committers = new HashSet<>(); // Need unique entries
		
		if(gitUrl == null) return committers;
		
		String[] parts = gitUrl.split("(/)|(:)|(@)");

		if (parts.length < 4) {
			LOG.warn("Malformed git url " + gitUrl);
			return committers;
		}

		// Get date 1 year ago
		String date = getISODate(1);

		String url = "https://github.com/api/v3/repos/"
				+ parts[parts.length - 2]
				+ "/"
				+ parts[parts.length - 1].substring(0,
						parts[parts.length - 1].length() - 4)
				+ "/commits?since=" + date;

		try {
			StringBuilder sb = readData(committers, url);
			
			// Malformed url
			if (sb == null) {
				return committers;
			}
			JSONArray ar = new JSONArray(sb.toString());
			
			// if no committers  found in last 1 year then check last 2 year 
			if (ar.length() == 0) {
				date = getISODate(2);
				url = "https://github.com/api/v3/repos/"
						+ parts[parts.length - 2]
						+ "/"
						+ parts[parts.length - 1].substring(0,
								parts[parts.length - 1].length() - 4)
						+ "/commits?since=" + date;
				sb = readData(committers, url);
				ar = new JSONArray(sb.toString());
			}			

			for (int i = 0; i < ar.length(); i++) {
				JSONObject json = (JSONObject) ar.get(i);
				json = (JSONObject) json.get("commit");
				json = (JSONObject) json.get("author");
				committers.add(json.get("email").toString());
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return committers;
	}

	private static String getISODate(int i) {
		DateFormat df = new SimpleDateFormat("YYYY-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -i);
		String date = df.format(cal.getTime());
		return date;
	}

	private static StringBuilder readData(Set<String> committers, String url)
			throws IOException, ClientProtocolException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(response1.getEntity().getContent())));

		if (response1.getStatusLine().getStatusCode() != 200) {
			LOG.warn("Unable to get git committers for " + url
					+ " status code is "
					+ response1.getStatusLine().getStatusCode());
			return null;
		}

		StringBuilder sb = new StringBuilder();
		String output;
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		return sb;
	}

}
