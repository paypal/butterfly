package org.ostara.service.git.auth;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.ostara.config.Config;

public class Auth {
	private String _userName = null;
	private String _password = null;
	private String _gitOtp;

	public Auth(String userName, String password, String gitOtp) {
		_userName = userName;
		_password = password;
		_gitOtp = gitOtp;
	}
	
	public Object authorize() {
		Map<String, String> userObj = ReadJsonFromUrlBasicAuth.read(Config.getInstance().getGitAPIUserUrl(), getBasicAuthToken(), _gitOtp);
		if(userObj == null){
			Map<String, String> unauthorized = new HashMap<String, String>();
			unauthorized.put("error", "401");
			
			return unauthorized; 
		} else
		if(userObj.containsKey(ReadJsonFromUrlBasicAuth.X_GIT_HUB_OTP_HEADER)) {
			Map<String, String> unauthorized = new HashMap<String, String>();
			unauthorized.put("error", "401");
			unauthorized.put(ReadJsonFromUrlBasicAuth.X_GIT_HUB_OTP_HEADER, String.valueOf(Boolean.TRUE));
			
			return unauthorized; 
		} else{
			AuthToken authToken = AuthToken.getinstance();
			String userAuthToken = authToken.generate(userObj);
			UserInfo userInfo = new UserInfo(userAuthToken,  userObj.get("login"), userObj.get("email"));
			userInfo.set_fullname(userObj.get("name"));
			userInfo.set_gravatar(userObj.get("avatar_url"));
			return userInfo;
			
		}
	}

	private String getBasicAuthToken() {
		String encoding = (String.format("%s:%s", _userName, _password));

		byte[] authEncBytes = Base64.encodeBase64(encoding.getBytes());
		return new String(authEncBytes);
	}
}
