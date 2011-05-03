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
		else if (id == AppPortletIds.AGREEMENT_DISPLAY)
			portlet = new AgreementPortlet();
		else if (id == AppPortletIds.RECENT_AGREEMENTS_DISPLAY)
			portlet = new RecentAgreementsPortlet();
		else if (id == AppPortletIds.RECENT_INSTITUTIONS_DISPLAY)
			portlet = new RecentInstitutionsPortlet();
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
