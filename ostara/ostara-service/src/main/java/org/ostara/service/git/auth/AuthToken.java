package org.ostara.service.git.auth;

import java.util.HashMap;
import java.util.Map;

public class AuthToken {
	private static AuthToken _instance = null;
	private Map<String, Object> _userAuthTokens = new HashMap<String, Object>();

	private AuthToken() {

	}

	public static AuthToken getinstance() {
		if (_instance == null) {
			_instance = new AuthToken();
		}
		return _instance;

	}

	public String generate(Object userObj) {
		String token = Long.toString(System.currentTimeMillis());
		
		_userAuthTokens.put(token, userObj);
		return token;

	}

	public Object getTokenValue(String userToken) {
		return _userAuthTokens.get(userToken);

	}
	
	public Boolean isValidToken(String userToken) {
		return _userAuthTokens.containsKey(userToken);
	}

}
