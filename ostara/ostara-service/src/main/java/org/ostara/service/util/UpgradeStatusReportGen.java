package org.ostara.service.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.ebaysf.ostara.telemetry.mongodb.ArtifactsData;
import org.ebaysf.ostara.telemetry.mongodb.TelemetryDAO;
import org.ebaysf.ostara.telemetry.mongodb.TelemetryData;
import org.ebaysf.ostara.telemetry.mongodb.UserFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpgradeStatusReportGen {
	private static final String NOT_APPLICABLE = "N/A";

	private static Logger LOG = LoggerFactory
			.getLogger(UpgradeStatusReportGen.class);
	
	private static Map<String, TelemetryData> telemetryMap = new HashMap<>();

	public static void printUsers() {
		List<TelemetryData> data = TelemetryDAO.getInstance().getAllData();
		
		Set<String> users = new HashSet<>();
		
		for (TelemetryData telemetryData : data) {
			users.add(telemetryData.getUserName());
		}
		
		List<String> sortedUsers = new ArrayList<>(users);
		
//		Collections.sort(sortedUsers);
		
		System.out.println(">>>>>>>>>>>>");
		System.out.println(StringUtils.join(sortedUsers, "\n"));
		
		System.out.println("<<<<<<<<<<<<");
	}
	
	public static ReportData getReportData() {
		
		ReportData reportData = new ReportData();
		List<TelemetryData> data = TelemetryDAO.getInstance().getAllData();
		for (TelemetryData telemetryData : data) {
			if(!telemetryMap.containsKey(telemetryData.getTaskId())) {
				telemetryMap.put(telemetryData.getTaskId(), telemetryData);
			}
		}
		reportData.setAllData(data);
		data = filterData(data);
		reportData.setData(data);
		reportData.setFailCount(getFailCount(data));
		reportData.setCount(data.size());
		List<TelemetryData> uniqueData = getUniqueUpgradeJobs(data);
		reportData.setUniqueData(uniqueData);
		reportData.setUniqueCount(uniqueData.size());
		return reportData;
	}

	private static int getFailCount(List<TelemetryData> data) {
		int count = 0;
		for (TelemetryData t : data) {
			if (t.isSuccess() == null) {
				Boolean gitPushSuccess = t.isGitPushSuccesful();
				if (gitPushSuccess != null) {
					if (gitPushSuccess == false) {
						count++;
					}
				}
			} else if (t.isSuccess() == false) {
				count++;
			}
		}
		return count;
	}

	public static String formatUserName(String userName) {
		if (userName == null) {
			return NOT_APPLICABLE;
		} else {
			return "<a href=\"http://myhub.corp.ebay.com/Person.aspx?accountname=corp%5C"
					+ userName + "\" target=\"_blank\">" + userName + "</a>";
		}
	}

	public static List<TelemetryData> filterData(List<TelemetryData> allData) {
		List<TelemetryData> out = new ArrayList<>();

		for (TelemetryData t : allData) {
			if (UserFilter.isTeamMember(t.getUserName())
					|| UserFilter.isTeamMember(t.getAdditionalEmails())) {
				LOG.info("Skipping team member created entry: taskId="
						+ t.getTaskId());
				continue;
			} else {
				out.add(t);
			}
		}

		return out;
	}

	public static String formatUrl(String url) {
		if (url != null) {
			return "<a href=\"" + url + "\">link</a>";
		}
		return NOT_APPLICABLE;
	}

	public static String formatMillis(long number) {
		return formatNumber(TimeUnit.MILLISECONDS.toSeconds(number));
	}

	public static long millisToSec(long number) {
		return TimeUnit.MILLISECONDS.toSeconds(number);
	}

	private static String formatNumber(long number) {
		return "<span align=\"right\">" + Long.toString(number) + "</span>";
	}

	public static String formatBoolean(Boolean b) {
		if (b == null) {
			return NOT_APPLICABLE;
		}
		if (b) {
			return "<font color=\"green\"><b>YES</b></font>";
		} else {
			return "<font color=\"red\"><b>NO</b></font>";
		}
	}
	
	public static String ifPullRequestMerged(String pullURL) {
		
		if(pullURL == null || pullURL.equalsIgnoreCase("n/a")) {
			return formatBoolean(null);
		}
		
		String[] strings = new String[11];		
		strings = pullURL.split("/");		
		String url = "https://github.corp.ebay.com/api/v3/repos/" + strings[3] + "/" + strings[4] + "/pulls/" + strings[6] + "/merge";
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(request);
			if(response.getStatusLine().getStatusCode() == 204) {
				return formatBoolean(true);
			}
			else if(response.getStatusLine().getStatusCode() == 404) {
				return formatBoolean(false);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}				
		return formatBoolean(false);
	}
	
	public static List<TelemetryData> getUniqueUpgradeJobs(List<TelemetryData> data) {
		List<TelemetryData> finalData = new ArrayList<>();
		for(TelemetryData d : data) {
			if (d.isSuccess() == null) {
				Boolean gitPushSuccess = d.isGitPushSuccesful();
				if (gitPushSuccess != null) {
					if (gitPushSuccess == true) {
						boolean flag = true;
						for(TelemetryData f : finalData) {
							if(f.getSourceGitURL().equals(d.getSourceGitURL()) && f.getSourceGitRelativePath().equals(d.getSourceGitRelativePath())) {
								flag = false;
								break;
							}
						}
						if(flag) {
							finalData.add(d);
						}
					}
				}
			} else if (d.isSuccess() == true) {
				boolean flag = true;
				for(TelemetryData f : finalData) {
					if(f.getSourceGitURL().equals(d.getSourceGitURL()) && f.getSourceGitRelativePath().equals(d.getSourceGitRelativePath())) {
						flag = false;
						break;
					}
				}
				if(flag) {
					finalData.add(d);
				}
			}
		}
		return finalData;
	}
	
	public static String getStringFromList(List<String> list) {
		if(list == null || list.size() == 0) {
			return NOT_APPLICABLE;
		}
		
		return StringUtils.join(list, ", ");
	}
	
	public static Map<String, MissingArtifactData> getMissingArtifactData() {
		TelemetryDAO dao = TelemetryDAO.getInstance();
		Map<String, MissingArtifactData> missingArtifactList = new HashMap<>();
		List<ArtifactsData> artifacts = dao.getAllMissingArtifacts();
		
		for(ArtifactsData artifact : artifacts) {
			if(missingArtifactList.containsKey(artifact.getArtifactId() + artifact.getGroupId())) {
				MissingArtifactData m = missingArtifactList.get(artifact.getArtifactId() + artifact.getGroupId());
				m.getTelemetryList().add(telemetryMap.get(artifact.getTaskId()));
				
				// Take whichever artifact entry has a value
				if(StringUtils.isEmpty(m.getArtifact().getOwner())) {
					m.getArtifact().setOwner(artifact.getOwner());
				}
				
				if(StringUtils.isEmpty(m.getArtifact().getGitCommitters())) {
					m.getArtifact().setGitCommitters(artifact.getGitCommitters());
				}
			}
			else {
				MissingArtifactData m = new MissingArtifactData();
				m.setArtifact(artifact);
				m.setTelemetryList(new ArrayList<TelemetryData>(Arrays.asList(telemetryMap.get(artifact.getTaskId()))));
				missingArtifactList.put(artifact.getArtifactId() + artifact.getGroupId(), m);
			}			
		}		
		return missingArtifactList;
	}
	
	public static String getGitList(List<TelemetryData> list) {
		if(list == null || list.size() == 0) {
			return NOT_APPLICABLE;
		}
		StringBuilder sb = new StringBuilder();
		for(TelemetryData s : list) {
			sb.append(s.getSourceGitURL()).append(", ");
		}
		return sb.toString();
	}
	
	public static String getMailtoLinkForMissingArtifact(MissingArtifactData c) {
		try {
	    String mailto = "<a href=\"mailto:?subject=";

	    if(isUnknownOwner(c.getArtifact().getOwner()) && isUnknownOwner(c.getArtifact().getGitCommitters())) {
	    	LOG.info("No owner identified for " + c);
	    	return NOT_APPLICABLE;
	    }
	    
	    String subject = "Important information regarding the " + c.getArtifact().getGroupId() + ":" + c.getArtifact().getArtifactId() + " Maven artifact";
	    mailto +=  encode(subject);
	    
	    Set<String> dependencyOwners = new HashSet<>();
	    
	    if(!isUnknownOwner(c.getArtifact().getOwner())) {
	    	dependencyOwners.add(c.getArtifact().getOwner());
	    }
	    
	    if(!isUnknownOwner(c.getArtifact().getGitCommitters())) {
	    	StringTokenizer tok = new StringTokenizer(c.getArtifact().getGitCommitters(), ",");
	    	
	    	while(tok.hasMoreTokens()) {
	    		String crtOwner = tok.nextToken().trim();
	    		if(!isUnknownOwner(crtOwner)) {
	    			dependencyOwners.add(crtOwner);
	    		}
	    	}
	    }
	    
	    mailto += "&to=" + StringUtils.join(dependencyOwners, ';');
	    
	    Set<String> appNames = new HashSet<>();
	    Set<String> appOwners = new HashSet<>();
	    
	    for(TelemetryData d:c.getTelemetryList()) {
	    	if(d==null) {
	    		continue;
	    	}
	    	
	    	if(!StringUtils.isEmpty(d.getUserName())) {
	    		appOwners.add(d.getUserName() + "@ebay.com");
	    	}
	    	
	    	if(d.getAppName() != null) {
	    		appNames.addAll(d.getAppName());
	    	}
	    }
	    
	    if(appNames.isEmpty()) {
	    	return NOT_APPLICABLE;
	    }
	    
	    mailto += "&cc=" + StringUtils.join(appOwners, ';');
	    
	    String body = "Hello " + c.getArtifact().getArtifactId() + " team,\n"
	    						+ "\n"
	    						+ "According to our analysis you are the owner of the " + c.getArtifact().getGroupId() + ":" + c.getArtifact().getArtifactId() + " artifact from the old Maven repository.\n"
  								+ "\n"
  								+ "We have also identified the following apps that are using the artifact your team owns: " + StringUtils.join(appNames, ", ") + ".\n"
									+ "\n"
									+ "In order for the depending apps to be able to seamlessly upgrade to the new platform version you will need to make the artifact available in the new Maven repository\n"
									+ "\n"
									+ "For your convenience, the identified owners of the apps were added in the CC list of this email.\n"
									+ "\n"
									+ "Thank you,\n"
									+ "The Platform Team";
    
	    mailto += "&body=" + encode(body);
	    
	    mailto += "\">edit&amp;send</a>";
	    
	    return mailto;
    } catch (UnsupportedEncodingException e) {
	    LOG.warn(e.getMessage(), e);
	    return NOT_APPLICABLE;
    }
	}

	private static String encode(String subject) throws UnsupportedEncodingException {
		return URLEncoder.encode(subject, CharEncoding.UTF_8).replace("+", "%20");
	}
	
	public static boolean isUnknownOwner(String owner) {
	  return StringUtils.isEmpty(owner) || owner.trim().equalsIgnoreCase("jenkins@ebay.com");
  }

	public static String getMoreDetailsForMissingArtifacts(List<TelemetryData> list) {		
		StringBuilder sb = new StringBuilder();
		sb.append("<table cellpadding=\"5\" cellspacing=\"0\" border=\"0\" style=\"padding-left:50px;\">");
		sb.append("<tr><th>User</th><th>Git</th><th>TaskId</th><th>AppName</th></tr>");
		for (TelemetryData telemetryData : list) {
			if(telemetryData == null) {
				continue;
			}
			
			sb.append("<tr><td>");
			sb.append(telemetryData.getUserName());
			sb.append("</td><td>");
			sb.append(telemetryData.getSourceGitURL());
			sb.append("</td><td>");
			sb.append(telemetryData.getTaskId());
			sb.append("</td><td>");
			sb.append(telemetryData.getAppName());
			sb.append("</td></tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	public static String getMoreDetailsForTask(String taskId) {
		TelemetryDAO dao = TelemetryDAO.getInstance();
		List<ArtifactsData> list = dao.getMissingArtifactsByTaskId("13936239411041010828127");
		StringBuilder sb = new StringBuilder();
		sb.append("<table cellpadding=\"5\" cellspacing=\"0\" border=\"0\" style=\"padding-left:50px;\">");
		sb.append("<tr><th>ArtifactId</th><th>GrouId</th><th>Git URL</th><th>Git Committers</th></tr>");
		for (ArtifactsData artifactData : list) {
			sb.append("<tr><td>");
			sb.append(artifactData.getArtifactId());
			sb.append("</td><td>");
			sb.append(artifactData.getGroupId());
			sb.append("</td><td>");
			sb.append(artifactData.getGitUrl());
			sb.append("</td><td>");
			sb.append(artifactData.getGitCommitters());
			sb.append("</td></tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

}
