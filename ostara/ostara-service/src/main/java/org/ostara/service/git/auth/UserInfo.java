package org.ostara.service.git.auth;

public class UserInfo {
	private String _authToken = null;
	private String _userName = null;
	private String _email = null;
	private String _gravatar = null;
	private String _fullName = null;
	
	public UserInfo(String authToken, String userName, String email) {
		this._authToken = authToken;
		this._userName = userName;
		this._email = email;
	}
	
	public String get_authToken() {
		return _authToken;
	}
	public void set_authToken(String _authToken) {
		this._authToken = _authToken;
	}
	public String get_userName() {
		return _userName;
	}
	public void set_userName(String _userName) {
		this._userName = _userName;
	}
	public String get_email() {
		return _email;
	}
	public void set_email(String _email) {
		this._email = _email;
	}
	public String get_gravatar() {
		return _gravatar;
	}
	public void set_gravatar(String _gravatar) {
		this._gravatar = _gravatar;
	}
	public String get_fullName() {
		return _fullName;
	}
	public void set_fullname(String _fullName) {
		this._fullName = _fullName;
	}
		
		
}
