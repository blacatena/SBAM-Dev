package com.scholastic.sbam.client.uiobjects.uireports;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.scholastic.sbam.client.stores.BetterFilterListStore;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;

public abstract class CheckBoxSelectionCard extends SnapshotCriteriaCardBase {
	
	protected static int	COLUMN_COUNT				=	4;
	protected static int	COLUMN_WIDTH				=	200;
	
	protected LayoutContainer	container				=	new LayoutContainer ( new TableLayout(getNumberOfColumns())); ;
	protected List<CheckBox>	checkBoxes				=	new ArrayList<CheckBox>();
	
	protected static BetterFilterListStore<BeanModel>		institutionStates	= new BetterFilterListStore<BeanModel>();

	/**
	 * Get the number of columns
	 * @return
	 */
	public int getNumberOfColumns() {
		return COLUMN_COUNT;
	}
	
	/**
	 * Get the width of each column.
	 * @return
	 */
	public int getColumnWidth() {
		return COLUMN_WIDTH;
	}

	/**
	 * Get the name of the parameter for these values.
	 * @return
	 */
	public abstract String getParameterName();
	
	/**
	 * Create the list of models, and call addColumns to add them to the panel as checkboxes
	 */
	public abstract void loadCheckBoxes();
	
	@Override
	public void populateFields() {
			// Nothing to do here
	}

	@Override
	protected void addCriteriaFields() {
		loadCheckBoxes();
		contentPanel.add(container);
	}

	@Override
	protected void setFields(SnapshotParameterSetInstance snapshotParameterSet) {
		setCheckBoxes(snapshotParameterSet, getParameterName(), checkBoxes);
	}

	@Override
	public void addParametersFromFields(SnapshotParameterSetInstance snapshotParameterSet) {
		addParametersFrom(checkBoxes, snapshotParameterSet, getParameterName(), getParameterName());
	}
	
	/**
	 * This adds the models to the table columns so they are in order first top to bottom, then left to right.
	 * @param table
	 * @param columnCount
	 * @param models
	 * @param keyName
	 */
	public void addColumns(LayoutContainer table, List<BeanModel> models, String keyName, List<CheckBox> boxList, String breakColumn) {

		String breakColVal = null;
		int col = 0;
		int row = 0;
		
		//	Distribute the models in proper table order... top to bottom, then left to right
		
		int perCol = models.size() / getNumberOfColumns();
		if (models.size() % getNumberOfColumns() > 0)
			perCol++;
		
		if (breakColumn != null) {	// Add rows for breaks
			breakColVal = null;
			for (BeanModel model : models) {
				if (breakColVal != null && !breakColVal.equals(model.get(breakColumn).toString())) {
					perCol++;
				}
				breakColVal = model.get(breakColumn).toString();
			}
		}
		
		breakColVal = null;
		BeanModel tableModels [] [] = new BeanModel [perCol] [getNumberOfColumns()];
		boolean   tableBreaks [] [] = new boolean   [perCol] [getNumberOfColumns()];
		for (int i = 0; i < models.size(); i++) {
			
			if (breakColumn != null) {
				if (breakColVal != null && !breakColVal.equals(models.get(i).get(breakColumn).toString())) {
					if (row + 1 < perCol) {
						tableBreaks [row] [col] = true;
						row++;	// This leaves a null cell that will get a break added to it
					}
				}
				breakColVal = models.get(i).get(breakColumn).toString();
			}
			
			tableModels [row] [col] = models.get(i);
			row++;
			if (row >= perCol) {
				row = 0;
				col++;
			}
		}
		
		//	Put the models (and any breaks) into the actual table
		
		TableData td = new TableData();
		td.setWidth(getColumnWidth() + "px");
		
		int modelCount = 0;
		for (row = 0; row < perCol; row++) {
			for (col = 0; col < getNumberOfColumns(); col++) {
				
				BeanModel model = tableModels [row] [col];
				if (model == null) {
					if (breakColumn != null && tableBreaks [row] [col]) {
						table.add(new Html("<hr />"));
					}
					continue;
				}
				
				CheckBox box = new CheckBox();
				box.setBoxLabel(model.get("description").toString());
				box.setValueAttribute(model.get(keyName).toString());

				table.add(box, td);
				boxList.add(box);
				
				//	Count the models, so when we're done we don't add needless "breaks" to the end
				modelCount++;
				if (modelCount >= models.size())
					break;
			}
			if (modelCount >= models.size())
				break;
		}
		
		table.layout(true);
		
	}

}
