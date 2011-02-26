package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.HelpTextService;
import com.scholastic.sbam.client.services.HelpTextServiceAsync;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.HelpTextInstance;

/**
 * A dialog window to display and navigate help text.
 * 
 * @author Bob Lacatena
 *
 */
public class HelpTextDialog extends EffectsDialog {
	
	private final HelpTextServiceAsync helpTextService = GWT.create(HelpTextService.class);
	
	
	private	String 				helpTextId;
	private	HelpTextInstance	helpText;
	
	protected Html text = new Html();
	protected Button done;
	protected Status status;

	public HelpTextDialog(String helpTextId) {
		this.helpTextId = helpTextId;
		init();
	}

	public HelpTextDialog() {
		init();
	}

	public void init() {
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(90);
		layout.setDefaultWidth(450);
		setLayout(layout);
	
//		setAnimCollapse(true);
		setButtonAlign(HorizontalAlignment.LEFT);
		setButtons("");
		IconSupplier.setIcon(this, IconSupplier.getNoteIconName());
		setHeading("SBAM Help");
		setModal(true);
		setBodyBorder(true);
		setBodyStyle("padding: 8px;background: none");
		setWidth(500);
		setHeight(500);
		setResizable(true);
		setClosable(true);
		setShadow(true);
		setShadowOffset(getShadowOffset() + (getShadowOffset() / 2));
		this.setAutoHide(true);
		this.removeFromParentOnHide = true;
		
		add(text);
		
		if (helpTextId != null && helpTextId.length() > 0)
			loadHelpText(helpTextId);
		else
			formatBlankPage();
		
//		KeyListener keyListener = new KeyListener() {
//		public void componentKeyUp(ComponentEvent event) {
//		validate();
//		}
//	
//		};

//		setFocusWidget(userName);

	}

	@Override
	protected void createButtons() {
		super.createButtons();
		status = new Status();
		status.setBusy("Loading...");
		status.hide();
		status.setAutoWidth(true);
		getButtonBar().add(status);
		
		getButtonBar().add(new FillToolItem());
		
		done = new Button("Done");
		done.addSelectionListener(new SelectionListener<ButtonEvent>() {
				public void componentSelected(ButtonEvent ce) {
					hide(null);
				}
		
			});
	
		addButton(done);
	}
	
	public void show(boolean animate) {
		super.show(animate);
	}

	protected void loadHelpText(final String id) {
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		helpTextService.getHelpText(id,
				new AsyncCallback<HelpTextInstance>() {
					public void onFailure(Throwable caught) {
						setHelpText(null);
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Help text access failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(HelpTextInstance helpText) {
						setHelpText(helpText);
					}
			});
	}
	
	private void formatPage() {
		if (helpText == null) {
			formatBlankPage();
			return;
		}
		
		this.setHeading(helpText.getTitle());
		if (helpText.getIconName() != null && helpText.getIconName().length() > 0)
			IconSupplier.setIcon(this, helpText.getIconName());
		else
			IconSupplier.setIcon(this, IconSupplier.getHelpIconName());
		text.setHtml(helpText.getText());
	}
	
	private void formatBlankPage() {
		text.setHtml("<em>This page intentionally left blank.</em>");
	}
	
	public void setHelpText(HelpTextInstance helpText) {
		if(helpText != null)
			this.helpTextId = helpText.getId();	//	Can't use the setter here of all places, because it will trigger an infinite loop of reloads
		this.helpText = helpText;
		formatPage();
	}

	public String getHelpTextId() {
		return helpTextId;
	}

	public void setHelpTextId(String helpTextId) {
		this.helpTextId = helpTextId;
		loadHelpText(helpTextId);
	}

	public HelpTextInstance getHelpText() {
		return helpText;
	}
	
}
