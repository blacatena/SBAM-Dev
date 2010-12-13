package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.CardPanel;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.layout.TableRowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;

public class StickyNoteDisplay extends Composite {
	private Button btnEdit;
	private CardLayout noteLayout;
	private CardPanel editPanel;
	private Html htmlNote;
	private StickyNoteEditor stickyNoteEditor;

	public StickyNoteDisplay() {
		
		LayoutContainer editorContainer = new LayoutContainer();
		editorContainer.setLayout(new BorderLayout());
		
		htmlNote = new Html("Place your thoughts <em>here</em>.");
		editorContainer.add(htmlNote, new BorderLayoutData(LayoutRegion.CENTER));
		
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
		
		btnEdit = new Button("Edit");
		btnEdit.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (stickyNoteEditor != null)
					stickyNoteEditor.setText(getText());
				if (editPanel != null && noteLayout != null)
					noteLayout.setActiveItem(editPanel);
			}
		});
		toolBar.add(btnEdit);
		toolsContainer.add(toolBar);
		editorContainer.add(toolsContainer, bld_toolsContainer);
		initComponent(editorContainer);
	}

	public Button getBtnEdit() {
		return btnEdit;
	}

	public void setBtnEdit(Button btnEdit) {
		this.btnEdit = btnEdit;
	}

	public CardLayout getNoteLayout() {
		return noteLayout;
	}

	public void setNoteLayout(CardLayout noteLayout) {
		this.noteLayout = noteLayout;
	}

	public CardPanel getEditPanel() {
		return editPanel;
	}

	public void setEditPanel(CardPanel editPanel) {
		this.editPanel = editPanel;
	}

	public Html getHtmlNote() {
		return htmlNote;
	}

	public void setHtmlNote(Html htmlNote) {
		this.htmlNote = htmlNote;
	}

	public StickyNoteEditor getStickyNoteEditor() {
		return stickyNoteEditor;
	}

	public void setStickyNoteEditor(StickyNoteEditor stickyNoteEditor) {
		this.stickyNoteEditor = stickyNoteEditor;
	}
	
	public String getText() {
		return htmlNote.getHtml();
	}
	
	public void setText(String html) {
		htmlNote.setHtml(html);
	}

}
