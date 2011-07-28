package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.Timer;
import com.scholastic.sbam.client.stores.BetterFilterListStore;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.reporting.SnapshotParameterNames;

public class InstitutionCountrySelectionCard extends CheckBoxSelectionCard {
	
	protected static BetterFilterListStore<BeanModel>		institutionCountries	= new BetterFilterListStore<BeanModel>();

	@Override
	public String getPanelToolTip() {
		return "Select customers for the snapshot by country.";
	}

	@Override
	public String getPanelIconName() {
		return IconSupplier.getCountriesIconName();
	}


	@Override
	public String getPanelTitle() {
		return "By Country";
	}
	
	@Override
	public void loadCheckBoxes() {
//	public void loadCountries() {
		if (UiConstants.areInstitutionCountriesLoaded()) {
			loadCountriesNoWait();
		} else {
			UiConstants.loadInstitutionCountries();
			Timer timer = new Timer() {
				@Override
				public void run() {
					if (UiConstants.areInstitutionCountriesLoaded()) {
						this.cancel();
						loadCountriesNoWait();
					}
				}
			};
			timer.scheduleRepeating(200);
		}
	}
	
	public void loadCountriesNoWait() {
		addColumns(container, UiConstants.getInstitutionCountries().getModels(), "countryCode", checkBoxes, null);
	}


	@Override
	public String getParameterName() {
		return SnapshotParameterNames.INSTITUTION_COUNTRY;
	}

}
