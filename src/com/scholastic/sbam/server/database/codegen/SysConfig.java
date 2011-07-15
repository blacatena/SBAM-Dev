package com.scholastic.sbam.server.database.codegen;

// Generated Jul 14, 2011 10:05:06 PM by Hibernate Tools 3.2.4.GA

/**
 * SysConfig generated by hbm2java
 */
public class SysConfig implements java.io.Serializable {

	private String id;
	private int seq;
	private String emailServer;
	private int emailPort;
	private String emailUser;
	private String emailPassword;
	private String emailAddress;
	private String emailCc;
	private String emailBcc;
	private String instanceName;
	private String siteUrl;
	private String techContactName;
	private String techContactEmail;
	private String executionMode;
	private char instConfigInner;
	private char instConfigPairs;
	private int instConfigMinStr;
	private int instConfigMinInner;
	private int instConfigMaxPair;
	private int instConfigMaxList;
	private int instConfigMaxWords;
	private int instConfigLoadLimit;
	private int instConfigLoadWatch;
	private int instConfigLoadGc;
	private String instConfigLoadStatus;
	private char aeUcnMode;

	public SysConfig() {
	}

	public SysConfig(String id, int seq, String emailServer, int emailPort,
			String emailUser, String emailPassword, String emailAddress,
			String emailCc, String emailBcc, String instanceName,
			String siteUrl, String techContactName, String techContactEmail,
			String executionMode, char instConfigInner, char instConfigPairs,
			int instConfigMinStr, int instConfigMinInner,
			int instConfigMaxPair, int instConfigMaxList,
			int instConfigMaxWords, int instConfigLoadLimit,
			int instConfigLoadWatch, int instConfigLoadGc,
			String instConfigLoadStatus, char aeUcnMode) {
		this.id = id;
		this.seq = seq;
		this.emailServer = emailServer;
		this.emailPort = emailPort;
		this.emailUser = emailUser;
		this.emailPassword = emailPassword;
		this.emailAddress = emailAddress;
		this.emailCc = emailCc;
		this.emailBcc = emailBcc;
		this.instanceName = instanceName;
		this.siteUrl = siteUrl;
		this.techContactName = techContactName;
		this.techContactEmail = techContactEmail;
		this.executionMode = executionMode;
		this.instConfigInner = instConfigInner;
		this.instConfigPairs = instConfigPairs;
		this.instConfigMinStr = instConfigMinStr;
		this.instConfigMinInner = instConfigMinInner;
		this.instConfigMaxPair = instConfigMaxPair;
		this.instConfigMaxList = instConfigMaxList;
		this.instConfigMaxWords = instConfigMaxWords;
		this.instConfigLoadLimit = instConfigLoadLimit;
		this.instConfigLoadWatch = instConfigLoadWatch;
		this.instConfigLoadGc = instConfigLoadGc;
		this.instConfigLoadStatus = instConfigLoadStatus;
		this.aeUcnMode = aeUcnMode;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getSeq() {
		return this.seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getEmailServer() {
		return this.emailServer;
	}

	public void setEmailServer(String emailServer) {
		this.emailServer = emailServer;
	}

	public int getEmailPort() {
		return this.emailPort;
	}

	public void setEmailPort(int emailPort) {
		this.emailPort = emailPort;
	}

	public String getEmailUser() {
		return this.emailUser;
	}

	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}

	public String getEmailPassword() {
		return this.emailPassword;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

	public String getEmailAddress() {
		return this.emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailCc() {
		return this.emailCc;
	}

	public void setEmailCc(String emailCc) {
		this.emailCc = emailCc;
	}

	public String getEmailBcc() {
		return this.emailBcc;
	}

	public void setEmailBcc(String emailBcc) {
		this.emailBcc = emailBcc;
	}

	public String getInstanceName() {
		return this.instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getSiteUrl() {
		return this.siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public String getTechContactName() {
		return this.techContactName;
	}

	public void setTechContactName(String techContactName) {
		this.techContactName = techContactName;
	}

	public String getTechContactEmail() {
		return this.techContactEmail;
	}

	public void setTechContactEmail(String techContactEmail) {
		this.techContactEmail = techContactEmail;
	}

	public String getExecutionMode() {
		return this.executionMode;
	}

	public void setExecutionMode(String executionMode) {
		this.executionMode = executionMode;
	}

	public char getInstConfigInner() {
		return this.instConfigInner;
	}

	public void setInstConfigInner(char instConfigInner) {
		this.instConfigInner = instConfigInner;
	}

	public char getInstConfigPairs() {
		return this.instConfigPairs;
	}

	public void setInstConfigPairs(char instConfigPairs) {
		this.instConfigPairs = instConfigPairs;
	}

	public int getInstConfigMinStr() {
		return this.instConfigMinStr;
	}

	public void setInstConfigMinStr(int instConfigMinStr) {
		this.instConfigMinStr = instConfigMinStr;
	}

	public int getInstConfigMinInner() {
		return this.instConfigMinInner;
	}

	public void setInstConfigMinInner(int instConfigMinInner) {
		this.instConfigMinInner = instConfigMinInner;
	}

	public int getInstConfigMaxPair() {
		return this.instConfigMaxPair;
	}

	public void setInstConfigMaxPair(int instConfigMaxPair) {
		this.instConfigMaxPair = instConfigMaxPair;
	}

	public int getInstConfigMaxList() {
		return this.instConfigMaxList;
	}

	public void setInstConfigMaxList(int instConfigMaxList) {
		this.instConfigMaxList = instConfigMaxList;
	}

	public int getInstConfigMaxWords() {
		return this.instConfigMaxWords;
	}

	public void setInstConfigMaxWords(int instConfigMaxWords) {
		this.instConfigMaxWords = instConfigMaxWords;
	}

	public int getInstConfigLoadLimit() {
		return this.instConfigLoadLimit;
	}

	public void setInstConfigLoadLimit(int instConfigLoadLimit) {
		this.instConfigLoadLimit = instConfigLoadLimit;
	}

	public int getInstConfigLoadWatch() {
		return this.instConfigLoadWatch;
	}

	public void setInstConfigLoadWatch(int instConfigLoadWatch) {
		this.instConfigLoadWatch = instConfigLoadWatch;
	}

	public int getInstConfigLoadGc() {
		return this.instConfigLoadGc;
	}

	public void setInstConfigLoadGc(int instConfigLoadGc) {
		this.instConfigLoadGc = instConfigLoadGc;
	}

	public String getInstConfigLoadStatus() {
		return this.instConfigLoadStatus;
	}

	public void setInstConfigLoadStatus(String instConfigLoadStatus) {
		this.instConfigLoadStatus = instConfigLoadStatus;
	}

	public char getAeUcnMode() {
		return this.aeUcnMode;
	}

	public void setAeUcnMode(char aeUcnMode) {
		this.aeUcnMode = aeUcnMode;
	}

}
