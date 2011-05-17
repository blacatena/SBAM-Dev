package com.scholastic.sbam.client.uiobjects.uiapp;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.scholastic.sbam.client.util.IconSupplier;

public class AppNavTree {
	
	public static class SelectionIconProvider implements ModelIconProvider<ModelData> {

		@Override
		public AbstractImagePrototype getIcon(ModelData model) {
			if (model.get("iconName") != null)
				return IconSupplier.getAppNavIcon(model.get("iconName").toString());
			return null;
		}
		
	}
	
	public static class SelectionTreeNode extends BaseTreeModel {
		private static final long serialVersionUID = 1L;

	 	public SelectionTreeNode() {
	 	}

	 	public SelectionTreeNode(AppPortletIds portletId) {
	 		this(portletId.name, portletId.iconName, portletId);
	 	}

	 	public SelectionTreeNode(String name, AppPortletIds portletId) {
	 		this(name, portletId.iconName, portletId);
	 	}

	 	public SelectionTreeNode(String name, String iconName) {
	 		this(name, iconName, AppPortletIds.UNKNOWN_PORTLET);
	 	}

	 	public SelectionTreeNode(String name, String iconName, AppPortletIds portlet) {
	 		set("name", name);
	 		set("iconName", iconName);
	 		set("portlet", portlet);
	 	}

	 	public String getName() {
	 		return (String) get("name");
	 	}

	 	public String getIconName() {
	 		return (String) get("iconName");
	 	}
	 	
	 	public AppPortletIds getPortletId() {
	 		return (AppPortletIds) get("portlet");
	 	}
	 	
	 	public String toString() {
	 		return getName();
	 	}
	}

  
	public static class Folder extends BaseTreeModel implements IsSerializable {  
		private static final long serialVersionUID = 1L;  
		private static int ID = 0;  
		    
		public Folder() {  
			set("id", ID++);  
		}  
		  
		public Folder(String name) {  
			set("id", ID++);  
			set("name", name);  
		}
		
		public Folder(String name, String iconName) {
			this(name);
	 		set("iconName", iconName);
		}
		  
		public Folder(String name, BaseTreeModel[] children) {  
			this(name);  
			for (int i = 0; i < children.length; i++) {  
				add(children[i]);  
			}  
		}  
		  
		public Integer getId() {  
			return (Integer) get("id");  
		}  
		  
		public String getName() {  
			return (String) get("name");  
		}
		
		public void add(BaseTreeModel [] children) { 
			for (int i = 0; i < children.length; i++) {  
				add(children[i]);  
			}  
		}
		  
		public String toString() {  
			return getName();  
		}  
	}
	
	
	public static Folder getTreeModel() {
		
	  Folder creates = new Folder("Create", IconSupplier.getNewIconName());
	  creates.add(new SelectionTreeNode("New Agreement", 		IconSupplier.getAgreementIconName(), AppPortletIds.AGREEMENT_DISPLAY));
	  creates.add(new SelectionTreeNode("New Site", 			AppPortletIds.SITE_LOCATION_DISPLAY));
	  creates.add(new SelectionTreeNode("New Proxy", 			IconSupplier.getProxyIconName()));
	  creates.add(new SelectionTreeNode("New Link", 			IconSupplier.getAgreementLinkIconName()));
		
	  Folder searches = new Folder("Searches", IconSupplier.getSearchIconName());
	  
	  Folder custSrch = new Folder("Customers", IconSupplier.getInstitutionIconName());
	  custSrch.add(new SelectionTreeNode("Full Institution",	AppPortletIds.FULL_INSTITUTION_SEARCH));
	  custSrch.add(new SelectionTreeNode("Customer",			AppPortletIds.CUSTOMER_SEARCH));
	  custSrch.add(new SelectionTreeNode("Site List", 			AppPortletIds.SITE_INSTITUTION_SEARCH));
	  custSrch.add(new SelectionTreeNode("Contacts", 			IconSupplier.getContactsIconName()));
	  
	  Folder agreeSrch = new Folder("Agreements", IconSupplier.getAgreementIconName());
	  agreeSrch.add(new SelectionTreeNode("Agreement", 			AppPortletIds.AGREEMENT_SEARCH));
	  agreeSrch.add(new SelectionTreeNode("Product Terms", 		AppPortletIds.AGREEMENT_TERM_SEARCH));
	  agreeSrch.add(new SelectionTreeNode("Sites", 				IconSupplier.getSiteIconName()));
	  agreeSrch.add(new SelectionTreeNode("Methods", 			IconSupplier.getAccessMethodIconName()));
	  agreeSrch.add(new SelectionTreeNode("Notes", 				IconSupplier.getNoteIconName()));
	  agreeSrch.add(new SelectionTreeNode("Contacts", 			IconSupplier.getContactsIconName()));
	  agreeSrch.add(new SelectionTreeNode("Proxies", 			IconSupplier.getProxyIconName()));
	  agreeSrch.add(new SelectionTreeNode("Links", 				IconSupplier.getAgreementLinkIconName()));
	  
	  Folder recentSrch = new Folder("Recent", IconSupplier.getRecentIconName());
	  recentSrch.add(new SelectionTreeNode("Agreements",		AppPortletIds.RECENT_AGREEMENTS_DISPLAY));
	  recentSrch.add(new SelectionTreeNode("Institutions",		AppPortletIds.RECENT_INSTITUTIONS_DISPLAY));
	  recentSrch.add(new SelectionTreeNode("Customers",			AppPortletIds.RECENT_CUSTOMERS_DISPLAY));
	  recentSrch.add(new SelectionTreeNode("Sites",				AppPortletIds.RECENT_SITES_DISPLAY));
	  recentSrch.add(new SelectionTreeNode("Proxies",			IconSupplier.getProxyIconName()));
	  
	  searches.add(custSrch);
	  searches.add(agreeSrch);
	  searches.add(recentSrch);
	  
//	  Folder agreements = new Folder("Agreements");
//	  agreements.add(new SelectionTreeNode("Create New", IconSupplier.getNewIconName()));
//	  agreements.add(new SelectionTreeNode("Agreement", IconSupplier.getAgreementIconName()));
//	  agreements.add(new SelectionTreeNode("Product Terms", IconSupplier.getProductIconName()));
//	  agreements.add(new SelectionTreeNode("Sites", IconSupplier.getSiteIconName()));
//	  agreements.add(new SelectionTreeNode("Contacts", IconSupplier.getContactsIconName()));
//	  agreements.add(new SelectionTreeNode("Methods", IconSupplier.getAccessMethodIconName()));

	  Folder root = new Folder("root");
	  root.add(creates);
	  root.add(searches);
	  root.add(new SelectionTreeNode("Portlet Manager", IconSupplier.getPortletsIconName()));
	  root.add(new SelectionTreeNode("Help", IconSupplier.getHelpIconName()));
//	  root.add(agreements);

	  return root;
	}
	
	public static TreePanel<ModelData> getTreePanel() {
		Folder model = getTreeModel();  
		
		TreeStore<ModelData> store = new TreeStore<ModelData>();  
		store.add(model.getChildren(), true); 
		
		final TreePanel<ModelData> tree = new TreePanel<ModelData>(store);
		tree.setDisplayProperty("name");  
		tree.setWidth(250);
		tree.setIconProvider(new SelectionIconProvider());
		tree.setAutoExpand(true);
		
		return tree;
	}
}
