package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class AppContainer extends LayoutContainer {
	private LayoutContainer contentContainer;
	private LayoutContainer headerContainer;
	private LayoutContainer footerContainer;
	private LayoutContainer fitContainer;

	/**
	 * DO NOT USE
	 */
	public AppContainer() {
		setLayout(new FlowLayout(5));
		
		headerContainer = new LayoutContainer();
		headerContainer.setLayout(new FlowLayout(5));
		add(headerContainer);
		
		fitContainer = new LayoutContainer();
		fitContainer.setHeight(900);
		fitContainer.setLayout(new FitLayout());
		
		contentContainer = new LayoutContainer();
		fitContainer.add(contentContainer);
		contentContainer.setLayout(new CenterLayout());
		contentContainer.setBorders(true);
		add(fitContainer);
		fitContainer.setBorders(true);
		
		footerContainer = new LayoutContainer();
		footerContainer.setLayout(new FlowLayout(5));
		add(footerContainer);
	}

	public LayoutContainer getContentContainer() {
		return contentContainer;
	}

	public void setContentContainer(LayoutContainer contentContainer) {
		this.contentContainer = contentContainer;
	}

	public LayoutContainer getHeaderContainer() {
		return headerContainer;
	}

	public void setHeaderContainer(LayoutContainer headerContainer) {
		this.headerContainer = headerContainer;
	}

	public LayoutContainer getFooterContainer() {
		return footerContainer;
	}

	public void setFooterContainer(LayoutContainer footerContainer) {
		this.footerContainer = footerContainer;
	}

}
