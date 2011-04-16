package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

/**
 * An icon button field specifically designed to present notes as tool tips, and allow them to be edited. 
 * @author Bob Lacatena
 *
 */
public class NotesIconButtonField<D> extends IconButtonField<D> {
	
	protected LayoutContainer	constrainContainer;
	protected HtmlEditor		noteEditor;
	
	/**
	 * Whether or not the note field can ever be disabled.  Defaults to false.
	 */
	protected boolean	allowDisable	= false;
	/**
	 * Determines whether the icon displayed represents editing an existing note ("edit" mode) or adding a new one.
	 */
	protected boolean	editMode		= false;
	/**
	 * Set while the note is being updated on the backend, and so should not change again here.
	 */
	protected boolean	noteLocked		= false;
	/**
	 * The current text of the note.
	 */
	protected String	note			= "";
	/**
	 * The text to display when there is no note.
	 */
	protected String	emptyNoteText	= "Click the icon to create a note.";
	
	public NotesIconButtonField(LayoutContainer constrainContainer) {
		this.constrainContainer = constrainContainer;
		this.setTriggerStyle("trigger-notes-add");
	}
	
	/**
	 * Set the note to this value, along with the corresponding tooltip.
	 * @param note
	 */
	public void setNote(String note) {
		if (note == null)
			note = "";
		
		this.note = note;
		
		if (note.length() == 0)
			note = emptyNoteText;
		
		ToolTipConfig notesToolTip = new ToolTipConfig();
		notesToolTip.setTitle("<b>Notes</b>");  
		notesToolTip.setMouseOffset(new int[] {-2, 0});  
		notesToolTip.setAnchor("right");  
		notesToolTip.setCloseable(true);
		notesToolTip.setMaxWidth(415);
		notesToolTip.setMinWidth(200);
		notesToolTip.setText("<div class=\"noteToolTip\"><div>" + note + "</div></div>");
		
		this.setToolTip(notesToolTip);
	}

	public void setEditMode() {
		editMode = true;
		this.setTriggerStyle("trigger-notes-edit");
		if (trigger != null && trigger.dom != null)
			trigger.dom.setClassName("x-form-trigger " + triggerStyle);
	}

	public void setAddMode() {
		editMode = false;
		this.setTriggerStyle("trigger-notes-add");
		if (trigger != null && trigger.dom != null)
			trigger.dom.setClassName("x-form-trigger " + triggerStyle);
	}
	
	/**
	 * Do not disable if not allowed.
	 */
	@Override
	public void disable() {
		if (allowDisable) {
			super.disable();
		}
	}
	
	/**
	 * Do not disable if not allowed.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		if (enabled || allowDisable) {
			super.setEnabled(enabled);
		}
	}

	@Override
	protected void onTriggerClick(ComponentEvent ce) {
		this.hideToolTip();
		
		if (noteLocked)
			return;
		
		final Dialog simple = new Dialog() {
			@Override
			protected void onButtonPressed(Button button) {
				if ("Save".equalsIgnoreCase(button.getText()) || Dialog.OK.equalsIgnoreCase(button.getText())) {
					if (!noteEditor.getRawValue().equals(note)) {
						setNote(noteEditor.getRawValue());
						lockNote();
						updateNote(noteEditor.getRawValue());
					}
				}
				closeDialog();
			}

			protected void closeDialog() {
				if (constrainContainer != null)
					constrainContainer.unmask();
				this.hide();
				noteEditor = null;
			}
		};
		
	    simple.setHeading("Notes");
	    simple.setButtons(Dialog.OKCANCEL);
	    
	    //	Rename the OK button to Save
	    for (Component component : simple.getButtonBar().getItems())
	    	if (component instanceof Button) {
	    		Button button = (Button) component;
	    		if (button.getText().equalsIgnoreCase(Dialog.OK))
	    			button.setText("Save");
	    	}
	    
	    simple.setBodyStyleName("pad-text");
	    simple.setLayout(new FitLayout());
	    
	    noteEditor = new HtmlEditor();
	    noteEditor.setEmptyText("Enter your note here.");
	    noteEditor.setRawValue(note);
	    
	    simple.add(noteEditor);
	    simple.getItem(0).getFocusSupport().setIgnore(true);  
	    simple.setScrollMode(Scroll.NONE);
	    simple.setConstrain(true);
	    simple.setClosable(false);
	    
	    if (constrainContainer != null) {
	    	constrainContainer.mask();
	    	simple.setContainer(constrainContainer.getElement());
	    	simple.setSize(constrainContainer.getWidth() - 200, constrainContainer.getHeight() - 200);
	    	simple.setPagePosition(constrainContainer.getAbsoluteLeft() + 100, constrainContainer.getAbsoluteTop() + 100);
	    }
	    simple.show();
	}
	
	public void lockNote() {
		noteLocked = true;
		super.disable();
	}
	
	public void unlockNote() {
		noteLocked = false;
		super.enable();
	}

	public boolean isAllowDisable() {
		return allowDisable;
	}

	public void setAllowDisable(boolean allowDisable) {
		this.allowDisable = allowDisable;
	}
	
	public String getNote() {
		return note;
	}

	public String getEmptyNoteText() {
		return emptyNoteText;
	}

	public void setEmptyNoteText(String emptyNoteText) {
		this.emptyNoteText = emptyNoteText;
	}

	public void updateNote(String note) {
		System.out.println("Implement this method to actually update the note.  Remember to unlock the note field after the update completes.");
	}
}
