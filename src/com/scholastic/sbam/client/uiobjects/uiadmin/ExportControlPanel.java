package com.scholastic.sbam.client.uiobjects.uiadmin;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.GetExportReportService;
import com.scholastic.sbam.client.services.GetExportReportServiceAsync;
import com.scholastic.sbam.client.services.InitiateExportService;
import com.scholastic.sbam.client.services.InitiateExportServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class ExportControlPanel extends LayoutContainer implements AppSleeper {
	private final GetExportReportServiceAsync	getExportReportService = GWT.create(GetExportReportService.class);
	private final InitiateExportServiceAsync	initiateExportService = GWT.create(InitiateExportService.class);
	
	protected Html					refreshLabel = new Html("Refresh Delay (in seconds):");
	protected NumberField			refreshDelay;
	protected Button				exportButton;
	protected Html					html;
	protected MultiField<String>	multiField;
	
	protected ExportProcessReport	exportProcessReport;
	
	@Override
	public void onRender(Element element, int index) {
		super.onRender(element, index);
		
		TableLayout layout = new TableLayout(2);
		layout.setCellPadding(10);
		layout.setWidth("500px");
		setLayout(layout);
		
		exportButton = new Button("Trigger Export") {
			@Override
			public void onClick(ComponentEvent ce) {
				initiateExport();
			}
		};
		IconSupplier.forceIcon(exportButton, IconSupplier.getExportIconName());
		
		refreshDelay = new NumberField();
		refreshDelay.setMinValue(0);
		refreshDelay.setMaxValue(500);
		refreshDelay.setValue(30);
		refreshDelay.addListener(Events.OnChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				System.out.println("refreshDelay " + be.getType().getClass().getName());
			}
			
		});
		
		multiField = new MultiField<String>();
		multiField.add(new LabelField("Refresh Delay (in seconds):"));
		multiField.add(refreshDelay);
		
		TableData tableData = new TableData();
		tableData.setColspan(2);
		tableData.setWidth("100%");
		html = new Html();
		
		add(exportButton);
		add(refreshDelay);
		add(html, tableData);
	}
	
	protected void initiateExport() {
		exportButton.disable();
		System.out.println("Request export...");
		initiateExportService.initiateExport(new AsyncCallback<ExportProcessReport>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Export initiation failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(ExportProcessReport exportProcessReport) {
						System.out.println("Export request response...");
						if (exportProcessReport == null) {
							System.out.println("No process report returned!!!");
						} else {
							System.out.println(exportProcessReport.getLastMessage());
						}
					}
			});
	}
	
	protected void refreshExportProcessReport() {
		getExportReportService.getExportReport(new AsyncCallback<ExportProcessReport>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Export report refresh failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(ExportProcessReport exportProcessReport) {
						if (exportProcessReport == null) {
							System.out.println("There is no export process report available.");
							html.setHtml("There is no export process report available.");
							exportButton.enable();
						} else {
							System.out.println(exportProcessReport.getLastMessage());
							html.setHtml(exportProcessReport.getLastMessage().getMessage());
							exportButton.setEnabled(!exportProcessReport.isRunning());
						}
					}
			});
	}

	@Override
	public void awaken() {
		
	}

	@Override
	public void sleep() {
		
	}
}
