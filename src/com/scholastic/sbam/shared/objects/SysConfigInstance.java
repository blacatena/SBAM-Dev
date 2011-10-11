package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SysConfigInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable, UserCacheTarget {

	public static final int SYS_CONFIG_KEY_SET	= 0;
	
	private static BeanModelFactory beanModelfactory;

	protected	String	id;
	protected	int		seq;
	protected	String	emailServer;
	protected	int		emailPort;
	protected	String	emailUser;
	protected	String	emailPassword;
	protected	String	emailAddress;
	protected	String	emailCc;
	protected	String	emailBcc;
	protected	String	instanceName;
	protected	String	siteUrl;
	protected	String	techContactName;
	protected	String	techContactEmail;
	protected	String	executionMode;
	protected	char	instConfigInner;
	protected	char	instConfigPairs;
	protected	int		instConfigMinStr;
	protected	int		instConfigMinInner;
	protected	int		instConfigMaxPair;
	protected	int		instConfigMaxList;
	protected	int		instConfigMaxWords;
	protected	int		instConfigLoadLimit;
	protected	int		instConfigLoadWatch;
	protected	int		instConfigLoadGc;
	protected	String	instConfigLoadStatus;
	protected	char	ae_ucn_mode;
	
	@Override
	public void markForDeletion() {
		setSeq(-1);
	}

	@Override
	public boolean thisIsDeleted() {
		return seq == -1;
	}

	@Override
	public boolean thisIsValid() {
		return true;
	}

	@Override
	public String returnTriggerProperty() {
		return "junk";
	}

	@Override
	public String returnTriggerValue() {
		return "junk";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getEmailServer() {
		return emailServer;
	}

	public void setEmailServer(String emailServer) {
		this.emailServer = emailServer;
	}

	public int getEmailPort() {
		return emailPort;
	}

	public void setEmailPort(int emailPort) {
		this.emailPort = emailPort;
	}

	public String getEmailUser() {
		return emailUser;
	}

	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}

	public String getEmailPassword() {
		return emailPassword;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailCc() {
		return emailCc;
	}

	public void setEmailCc(String emailCc) {
		this.emailCc = emailCc;
	}

	public String getEmailBcc() {
		return emailBcc;
	}

	public void setEmailBcc(String emailBcc) {
		this.emailBcc = emailBcc;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public String getTechContactName() {
		return techContactName;
	}

	public void setTechContactName(String techContactName) {
		this.techContactName = techContactName;
	}

	public String getTechContactEmail() {
		return techContactEmail;
	}

	public void setTechContactEmail(String techContactEmail) {
		this.techContactEmail = techContactEmail;
	}

	public String getExecutionMode() {
		return executionMode;
	}

	public void setExecutionMode(String executionMode) {
		this.executionMode = executionMode;
	}

	public char getInstConfigInner() {
		return instConfigInner;
	}

	public void setInstConfigInner(char instConfigInner) {
		this.instConfigInner = instConfigInner;
	}

	public char getInstConfigPairs() {
		return instConfigPairs;
	}

	public void setInstConfigPairs(char instConfigPairs) {
		this.instConfigPairs = instConfigPairs;
	}

	public int getInstConfigMinStr() {
		return instConfigMinStr;
	}

	public void setInstConfigMinStr(int instConfigMinStr) {
		this.instConfigMinStr = instConfigMinStr;
	}

	public int getInstConfigMinInner() {
		return instConfigMinInner;
	}

	public void setInstConfigMinInner(int instConfigMinInner) {
		this.instConfigMinInner = instConfigMinInner;
	}

	public int getInstConfigMaxPair() {
		return instConfigMaxPair;
	}

	public void setInstConfigMaxPair(int instConfigMaxPair) {
		this.instConfigMaxPair = instConfigMaxPair;
	}

	public int getInstConfigMaxList() {
		return instConfigMaxList;
	}

	public void setInstConfigMaxList(int instConfigMaxList) {
		this.instConfigMaxList = instConfigMaxList;
	}

	public int getInstConfigMaxWords() {
		return instConfigMaxWords;
	}

	public void setInstConfigMaxWords(int instConfigMaxWords) {
		this.instConfigMaxWords = instConfigMaxWords;
	}

	public int getInstConfigLoadLimit() {
		return instConfigLoadLimit;
	}

	public void setInstConfigLoadLimit(int instConfigLoadLimit) {
		this.instConfigLoadLimit = instConfigLoadLimit;
	}

	public int getInstConfigLoadWatch() {
		return instConfigLoadWatch;
	}

	public void setInstConfigLoadWatch(int instConfigLoadWatch) {
		this.instConfigLoadWatch = instConfigLoadWatch;
	}

	public int getInstConfigLoadGc() {
		return instConfigLoadGc;
	}

	public void setInstConfigLoadGc(int instConfigLoadGc) {
		this.instConfigLoadGc = instConfigLoadGc;
	}

	public String getInstConfigLoadStatus() {
		return instConfigLoadStatus;
	}

	public void setInstConfigLoadStatus(String instConfigLoadStatus) {
		this.instConfigLoadStatus = instConfigLoadStatus;
	}

	public char getAe_ucn_mode() {
		return ae_ucn_mode;
	}

	public void setAe_ucn_mode(char ae_ucn_mode) {
		this.ae_ucn_mode = ae_ucn_mode;
	}

	public static BeanModel obtainModel(SysConfigInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(SysConfigInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}
	
	public static String getUserCacheCategory() {
		return "SysConfig";
	}

	@Override
	public String userCacheCategory(int keySet) {
		return getUserCacheCategory();
	}

	@Override
	public String userCacheStringKey(int keySet) {
		return id;
	}

	@Override
	public int userCacheIntegerKey(int keySet) {
		return 0;
	}
	
	@Override
	public int userCacheKeyCount() {
		return 1;
	}
}
