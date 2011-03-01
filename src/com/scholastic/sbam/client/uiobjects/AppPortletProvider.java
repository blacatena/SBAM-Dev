package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.scholastic.sbam.client.uiobjects.InstitutionSearchPortlet;
import com.scholastic.sbam.client.services.AppPortlet;

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
	
	private Portal portal;
	
	public AppPortletProvider(Portal portal) {
		this.portal = portal;
	}
	
	public boolean addPortlet(ModelData model) {
		if (portal != null && model.get("portlet") != null) {
			addPortlet(getPortlet((AppPortletIds) model.get("portlet")));
			return true;
		}
		return false;
	}
	
	public void addPortlet(Portlet portlet) { 
		addPortlet(portlet, 0);
	}
	
	public void addPortlet(Portlet portlet, int column) { 
		portal.add(portlet, column);
	}
	
	private void configPanel(final ContentPanel panel, final AppPortletIds id) {  
		panel.setCollapsible(true);
		panel.setAnimCollapse(false);
	//	panel.getHeader().addTool(new ToolButton("x-tool-gear"));
	}
	
	public Portlet getPortlet(AppPortletIds id) {
		Portlet portlet;
		if (id == AppPortletIds.FULL_INSTITUTION_SEARCH)
			portlet = new InstitutionSearchPortlet();
		else {
			portlet = new AppPortlet(id.helpTextId);
			portlet.setHeading("Unknown Portlet Request");
			portlet.addText("This portlet was created in response to a request for unmapped ID '" + id.name + "'.");
		}

		configPanel(portlet, id);
		if (portlet instanceof AppPortletRequester)
			((AppPortletRequester) portlet).setAppPortletProvider(this);
		
		return portlet;
	}

	public Portal getPortal() {
		return portal;
	}

	public void setPortal(Portal portal) {
		this.portal = portal;
	}
	
}
