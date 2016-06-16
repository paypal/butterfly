package org.ostara.service.git.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class ReadJsonFromUrlBasicAuth {
  public static final String X_GIT_HUB_OTP_HEADER = "X-GitHub-OTP";
private static final Logger logger = LoggerFactory.getLogger(ReadJsonFromUrlBasicAuth.class);
  
  public static Map<String, String> read(String restUrl, String basicAuthToken, String _gitOtp) {
    HttpURLConnection conn = null;
    
    try {

      URL url = new URL(restUrl);
      conn = (HttpURLConnection) url.openConnection();
      
      if(!StringUtils.isEmpty(_gitOtp)) {
    	  conn.addRequestProperty(X_GIT_HUB_OTP_HEADER, _gitOtp);
      }
      
      int numCharsRead;
      char[] charArray = new char[1024];
      StringBuffer sb = new StringBuffer();
      conn.setRequestMethod("GET");
      conn.setDoOutput(true);
      conn.setRequestProperty("Authorization", "Basic " + basicAuthToken);
      InputStream content = (InputStream) conn.getInputStream();
      InputStreamReader reader = new InputStreamReader(content);
      while ((numCharsRead = reader.read(charArray)) > 0) {
        sb.append(charArray, 0, numCharsRead);
      }

      Gson gson = new Gson();
      Map<String, String> jsonObject = null;

      String jsonString = sb.toString();

      if (jsonString == null || jsonString.isEmpty()) {
        return null;
      }
      try {
        jsonObject = (Map<String, String>) gson.fromJson(jsonString, Object.class);
      } catch (Exception ex) {
        logger.error(ex.getMessage(), ex);
        throw ex;
      } finally {
        conn.disconnect();

      }
      return jsonObject;
    } catch (IOException e) {
    	if(conn != null) {
    		String otp = conn.getHeaderField(X_GIT_HUB_OTP_HEADER);
			if(otp != null && otp.contains("required")) {
				logger.warn("One time password login required by GitHub: " + otp);
    			Map<String, String> tmpMap = new HashMap<>();
    			tmpMap.put(X_GIT_HUB_OTP_HEADER, String.valueOf(Boolean.TRUE));
    			return tmpMap;
    		}
    	} else {
    		logger.warn(e.getMessage(), e);
    	}
    }
    return null;
  }

}
