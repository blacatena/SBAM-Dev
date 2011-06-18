package com.scholastic.sbam.shared.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ExportProcessReport implements BeanModelTag, IsSerializable {
	
	List<ExportProcessMessage> messages = new ArrayList<ExportProcessMessage>();
	
	protected		boolean	validExport		=	false;
	protected		boolean running			=	false;
	protected		String	status			=	 "Unstarted";
	
	protected		Date	timeStarted;
	protected		Date	timeCompleted;
	
	protected		int		errors;
	protected		int		agreements;
	protected		int		sites;
	protected		int		sitePrefs;
	protected		int		authUnits;
	protected		int		aeAuCopied;
	protected		int		ips;
	protected		int		uids;
	protected		int		puids;
	protected		int		urls;
	protected		int		rsUrls;
	protected		int		ipEntries;
	protected		int		puidEntries;
	
	protected		int		ipConflicts;
	protected		int		uidConflicts;
	protected		int		puidConflicts;
	protected		int		urlConflicts;
	
	protected		int		ipDuplicates;
	protected		int		uidDuplicates;
	protected		int		puidDuplicates;
	protected		int		urlDuplicates;
	protected		int		rsUrlDuplicates;
	
	protected		int		badCustomerCodes;
	protected		int		badCustomerCodeAus;
	
	protected		int		services;
	protected		int		preferences;
	protected		int		customers;
	
	protected 		char	ucnMode;
	
	/**
	 * Count of AUs that have already been used on previous agreements for this set of products
	 */
	protected		int		auCountExistingAgreement;
	/**
	 * Count of AUs that existed on the previous export, and were reused for the same products on this 
	 */
	protected		int		auCountReuseSameProduct;
	/**
	 * Count of AUs that have some but not all similar products from the last run.
	 */
	protected		int		auCountReuseSimilarProducts;
	/**
	 * Count of AUs that already exist but were not used the last run.
	 */
	protected		int		auCountReusePrevUnusedAuCount;
	/**
	 * Count of AUs that already exist but were not used the last run.
	 */
	protected		int		auCountReusePrevRandom;
	/**
	 * Count of AUs that were newly creatd for this export.
	 */
	protected		int		auCountCreatedThisExport;
	/**
	 * Agreement with no default site to use.
	 */
	protected		int		noDefaultSiteAgreementCount;
	/**
	 * Agreement with a single default site to use.
	 */
	protected		int		singleSiteAgreementCount;
	
	
	public ExportProcessReport() {
	}
	
	public void addError(String error) {
		addMessage(error, ExportProcessMessage.HIGH_ALERT);
		errors++;
	}
	
	public void addAlert(String alert) {
		addMessage(alert, ExportProcessMessage.ALERT);
	}
	
	public void countAgreement() {
		agreements++;
	}
	
	public void countSite() {
		sites++;
	}
	
	public void countSitePref() {
		sitePrefs++;
	}
	
	public void countAuthUnit() {
		authUnits++;
	}
	
	public void countAeAuCopied() {
		aeAuCopied++;
	}
	
	public void countIp() {
		ips++;
	}
	
	public void countUid() {
		uids++;
	}
	
	public void countPuid() {
		puids++;
	}
	
	public void countUrl() {
		urls++;
	}
	
	public void countRsUrl() {
		rsUrls++;
	}
	
	public void countIpEntry() {
		ipEntries++;
	}
	
	public void countPuidEntry() {
		puidEntries++;
	}
	
	public void countIpConflict() {
		ipConflicts++;
	}
	
	public void countUidConflict() {
		uidConflicts++;
	}
	
	public void countPuidConflict() {
		puidConflicts++;
	}
	
	public void countUrlConflict() {
		urlConflicts++;
	}
	
	public void countIpDuplicate() {
		ipDuplicates++;
	}
	
	public void countUidDuplicate() {
		uidDuplicates++;
	}
	
	public void countPuidDuplicate() {
		puidDuplicates++;
	}
	
	public void countUrlDuplicate() {
		urlDuplicates++;
	}
	
	public void countRsUrlDuplicate() {
		rsUrlDuplicates++;
	}
	
	public void countBadCustomerCodes() {
		badCustomerCodes++;
	}
	
	public void countBadCustomerCodeAus() {
		badCustomerCodeAus++;
	}
	
	public void countServices() {
		services++;
	}
	
	public void countPreferences() {
		preferences++;
	}
	
	public void countCustomers() {
		customers++;
	}

	public List<ExportProcessMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ExportProcessMessage> messages) {
		this.messages = messages;
	}
	
	public void addMessage(String message) {
		messages.add(new ExportProcessMessage(message));
	}
	
	public void addMessage(String message, int priority) {
		messages.add(new ExportProcessMessage(message, priority));
	}
	
	public void setStarted() {
		if (running) {
			addMessage("Start status requested when already running.");
			return;
		}
		running = true;
		setTimeStarted(new Date());
		setStatus("initiated");
		addMessage("Export process initiated.");
	}
	
	public void setCompleted() {
		setCompleted(null);
	}
	
	public void setCompleted(String disposition) {
		if (!running) {
			addMessage("Completion update requested when not running.");
			return;
		}
		running = false;
		setTimeCompleted(new Date());
		if (disposition != null && disposition.length() > 0) {
			setStatus(disposition);
			addMessage("Process completed " + disposition + ".");
		} else {
			validExport = true;
			setStatus("succesfully");
			addAlert("Process completed successfully.");
		}
	}
	
	public ExportProcessMessage getLastMessage() {
		if (messages.size() == 0)
			return null;
		return messages.get(messages.size() - 1);
	}
	
	public List<ExportProcessMessage> getLastMessages() {
		return getLastMessages(10);
	}
	
	public List<ExportProcessMessage> getLastMessages(int numMessages) {
		if (messages.size() == 0)
			return null;
		int from = messages.size() < numMessages ? 0 : messages.size() - numMessages;
		return messages.subList(from, messages.size());
	}

	public Date getTimeStarted() {
		return timeStarted;
	}

	public void setTimeStarted(Date timeStarted) {
		this.timeStarted = timeStarted;
	}

	public Date getTimeCompleted() {
		return timeCompleted;
	}

	public void setTimeCompleted(Date timeCompleted) {
		this.timeCompleted = timeCompleted;
	}

	public int getErrors() {
		return errors;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public int getAuthUnits() {
		return authUnits;
	}

	public int getAgreements() {
		return agreements;
	}

	public void setAgreements(int agreements) {
		this.agreements = agreements;
	}

	public void setAuthUnits(int authUnits) {
		this.authUnits = authUnits;
	}

	public int getAeAuCopied() {
		return aeAuCopied;
	}

	public void setAeAuCopied(int aeAuCopied) {
		this.aeAuCopied = aeAuCopied;
	}

	public int getSites() {
		return sites;
	}

	public void setSites(int sites) {
		this.sites = sites;
	}

	public int getSitePrefs() {
		return sitePrefs;
	}

	public void setSitePrefs(int sitePrefs) {
		this.sitePrefs = sitePrefs;
	}

	public int getIps() {
		return ips;
	}

	public void setIps(int ips) {
		this.ips = ips;
	}

	public int getUids() {
		return uids;
	}

	public void setUids(int uids) {
		this.uids = uids;
	}

	public int getPuids() {
		return puids;
	}

	public void setPuids(int puids) {
		this.puids = puids;
	}

	public int getUrls() {
		return urls;
	}

	public void setUrls(int urls) {
		this.urls = urls;
	}

	public int getRsUrls() {
		return rsUrls;
	}

	public void setRsUrls(int rsUrls) {
		this.rsUrls = rsUrls;
	}

	public int getIpEntries() {
		return ipEntries;
	}

	public void setIpEntries(int ipEntries) {
		this.ipEntries = ipEntries;
	}

	public int getPuidEntries() {
		return puidEntries;
	}

	public void setPuidEntries(int puidEntries) {
		this.puidEntries = puidEntries;
	}

	public int getIpConflicts() {
		return ipConflicts;
	}

	public void setIpConflicts(int ipConflicts) {
		this.ipConflicts = ipConflicts;
	}

	public int getUidConflicts() {
		return uidConflicts;
	}

	public void setUidConflicts(int uidConflicts) {
		this.uidConflicts = uidConflicts;
	}

	public int getPuidConflicts() {
		return puidConflicts;
	}

	public void setPuidConflicts(int puidConflicts) {
		this.puidConflicts = puidConflicts;
	}

	public int getUrlConflicts() {
		return urlConflicts;
	}

	public void setUrlConflicts(int urlConflicts) {
		this.urlConflicts = urlConflicts;
	}

	public int getIpDuplicates() {
		return ipDuplicates;
	}

	public void setIpDuplicates(int ipDuplicates) {
		this.ipDuplicates = ipDuplicates;
	}

	public int getUidDuplicates() {
		return uidDuplicates;
	}

	public void setUidDuplicates(int uidDuplicates) {
		this.uidDuplicates = uidDuplicates;
	}

	public int getPuidDuplicates() {
		return puidDuplicates;
	}

	public void setPuidDuplicates(int puidDuplicates) {
		this.puidDuplicates = puidDuplicates;
	}

	public int getUrlDuplicates() {
		return urlDuplicates;
	}

	public void setUrlDuplicates(int urlDuplicates) {
		this.urlDuplicates = urlDuplicates;
	}

	public int getRsUrlDuplicates() {
		return rsUrlDuplicates;
	}

	public void setRsUrlDuplicates(int rsUrlDuplicates) {
		this.rsUrlDuplicates = rsUrlDuplicates;
	}

	public int getBadCustomerCodes() {
		return badCustomerCodes;
	}

	public void setBadCustomerCodes(int badCustomerCodes) {
		this.badCustomerCodes = badCustomerCodes;
	}

	public int getBadCustomerCodeAus() {
		return badCustomerCodeAus;
	}

	public void setBadCustomerCodeAus(int badCustomerCodeAus) {
		this.badCustomerCodeAus = badCustomerCodeAus;
	}

	public int getServices() {
		return services;
	}

	public void setServices(int services) {
		this.services = services;
	}

	public int getPreferences() {
		return preferences;
	}

	public void setPreferences(int preferences) {
		this.preferences = preferences;
	}

	public int getCustomers() {
		return customers;
	}

	public void setCustomers(int customers) {
		this.customers = customers;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public char getUcnMode() {
		return ucnMode;
	}

	public void setUcnMode(char ucnMode) {
		this.ucnMode = ucnMode;
	}

	public int getAuCountExistingAgreement() {
		return auCountExistingAgreement;
	}

	public void setAuCountExistingAgreement(int auCountExistingAgreement) {
		this.auCountExistingAgreement = auCountExistingAgreement;
	}

	public void auCountExistingAgreement() {
		auCountExistingAgreement++;
	}

	public int getAuCountReuseSameProduct() {
		return auCountReuseSameProduct;
	}

	public void setAuCountReuseSameProduct(int auCountReuseSameProduct) {
		this.auCountReuseSameProduct = auCountReuseSameProduct;
	}

	public void auCountReuseSameProduct() {
		auCountReuseSameProduct++;
	}

	public int getAuCountReuseSimilarProducts() {
		return auCountReuseSimilarProducts;
	}

	public void setAuCountReuseSimilarProducts(int auCountReuseSimilarProducts) {
		this.auCountReuseSimilarProducts = auCountReuseSimilarProducts;
	}

	public void auCountReuseSimilarProducts() {
		auCountReuseSimilarProducts++;
	}

	public int getAuCountReusePrevUnusedAuCount() {
		return auCountReusePrevUnusedAuCount;
	}

	public void setAuCountReusePrevUnusedAuCount(int auCountReusePrevUnusedAuCount) {
		this.auCountReusePrevUnusedAuCount = auCountReusePrevUnusedAuCount;
	}

	public void auCountReusePrevUnusedAuCount() {
		auCountReusePrevUnusedAuCount++;
	}

	public int getAuCountReusePrevRandom() {
		return auCountReusePrevRandom;
	}

	public void setAuCountReusePrevRandom(int auCountReusePrevRandom) {
		this.auCountReusePrevRandom = auCountReusePrevRandom;
	}

	public void auCountReusePrevRandom() {
		auCountReusePrevRandom++;
	}

	public int getAuCountCreatedThisExport() {
		return auCountCreatedThisExport;
	}

	public void setAuCountCreatedThisExport(int auCountCreatedThisExport) {
		this.auCountCreatedThisExport = auCountCreatedThisExport;
	}

	public void auCountCreatedThisExport() {
		auCountCreatedThisExport++;
	}

	public int getNoDefaultSiteAgreementCount() {
		return noDefaultSiteAgreementCount;
	}

	public void setNoDefaultSiteAgreementCount(int noSiteAgreementCount) {
		this.noDefaultSiteAgreementCount = noSiteAgreementCount;
	}

	public void noDefaultSiteAgreementCount() {
		noDefaultSiteAgreementCount++;
	}

	public int getSingleSiteAgreementCount() {
		return singleSiteAgreementCount;
	}

	public void setSingleSiteAgreementCount(int singleSiteAgreementCount) {
		this.singleSiteAgreementCount = singleSiteAgreementCount;
	}

	public void singleSiteAgreementCount() {
		singleSiteAgreementCount++;
	}

	public boolean isValidExport() {
		return validExport;
	}

	public boolean isRunning() {
		return running;
	}

	public long getElapsedSeconds() {
		if (timeCompleted == null || timeStarted == null)
			return -1L;
		return (timeCompleted.getTime() - timeStarted.getTime()) / 1000L;
	}

	public long getElapsedMinutes() {
		if (timeCompleted == null || timeStarted == null)
			return -1L;
		return (timeCompleted.getTime() - timeStarted.getTime()) / 60000L;
	}
	
}
