package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.CardPanel;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.layout.TableRowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;

public class StickyNoteEditor extends Composite {
	private CardLayout noteLayout;
	private CardPanel displayPanel;
	private Button btnDone;
	private HtmlEditor htmlEditor;
	StickyNoteDisplay stickyNoteDisplay;

	public StickyNoteEditor() {
		
		LayoutContainer editorContainer = new LayoutContainer();
		editorContainer.setLayout(new BorderLayout());
		
		htmlEditor = new HtmlEditor();
		editorContainer.add(htmlEditor, new BorderLayoutData(LayoutRegion.CENTER));
		htmlEditor.setFieldLabel("New HtmlEditor");
		
		LayoutContainer toolsContainer = new LayoutContainer();
		toolsContainer.setHeight(25);
		TableRowLayout trl_toolsContainer = new TableRowLayout();
		trl_toolsContainer.setWidth("100%");
		trl_toolsContainer.setCellHorizontalAlign(HorizontalAlignment.RIGHT);
		toolsContainer.setLayout(trl_toolsContainer);
		BorderLayoutData bld_toolsContainer = new BorderLayoutData(LayoutRegion.SOUTH, 25.0f);
		bld_toolsContainer.setMaxSize(25);
		bld_toolsContainer.setMinSize(25);
		
		ToolBar toolBar = new ToolBar();
		
		FillToolItem fillToolItem = new FillToolItem();
		toolBar.add(fillToolItem);
		
		btnDone = new Button("Done");
		btnDone.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (stickyNoteDisplay != null)
					stickyNoteDisplay.setText(getText());
				if (displayPanel != null && noteLayout != null)
					noteLayout.setActiveItem(displayPanel);
			}
		});
		toolBar.add(btnDone);
		toolsContainer.add(toolBar);
		editorContainer.add(toolsContainer, bld_toolsContainer);
		initComponent(editorContainer);
	}

	public CardLayout getNoteLayout() {
		return noteLayout;
	}

	public void setNoteLayout(CardLayout noteLayout) {
		this.noteLayout = noteLayout;
	}

	public CardPanel getDisplayPanel() {
		return displayPanel;
	}

	public void setDisplayPanel(CardPanel displayPanel) {
		this.displayPanel = displayPanel;
	}

	public Button getBtnDone() {
		return btnDone;
	}

	public void setBtnDone(Button btnDone) {
		this.btnDone = btnDone;
	}

	public HtmlEditor getHtmlEditor() {
		return htmlEditor;
	}

	public void setHtmlEditor(HtmlEditor htmlEditor) {
		this.htmlEditor = htmlEditor;
	}

	public StickyNoteDisplay getStickyNoteDisplay() {
		return stickyNoteDisplay;
	}

	public void setStickyNoteDisplay(StickyNoteDisplay stickyNoteDisplay) {
		this.stickyNoteDisplay = stickyNoteDisplay;
	}
	
	public String getText() {
		return htmlEditor.getValue();
	}
	
	public void setText(String html) {
		htmlEditor.setValue(html);
	}

}
