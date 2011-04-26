package com.scholastic.sbam.client.uiobjects.fields;

import com.google.gwt.core.client.GWT;
import com.scholastic.sbam.client.services.SiteLocationValidationService;
import com.scholastic.sbam.client.services.SiteLocationValidationServiceAsync;
import com.scholastic.sbam.client.validation.SimpleAsyncTextField;

/**
 * A field for free entry of a new site location code for a UCN + suffix.
 * 
 * Asynchronous validation is done to be sure the location code has not already been used (created) for that UCN + suffix.
 * 
 * @author Bob Lacatena
 *
 */
public class SiteLocCodeField extends SimpleAsyncTextField<String> {
	protected final SiteLocationValidationServiceAsync		validationService		= GWT.create(SiteLocationValidationService.class);
	
	protected int ucn;
	protected int ucnSuffix;
	
	public SiteLocCodeField() {
		super();
	}
	
	public SiteLocCodeField(int ucn, int ucnSuffix) {
		super();
		this.ucn		= ucn;
		this.ucnSuffix	= ucnSuffix;
	}
	
	@Override
	public void asyncValidate(String value) {
		validationService.validateSiteLocation(ucn, ucnSuffix, value, ++validationCounter, getValidationAsyncCallback());
	}

	public int getUcn() {
		return ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public int getUcnSuffix() {
		return ucnSuffix;
	}

	public void setUcnSuffix(int ucnSuffix) {
		this.ucnSuffix = ucnSuffix;
	}
	
}
