package com.scholastic.sbam.client.uiobjects.foundation;

import java.util.List;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public interface ParentCardPanel {
	public void switchLayout(int id);
	
	public void switchLayout(int id, LayoutContainer returnContainer);
	
	public void switchLayout(LayoutContainer container);
	
	public void switchLayout(LayoutContainer container, LayoutContainer returnContainer);
	
	public List<LayoutContainer> getCards();
	
	public LayoutContainer getCard(int id);
}
