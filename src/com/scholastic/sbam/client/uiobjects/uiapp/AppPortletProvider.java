package com.scholastic.sbam.client.uiobjects.uiapp;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.scholastic.sbam.client.uiobjects.foundation.UnknownPortlet;

/**
 * This class creates specific standard application portlets and adds them to a portal.
 * 
 * It is isolated from other classes (such as the portal itself) so that many different classes can create
 * portlets (such as other portlets).
 * 
 * @author Bob Lacatena
 *
 */
public class AppPortletProvider {
	
	private AppPortletPresenter presenter;
	
	public AppPortletProvider(AppPortletPresenter presenter) {
		this.presenter = presenter;
	}
	
	public boolean addPortlet(ModelData model) {
		if (presenter != null && model.get("portlet") != null) {
			addPortlet(getPortlet((AppPortletIds) model.get("portlet")));
			return true;
		}
		return false;
	}
	
	public void addPortlet(AppPortlet portlet) { 
		addPortlet(portlet, 0);
	}
	
	public void addPortlet(AppPortlet portlet, int column) { 
		presenter.add(portlet, column);
	}
	
	public boolean insertPortlet(ModelData model, int index, int column) { 
		if (presenter != null && model.get("portlet") != null) {
			insertPortlet(getPortlet((AppPortletIds) model.get("portlet")), index, column);
			return true;
		}
		return false;
	}
	
	public void insertPortlet(AppPortlet portlet, int index, int column) { 
		presenter.insert(portlet, index, column);
	}
	
	private void configPanel(final ContentPanel panel, final AppPortletIds id) {  
		panel.setCollapsible(true);
		panel.setAnimCollapse(false);
	//	panel.getHeader().addTool(new ToolButton("x-tool-gear"));
	}
	
	public AppPortletIds getAppPortletId(String className) {
		for (AppPortletIds id : AppPortletIds.values())
			if (id.getClassName().equals(className))
				return id;
		return AppPortletIds.UNKNOWN_PORTLET;
	}
	
	public AppPortlet getPortlet(String className) {
		return getPortlet(getAppPortletId(className));
	}
	
	public AppPortlet getPortlet(AppPortletIds id) {
		AppPortlet portlet;
		if (id == AppPortletIds.FULL_INSTITUTION_SEARCH)
			portlet = new InstitutionSearchPortlet();
		else if (id == AppPortletIds.CUSTOMER_SEARCH)
			portlet = new CustomerSearchPortlet();
		else if (id == AppPortletIds.SITE_INSTITUTION_SEARCH)
			portlet = new SiteInstitutionSearchPortlet();
		else if (id == AppPortletIds.AGREEMENT_SEARCH)
			portlet = new AgreementSearchPortlet();
		else if (id == AppPortletIds.AGREEMENT_AUTH_METHOD_SEARCH)
			portlet = new AgreementAuthMethodSearchPortlet();
		else if (id == AppPortletIds.AGREEMENT_AUTH_METHOD_CONFLICT)
			portlet = new AgreementConflictResolverPortlet();
		else if (id == AppPortletIds.AGREEMENT_CONTACT_SEARCH)
			portlet = new AgreementContactSearchPortlet();
		else if (id == AppPortletIds.AGREEMENT_NOTES_SEARCH)
			portlet = new AgreementNotesSearchPortlet();
		else if (id == AppPortletIds.AGREEMENT_REMOTE_SETUP_URL_SEARCH)
			portlet = new AgreementRemoteSetupUrlSearchPortlet();
		else if (id == AppPortletIds.AGREEMENT_SITE_SEARCH)
			portlet = new AgreementSiteSearchPortlet();
		else if (id == AppPortletIds.AGREEMENT_TERM_SEARCH)
			portlet = new AgreementTermSearchPortlet();
		else if (id == AppPortletIds.SITE_LOCATION_DISPLAY)
			portlet = new SiteLocationPortlet();
		else if (id == AppPortletIds.AGREEMENT_DISPLAY)
			portlet = new AgreementPortlet();
		else if (id == AppPortletIds.AGREEMENT_LINK_DISPLAY)
			portlet = new AgreementLinkPortlet();
		else if (id == AppPortletIds.AGREEMENT_LINK_SEARCH)
			portlet = new AgreementLinkSearchPortlet();
		else if (id == AppPortletIds.PROXY_DISPLAY)
			portlet = new ProxyPortlet();
		else if (id == AppPortletIds.PROXY_SEARCH)
			portlet = new ProxySearchPortlet();
		else if (id == AppPortletIds.RECENT_AGREEMENTS_DISPLAY)
			portlet = new RecentAgreementsPortlet();
		else if (id == AppPortletIds.RECENT_PROXIES_DISPLAY)
			portlet = new RecentProxiesPortlet();
		else if (id == AppPortletIds.RECENT_INSTITUTIONS_DISPLAY)
			portlet = new RecentInstitutionsPortlet();
		else if (id == AppPortletIds.RECENT_CUSTOMERS_DISPLAY)
			portlet = new RecentCustomersPortlet();
		else if (id == AppPortletIds.RECENT_SITES_DISPLAY)
			portlet = new RecentSiteLocationsPortlet();
		else {
			portlet = new UnknownPortlet(id.helpTextId);
			portlet.setHeading("Unknown Portlet Request");
			portlet.addText("This portlet was created in response to a request for unmapped ID '" + id.name + "'.");
		}

		configPanel(portlet, id);
		if (portlet instanceof AppPortletRequester)
			((AppPortletRequester) portlet).setAppPortletProvider(this);
		
		return portlet;
	}

	public AppPortletPresenter getPresenter() {
		return presenter;
	}

	public void setPresenter(AppPortletPresenter portal) {
		this.presenter = portal;
	}
	
}
