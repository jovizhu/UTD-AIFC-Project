package com.jovi.ldap;

public class LDAPUserInfo {

	private String userName;
	private String userDomain;
	private String ldapSignature;
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserDomain() {
		return userDomain;
	}
	public void setUserDomain(String userDomain) {
		this.userDomain = userDomain;
	}

	public String getLdapSignature() {
		return ldapSignature;
	}
	public void setLdapSignature(String ldapSignature) {
		this.ldapSignature = ldapSignature;
	}
	
	public String extractRole()
	{
		String userRole  = ldapSignature.split(":")[3];
		return userRole;
	}

}
