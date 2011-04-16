package com.scholastic.sbam.client.uiobjects.uiadmin;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.DocumentationListService;
import com.scholastic.sbam.client.services.DocumentationListServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.shared.objects.DocumentationInstance;

public class DocumentationLinksDisplay extends Composite implements AppSleeper {
	
	private LayoutContainer layoutContainer;
	private Html loading;
	private LayoutContainer docList;

	public DocumentationLinksDisplay() {
		layoutContainer = new LayoutContainer();
		
		loading = new Html("<i>Loading documentation links...</i>");
		loading.addStyleName("loading-indicator");
		
		docList = new LayoutContainer(new TableLayout(6));
		docList.setVisible(false);
		
		layoutContainer.add(loading);
		layoutContainer.add(docList);
		
		initComponent(layoutContainer);
	}
	
	public void startReload() {
		
		loadDocumentationList();
	}
	
	/**
	 * Load any documentation links.
	 */
	public void loadDocumentationList() {
		if (loading != null)
			loading.setVisible(true);
		if (docList != null)
			docList.setVisible(false);

		final DocumentationListServiceAsync documentationListService = GWT.create(DocumentationListService.class);

		documentationListService.getDocumentationLinks(null,
			new AsyncCallback<List<DocumentationInstance>>() {
				public void onFailure(Throwable caught) {
					if (docList == null)
						MessageBox.alert("Documentation List Load Failure", "<b>Documentation list load failed:</b> " + caught.getMessage(), null);
					else {
						// Show the RPC error message to the user
						docList.setLayout(new FlowLayout());
						docList.add(new Html("<b>Documentation list load failed:</b> " + caught.getMessage()));
						docList.setVisible(true);
						loading.setVisible(false);
					}
				}

				public void onSuccess(List<DocumentationInstance> list) {
					if (docList == null)
						return;
					
					docList.removeAll();
					docList.setLayout(new TableLayout(6));
					
					String contents = "";
					Html   html;
					for (DocumentationInstance link : list) {
						contents = "<div  class=\"pdocImage\">";
						if (link.getIconImage() != null && link.getIconImage().length() > 0) {
							contents += "<img src=\"";
							contents += link.getIconImage();
							contents += "\" border=0/ style=\"padding-right: 10px\">";
						}
						contents += "</div>";
						html = new Html(contents);
						html.setWidth(50);
						docList.add(html);
						
						//	Add link to/title of document
						contents = "<div  class=\"pdocTitle\">";
						if (link.getLink() != null && link.parseTypes().length > 0) {
							contents += "<a href=\"";
							contents += getLink(link) + link.parseTypes() [0];
							contents += "\" target=\"_blank\" class=\"pdocLink\" >";
							contents += link.getTitle();
							contents += "</a>";
						} else {
							contents += link.getTitle();
						}
						contents += "</div>";
						html = new Html(contents);
						html.setWidth(150);
						docList.add(html);
						
						for (String docType : new String [] {"pdf", "docx", "html"}) {
							contents = "<div class=\"pdocType\">";
							if (link.getTypes().contains(docType)) {
								contents += "<a href=\"";
								contents += getLink(link);
								contents += ".";
								contents += docType;
								contents += "\" target=\"_blank\" class=\"pdocImgLink\" >";
								contents += "<img src=\"resources/images/icons/docs/doc_";
								contents += docType;
								contents += ".png\" />";
								contents += "</a>";
								contents += "<a href=\"";
								contents += getLink(link);
								contents += ".";
								contents += docType;
								contents += "\" target=\"_blank\" class=\"pdocLink\" >";
								contents += docType;
								contents += "</a>";
							}
							contents += "</div>";
							html = new Html(contents);
							html.setWidth(100);
							docList.add(html);
						}
						
						// Add description of document
						contents = "<div  class=\"pdocDescr\">";
						if (link.getDescription() != null) {
							contents += link.getDescription();
						}
						contents += "</div>";
						html = new Html(contents);
					//	html.setWidth(50);
						docList.add(html);
					}
					
					if (docList.getItemCount() == 0) {
						docList.setLayout(new FlowLayout());
						docList.add(new Html("<i>No documentation found.</i>"));
					}
					
					loading.setVisible(false);
					docList.setVisible(true);
					docList.layout(true);
				}
			});
		
	}
	
	public String getLink(DocumentationInstance instance) {
		if (instance.getLink() == null)
			return "#";
		return instance.getLink();
	}

	@Override
	public void awaken() {
		startReload();
	}

	@Override
	public void sleep() {
	}
	

}
