package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.Timer;
import com.scholastic.sbam.client.stores.BetterFilterListStore;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.reporting.SnapshotParameterNames;

public class InstitutionStateSelectionCard extends CheckBoxSelectionCard {
	
	protected static BetterFilterListStore<BeanModel>		institutionStates	= new BetterFilterListStore<BeanModel>();

	@Override
	public String getPanelToolTip() {
		return "Select customers for the snapshot by state.";
	}

	@Override
	public String getPanelIconName() {
		return IconSupplier.getUsaIconName();
	}


	@Override
	public String getPanelTitle() {
		return "By U.S. State or Canadian Province";
	}
	
	@Override
	public void loadCheckBoxes() {
//	public void loadStates() {
		if (UiConstants.areInstitutionStatesLoaded()) {
			loadStatesNoWait();
		} else {
			UiConstants.loadInstitutionStates();
			Timer timer = new Timer() {
				@Override
				public void run() {
					if (UiConstants.areInstitutionStatesLoaded()) {
						this.cancel();
						loadStatesNoWait();
					}
				}
			};
			timer.scheduleRepeating(200);
		}
	}
	
	public void loadStatesNoWait() {
		addColumns(container, UiConstants.getInstitutionStates().getModels(), "stateCode", checkBoxes, "countryCode");
	}


	@Override
	public String getParameterName() {
		return SnapshotParameterNames.INSTITUTION_STATE;
	}

}
