package com.scholastic.sbam.client.uiobjects.uireports;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.InstitutionCountryListService;
import com.scholastic.sbam.client.services.InstitutionCountryListServiceAsync;
import com.scholastic.sbam.client.services.InstitutionStateListService;
import com.scholastic.sbam.client.services.InstitutionStateListServiceAsync;
import com.scholastic.sbam.client.stores.BetterFilterListStore;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.InstitutionCountryInstance;
import com.scholastic.sbam.shared.objects.InstitutionStateInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;

public class CustomerSelectionCard extends SnapshotCardBase {
	
	protected ContentPanel	contentPanel				=	 getNewContentPanel();
	
	private ContentPanel statesPanel;
	private ContentPanel countriesPanel;
	
	private static BetterFilterListStore<BeanModel>		institutionCountries= new BetterFilterListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		institutionStates	= new BetterFilterListStore<BeanModel>();
	
	public CustomerSelectionCard() {
		super();
		this.headingToolTip = "Use this panel to select customers for the snapshot.";
	}

	@Override
	public void addPanelContent() {
		contentPanel.add(getPanelsContainer());
		
		add(contentPanel);
	}
	
	public ContentPanel getNewContentPanel() {
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setHeading(getPanelTitle());
		IconSupplier.setIcon(contentPanel, IconSupplier.getCustomerIconName());
		return contentPanel;
	}
	
	public LayoutContainer getPanelsContainer() {

		
		LayoutContainer layoutContainer = new LayoutContainer();
		layoutContainer.setLayout(new AccordionLayout());
		
		layoutContainer.add(getStatesPanel());
		layoutContainer.add(getCountriesPanel());
		
		return layoutContainer;
	}
	
	public ContentPanel getStatesPanel() {
		/*
		 * State selection
		 */
		
		statesPanel = new ContentPanel(new FitLayout()) 
//			{
//				@Override
//				public void onExpand() {
//					super.onExpand();
//				//	welcomeMessageEditGrid.resizePanelHeight();
//					layout(true);
//				}
//				
//				@Override
//				public void afterRender() {
//					super.afterRender();
//					layout(true);
//				}
//			}
		;
		statesPanel.setBodyStyleName("dual-grid-bg");
		statesPanel.setHeading("By U.S. State");
		statesPanel.setCollapsible(true);
		IconSupplier.setIcon(statesPanel, IconSupplier.getUsaIconName());
		
//		statesPanel.addListener(Events.Collapse, new Listener<ComponentEvent>() {  
//			public void handleEvent(ComponentEvent be) {
//				if (welcomeMessageEditGrid != null) welcomeMessageEditGrid.sleep();
//			}  
//		});  
//		statesPanel.addListener(Events.Expand, new Listener<ComponentEvent>() {  
//			public void handleEvent(ComponentEvent be) {
//				if (welcomeMessageEditGrid != null) welcomeMessageEditGrid.awaken();
//			}  
//		});
//		
//		welcomeMessageEditGrid = new WelcomeMessageEditGrid();
		statesPanel.add(new Html("By U.S. State"));
		
		return statesPanel;
	}
	
	public ContentPanel getCountriesPanel() {
		/*
		 * Country selection
		 */
		
		countriesPanel = new ContentPanel(new FitLayout()) 
//			{
//				@Override
//				public void onExpand() {
//					super.onExpand();
//					userEditGrid.resizePanelHeight();
//					layout(true);
//				}
//				
//				@Override
//				public void afterRender() {
//					super.afterRender();
//					layout(true);
//				}
//			}
		;
		
		countriesPanel.setBodyStyleName("dual-grid-bg");
		countriesPanel.setHeading("By Country");
		countriesPanel.setCollapsible(true);
		IconSupplier.setIcon(countriesPanel, IconSupplier.getCountriesIconName());
		
//		countriesPanel.addListener(Events.Collapse, new Listener<ComponentEvent>() {  
//			public void handleEvent(ComponentEvent be) {
//				if (userEditGrid != null) userEditGrid.sleep();
//			}  
//		});  
//		countriesPanel.addListener(Events.Expand, new Listener<ComponentEvent>() {  
//			public void handleEvent(ComponentEvent be) {
//				if (userEditGrid != null) userEditGrid.awaken();
//			}  
//		});
//		
//		userEditGrid = new UserEditGrid();
//		userEditGrid.setVerticalMargins(100);
		countriesPanel.add(new Html("By Country"));
		
		return countriesPanel;
	}

	@Override
	public ContentPanel getContentPanel() {
		return contentPanel;
	}

	@Override
	public String getPanelTitle() {
		return "Snapshot Customer Selector";
	}
	
	public static void loadInstitutionCountries() {
		InstitutionCountryListServiceAsync institutionCountryListService = GWT.create(InstitutionCountryListService.class);
		
		AsyncCallback<List<InstitutionCountryInstance>> callback = new AsyncCallback<List<InstitutionCountryInstance>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				if (caught instanceof IllegalArgumentException)
					MessageBox.alert("Alert", caught.getMessage(), null);
				else {
					MessageBox.alert("Alert", "Link types load failed unexpectedly.", null);
					System.out.println(caught.getClass().getName());
					System.out.println(caught.getMessage());
				}
			}

			public void onSuccess(List<InstitutionCountryInstance> list) {
				institutionCountries.removeAll();
				if (institutionCountries.getKeyProvider() == null)
					institutionCountries.setKeyProvider(new SimpleKeyProvider("institutionCountryCode"));
				for (InstitutionCountryInstance instance : list) {
					institutionCountries.add(InstitutionCountryInstance.obtainModel(instance));	
				}
			}
		};
		
		institutionCountryListService.getInstitutionCountries(null, callback);
	}
	
	public static void loadInstitutionStates() {
		InstitutionStateListServiceAsync institutionStateListService = GWT.create(InstitutionStateListService.class);
		
		AsyncCallback<List<InstitutionStateInstance>> callback = new AsyncCallback<List<InstitutionStateInstance>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				if (caught instanceof IllegalArgumentException)
					MessageBox.alert("Alert", caught.getMessage(), null);
				else {
					MessageBox.alert("Alert", "Link types load failed unexpectedly.", null);
					System.out.println(caught.getClass().getName());
					System.out.println(caught.getMessage());
				}
			}

			public void onSuccess(List<InstitutionStateInstance> list) {
				institutionStates.removeAll();
				if (institutionStates.getKeyProvider() == null)
					institutionStates.setKeyProvider(new SimpleKeyProvider("institutionStateCode"));
				for (InstitutionStateInstance instance : list) {
					institutionStates.add(InstitutionStateInstance.obtainModel(instance));	
				}
			}
		};
		
		institutionStateListService.getInstitutionStates(null, callback);
	}
	
	
}
