package com.scholastic.sbam.client.uiobjects.uiadmin;

import java.util.List;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.GetExportReportService;
import com.scholastic.sbam.client.services.GetExportReportServiceAsync;
import com.scholastic.sbam.client.services.InitiateExportService;
import com.scholastic.sbam.client.services.InitiateExportServiceAsync;
import com.scholastic.sbam.client.services.TerminateExportService;
import com.scholastic.sbam.client.services.TerminateExportServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.exceptions.AuthenticationExportException;
import com.scholastic.sbam.shared.objects.ExportProcessMessage;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class ExportControlPanel extends LayoutContainer implements AppSleeper {
	public static final int DEFAULT_TIMER_DELAY = 1;
	public static final int MESSAGES_TO_DISPLAY = 10;
	
	private final GetExportReportServiceAsync	getExportReportService = GWT.create(GetExportReportService.class);
	private final InitiateExportServiceAsync	initiateExportService = GWT.create(InitiateExportService.class);
	private final TerminateExportServiceAsync	terminateExportService = GWT.create(TerminateExportService.class);
	
	protected NumberField			refreshDelay;
	protected NumberField			messageCount;
	protected CheckBox				consoleOutput;
	protected Button				exportButton;
	protected Html					countsHtml;
	protected Html					messagesHtml;
	protected Button				terminateButton;
	protected TextField<String>		terminateReason;
	protected MultiField<String>	displayFields;
	protected MultiField<String>	terminateFields;
	
	protected ExportProcessReport	exportProcessReport;
	
	protected Timer					exportProcessReportTimer;
	
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
		
		LabelField refreshLabel = new LabelField("Refresh Delay (seconds):");
		refreshLabel.setWidth(200);
		refreshLabel.setStyleAttribute("padding-left", "10px");
		refreshLabel.setStyleAttribute("padding-right", "10px");
		
		refreshDelay = new NumberField();
		refreshDelay.setMinValue(0);
		refreshDelay.setMaxValue(500);
		refreshDelay.setValue(DEFAULT_TIMER_DELAY);
		refreshDelay.addListener(Events.OnChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				setExportProcessReportTimer();
			}
			
		});
		

		LabelField countLabel = new LabelField("Messages to display:");
		countLabel.setWidth(150);
		countLabel.setStyleAttribute("padding-left", "10px");
		countLabel.setStyleAttribute("padding-right", "10px");
		
		messageCount = new NumberField();
		messageCount.setMinValue(1);
		messageCount.setMaxValue(5000);
		messageCount.setValue(MESSAGES_TO_DISPLAY);
		messageCount.addListener(Events.OnChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				refreshMessages();
			}
			
		});
		
		consoleOutput = new CheckBox();
		consoleOutput.setBoxLabel("Console output on");
		consoleOutput.setStyleAttribute("padding-left", "20px");
		consoleOutput.setStyleAttribute("padding-right", "20px");
		
		displayFields = new MultiField<String>();
		displayFields.add(consoleOutput);
		displayFields.add(refreshLabel);
		displayFields.add(refreshDelay);
		displayFields.add(countLabel);
		displayFields.add(messageCount);
		
		terminateButton = new Button("Terminate Export") {
			@Override
			public void onClick(ComponentEvent ce) {
				terminateExport();
			}
		};
		terminateButton.disable();
		IconSupplier.forceIcon(terminateButton, IconSupplier.getCancelIconName());


		LabelField terminateLabel = new LabelField("Terminate Reason:");
		terminateLabel.setWidth(150);
		terminateLabel.setStyleAttribute("padding-left", "10px");
		terminateLabel.setStyleAttribute("padding-right", "10px");
		
		terminateReason = new TextField<String>();
		terminateReason.addListener(Events.OnChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				enableTerminateButton();
			}
			
		});

		terminateFields = new MultiField<String>();
		terminateFields.add(terminateLabel);
		terminateFields.add(terminateReason);
		
		TableData tableData = new TableData();
		tableData.setColspan(2);
		tableData.setWidth("100%");
		
		countsHtml = new Html("");
		countsHtml.addStyleName("exportCounts");
		
		messagesHtml = new Html("No message.");
		
		add(exportButton);
		add(displayFields);
		add(terminateButton);
		add(terminateFields);
		add(countsHtml, tableData);
		add(messagesHtml, tableData);
	}
	
	@Override
	protected void afterRender() {
		refreshExportProcessReport();
	}
	
	protected void initiateExport() {
		
		exportButton.disable();
		enableTerminateButton();

		initiateExportService.initiateExport(consoleOutput.getValue(), new AsyncCallback<ExportProcessReport>() {
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
						if (exportProcessReport == null) {
							messagesHtml.setHtml("Export initiated...");
							setExportProcessReportTimer();
						} else {
							refreshMessages(exportProcessReport);
							setExportProcessReportTimer();
						}
					}
			});
	}
	
	protected void refreshExportProcessReport() {
		getExportReportService.getExportReport(consoleOutput.getValue(), new AsyncCallback<ExportProcessReport>() {
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
							System.out.println("No export process report found.");
							refreshMessages(exportProcessReport);
							exportButton.enable();
							enableTerminateButton();
						} else {
							if (exportProcessReport.isRunning())
								setExportProcessReportTimer();
							refreshMessages(exportProcessReport);
							exportButton.setEnabled(!exportProcessReport.isRunning());
							enableTerminateButton();
						}
					}
			});
	}
	
	protected void terminateExport() {

		String reason = terminateReason.getValue() != null ? terminateReason.getValue() : null;		
		if (reason == null || reason.trim().length() == 0) {
			terminateReason.forceInvalid("A termination reason is required.");
//			MessageBox.alert("Termination Refused", "A terminate reason is required.", null);
			return;
		}

		terminateReason.setValue("");
		
		terminateExportService.terminateExport(reason, new AsyncCallback<ExportProcessReport>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException || caught instanceof AuthenticationExportException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Export termination failed unexpectedly: " + caught.getMessage(), null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(ExportProcessReport exportProcessReport) {
						if (exportProcessReport == null) {
							messagesHtml.setHtml("Export termination requested...");
							setExportProcessReportTimer();
						} else {
							refreshMessages(exportProcessReport);
							setExportProcessReportTimer();
						}
					}
			});
	}
	
	protected void enableTerminateButton() {
		terminateReason.clearInvalid();
		terminateButton.setEnabled(
				exportProcessReport != null
				&& exportProcessReport.isRunning()
				&& !exportButton.isEnabled() 
				&& terminateReason.getValue() != null 
				&& terminateReason.getValue().length() > 10
			);
	}
	
	protected void refreshMessages(ExportProcessReport exportProcessReport) {
		this.exportProcessReport = exportProcessReport;
		refreshMessages();
	}
	
	protected void refreshMessages() {
		if (exportProcessReport == null) {
			countsHtml.setHtml("");
			messagesHtml.setHtml("There is no export process report available.");
			return;
		}
		
		StringBuffer countHtml = new StringBuffer();
		countHtml.append(exportProcessReport.getAgreements());
		countHtml.append(" Agreements");
		countHtml.append(", ");
		countHtml.append(exportProcessReport.getSites());
		countHtml.append(" Sites");
		countHtml.append(", ");
		countHtml.append(exportProcessReport.getIps());
		countHtml.append(" IPs");
		countHtml.append(", ");
		countHtml.append(exportProcessReport.getUids());
		countHtml.append(" UIDs");
		countHtml.append(", ");
		countHtml.append(exportProcessReport.getUrls());
		countHtml.append(" URLs");
		countHtml.append(", ");
		countHtml.append(exportProcessReport.getErrors());
		countHtml.append(" Errors, ");
		countHtml.append(exportProcessReport.getElapsedMinutes());
		countHtml.append(" Minutes (or ");
		countHtml.append(exportProcessReport.getElapsedSeconds());
		countHtml.append(" Seconds)");
		
		countsHtml.setHtml(countHtml.toString());
		
		int messagesCount = (messageCount.getValue() != null) ? messageCount.getValue().intValue() : MESSAGES_TO_DISPLAY;
		List<ExportProcessMessage> messages = exportProcessReport.getLastMessages(messagesCount);
		
		if (messages == null) {
			messagesHtml.setHtml("No messages.");
			return;
		}
			
		
		StringBuffer msgHtml = new StringBuffer();
		for (ExportProcessMessage message : messages) {
			//	Note that this is all done in reverse sequence, so that messages are displayed last, not first
			msgHtml.insert(0, "</div>");
			msgHtml.insert(0, message.getMessage());
			msgHtml.insert(0, "</span>");
			msgHtml.insert(0, message.getDate());
			msgHtml.insert(0, "<span class=\"exportDate\">");
			msgHtml.insert(0, "\">");
			msgHtml.insert(0, message.getStyleName());
			msgHtml.insert(0, "<div class=\"");
		}
		
		messagesHtml.setHtml(msgHtml.toString());
	}
	
	protected void setExportProcessReportTimer() {
		setExportProcessReportTimer(0);
	}
	
	protected void setExportProcessReportTimer(int forceDelay) {
		if (exportProcessReportTimer == null) {
			exportProcessReportTimer = new Timer() {

				@Override
				public void run() {
					refreshExportProcessReport();
				}
			
			};
		}
		
		int delay = (refreshDelay.getValue() != null) ? refreshDelay.getValue().intValue() : 0;
		
		if (forceDelay > 0)
			delay = forceDelay;
		
		if (delay <= 0)
			delay = DEFAULT_TIMER_DELAY;
		
		exportProcessReportTimer.cancel();
		exportProcessReportTimer.schedule(delay * 1000);
	}

	@Override
	public void awaken() {
		refreshExportProcessReport();
	}

	@Override
	public void sleep() {
		if (exportProcessReportTimer != null) {
			exportProcessReportTimer.cancel();
		}
	}
}
