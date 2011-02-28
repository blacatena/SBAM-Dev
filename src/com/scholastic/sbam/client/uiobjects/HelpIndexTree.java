package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.scholastic.sbam.client.services.HelpTextIndexService;
import com.scholastic.sbam.client.services.HelpTextIndexServiceAsync;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.HelpTextIndexInstance;

public class HelpIndexTree {
	
	
	public static class HelpIndexTreeSelectionModel extends TreePanelSelectionModel<ModelData> {
		HelpIndexTreeActor actor;
		
		HelpIndexTreeSelectionModel(HelpIndexTreeActor actor) {
			super();
			this.actor = actor;
		}
		
		@Override
		public void onSelectChange(ModelData model, boolean select) {
			super.onSelectChange(model, select);
			if (select) {
				actor.jumpTo(model.get("id").toString());
			}
			this.deselectAll();
		}
	}
	
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
		
		public SelectionTreeNode(HelpTextIndexInstance instance) {
			this(instance.getId(), instance.getTitle(), instance.getIconName());
			add(instance.getChildren());
		}
	 	public SelectionTreeNode(String id, String title, String iconName) {
	 		set("id", id);
	 		set("title", title);
	 		set("iconName", iconName);
	 	}

	 	public String getid() {
	 		return (String) get("id");
	 	}

	 	public String getTitle() {
	 		return (String) get("title");
	 	}

	 	public String getIconName() {
	 		return (String) get("iconName");
	 	}
	 	
	 	public String toString() {
	 		return getTitle();
	 	}
		
		public void add(List<HelpTextIndexInstance> children) { 
			for (HelpTextIndexInstance child : children) {  
				add(new SelectionTreeNode(child));  
			}  
		}
	}
	
	public static TreePanel<ModelData> getTreePanel(HelpIndexTreeActor actor) {
		
		final HelpTextIndexServiceAsync helpTextIndexService = GWT.create(HelpTextIndexService.class);
		
		final TreeStore<ModelData> store = new TreeStore<ModelData>();  
	//	store.add(model.getChildren(), true); 
		
		final TreePanel<ModelData> tree = new TreePanel<ModelData>(store);
		tree.setDisplayProperty("title");  
		tree.setWidth(400);
		tree.setIconProvider(new SelectionIconProvider());
		tree.setAutoExpand(true);
		tree.setSelectionModel(new HelpIndexTreeSelectionModel(actor));
		

		helpTextIndexService.getHelpTextIndex(
				new AsyncCallback<List<HelpTextIndexInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Help text index service failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(List<HelpTextIndexInstance> result) {
						for (HelpTextIndexInstance instance : result) {
							store.add(new SelectionTreeNode(instance), true);
						}
					}
				});
		
		return tree;
	}
}
