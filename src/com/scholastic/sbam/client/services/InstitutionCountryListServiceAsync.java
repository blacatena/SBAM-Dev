package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.InstitutionCountryInstance;

public interface InstitutionCountryListServiceAsync {

	void getInstitutionCountries(LoadConfig loadConfig, AsyncCallback<List<InstitutionCountryInstance>> callback);

}
