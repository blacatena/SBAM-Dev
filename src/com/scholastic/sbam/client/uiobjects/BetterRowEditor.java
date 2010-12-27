package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;

/**
 * This extension of the GXT RowEditor adds functionality for a Delete button, as well as automatic Store reject/commit changes
 * when the Cancel or Save or Delete buttons are pressed.
 * 
 * Implementations can customize the setting of the key properties (which identify a new record), delete property (i.e. the property which will denote a record as deleted)
 * and the delete value (i.e. the value of the delete property which means the record is deleted).
 * 
 * Implementations can also override the deletePressed(), doDelete(), isDeleted(), canDelete(), isNewRecord() methods to customize those behaviors, but generally customization can be done
 * by implementing the proper methods in the data instance, which should extend BetterRowEditInstance.
 * 
 * The Store (ListStore) must have a listener which calls the necessary backend service on an Update/COMMIT event.
 * 
 * ALTERNATE STRATEGY: Have the BetterRowEditor cancel changes and send the asynch event, which on return must remove the deleted row from the store.  This way the row isn't removed from the grid until it's
 * successfully removed from the database, and there's no disconnect between the RowEditor removing the row from the grid and store, versus the store removing the correct row from the database.  Either
 * strategy will work...  The disadvantage to this is that a delay on the backend will be disconcerting to the user, and he may try to delete the row multiple times.
 * 
 * @author Bob Lacatena
 *
 * @param <M>
 */
public class BetterRowEditor<M extends ModelData> extends RowEditor<ModelData> {
	
	private Button delButton;
	private ListStore<BeanModel>	store;
	
	/**
	 * Don't let a programmer create this without a store being declared
	 */
	@SuppressWarnings("unused")
	private BetterRowEditor() {
		super();
	}
	
	public BetterRowEditor(ListStore<BeanModel> store) {
		super();
		this.store = store;
	}
	
	/**
	 * Overridden to expand the buttons layout container to include the Delete button.
	 */
	@Override
	protected void onRender(Element target, int index) {
			super.onRender(target, index);
			//	Add a Delete button if the id is not null, i.e. a record exists
			if (btns != null) {
				btns.setLayout(new TableLayout(3));
				delButton = new Button("Delete");      
				delButton.addListener(Events.Select, new Listener<ButtonEvent>() {
					public void handleEvent(ButtonEvent be) {
						deletePressed(be);
					}
				});
				delButton.setMinWidth(getMinButtonWidth());
				btns.add(delButton);
				btns.layout(true);
			}
	}
	
	/**
	 * Overridden to expand the buttons layout width to accommodate three buttons (Cancel, Save, Delete).
	 */
	@Override
	protected void afterRender() {
		super.afterRender();
		if (renderButtons) {
			btns.setWidth((getMinButtonWidth() * 3) + (5 * 3) + (3 * 4));
		}
	}
	
	/**
	 * Handle the delete button being pressed.
	 * 
	 * The default behavior is to show a confirmation dialog, and then to perform the doDelete() method if the user confirms the action.
	 * @param be
	 */
	protected void deletePressed(ButtonEvent be) {
		final Listener<MessageBoxEvent> confirmDelete = new Listener<MessageBoxEvent>() {  
			public void handleEvent(MessageBoxEvent ce) {  
				Button btn = ce.getButtonClicked();
				if ("Yes".equals(btn.getText()))
					doDelete();
			}  
		};
		MessageBox.confirm("Confirm Delete", "Are you sure you want to delete this entry?", confirmDelete);
	}
	
	/**
	 * Delete a record by setting its delete property to the delete value, and then doing a stopEditing(false).
	 */
	protected void doDelete() {
		
		//	Let the instance mark itself as deleted
		BetterRowEditInstance instance = (BetterRowEditInstance) store.getAt(rowIndex).getBean();
		store.getRecord(store.getAt(rowIndex)).set(instance.returnTriggerProperty(), instance.returnTriggerValue());	// Just to make the store fires an update event
		instance.markForDeletion();
		
		//	Stop editing and make the updates
		stopEditing(true);
	}
	
	/**
	 * startEditing, with added functionality to enable or disable the delete button.
	 * 
	 * The preference for overriding this functionality is to override the canDelete(Store) method.
	 */
	@Override
	public void startEditing(int rowIndex, boolean doFocus) {
		super.startEditing(rowIndex, doFocus);
		if (canDelete(store.getAt(rowIndex)))
			delButton.enable();
		else
			delButton.disable();
	}
	
	/**
	 * stopEditing, with the added functionality of rejecting any changes in the Store if the user hit Cancel, and then removing all empty rows, or else automatically
	 * committing the changes to the store.
	 */
	@Override
	public void stopEditing(boolean saveChanges) {
		boolean validChanges = isValid();
		super.stopEditing(saveChanges);
		if (!saveChanges) {
			store.rejectChanges();
			removeEmptyRows(saveChanges);
		} else if (validChanges) {
			store.commitChanges();
			removeEmptyRows(saveChanges);
		}
	}
	
	/**
	 * Altered to count a record as valid if it is marked as deleted (i.e. it doesn't matter what the values are).
	 */
	@Override
	public boolean isValid() {
		if (isDeleted(store.getAt(rowIndex)))
			return true;
		boolean fieldsAreValid = super.isValid();
		if (fieldsAreValid) {
			BetterRowEditInstance instance = (BetterRowEditInstance) store.getAt(rowIndex).getBean();
			fieldsAreValid = instance.thisIsValid();
		}
		//	Now do a higher level of validation, e.g. multiple field relationships -- warning, THIS GETS CALLED REPEATEDLY during editing!!!!
		return fieldsAreValid;
	}
	
	/**
	 * Remove any rows in the database which are either deleted or new but incomplete (i.e. do not contain any key values).
	 */
	protected void removeEmptyRows(boolean saveChanges) {
		for (BeanModel data : store.getModels()) {
			//	Rows with no id and no user name are considered "empty"
			if (isNewRecord(data)) {
				if (!saveChanges)
					store.remove(data);
			} else if (isDeleted(data)) {
				store.remove(data);
			}
		}
	}
	
	/**
	 * Has this row been deleted?
	 * 
	 * The default implementation determines this by checking if the declared delete property value is equal to the declared delete value.
	 * @param data
	 * @return
	 */
	protected boolean isDeleted(BeanModel data) {
		BetterRowEditInstance instance = (BetterRowEditInstance) data.getBean();
		return instance.thisIsDeleted();
	}
	
	/**
	 * Can this row be deleted.
	 * 
	 * The default implementation will delete any row that is not already deleted -- !isDeleted() -- and that does not yet exist in the database -- !isNewRecord() --
	 * @param data
	 * @return
	 */
	protected boolean canDelete(BeanModel data) {
		return !isDeleted(data) && !isNewRecord(data);
	}
	
	/**
	 * Is this a new record (i.e. one that does not yet exist in the database).
	 * 
	 * This is determined by finding if all of the key values for the record are null or empty strings.
	 * 
	 * @param data
	 * @return
	 */
	protected boolean isNewRecord(BeanModel data) {
		BetterRowEditInstance instance = (BetterRowEditInstance) data.getBean();
		return instance.thisIsNewRecord();
	}

	public ListStore<BeanModel> getStore() {
		return store;
	}
	
	public void setStore(ListStore<BeanModel> store) {
		this.store = store;
	}
}
