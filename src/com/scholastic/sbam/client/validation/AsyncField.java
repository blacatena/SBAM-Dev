package com.scholastic.sbam.client.validation;

import java.util.List;

import com.extjs.gxt.ui.client.widget.form.Validator;
import com.scholastic.sbam.client.services.FieldValidationServiceAsync;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;

public interface AsyncField {
	
	
	
	public boolean validateValue(String value);
	
	/**
	 * Override this method to perform any immediate validation.
	 * 
	 * By default, this method will do validation using any set Validator instance.
	 * 
	 * @param value
	 * @return
	 * A list of all error messages relevant to the field.
	 */
	public List<String> syncValidate(String value);
	
	public void asyncValidate(String value);
	
	public void reset(BetterRowEditInstance instance);

	public Validator getValidator();

	public void setValidator(Validator validator);

	public FieldValidationServiceAsync getValidationService();

	public void setValidationService(FieldValidationServiceAsync validationService);

	public BetterRowEditInstance getDataInstance();

	public void setDataInstance(BetterRowEditInstance dataInstance);

	public int getValidationCounter();

	public boolean isAsyncReady();

	public void setAsyncReady(boolean asyncReady);
	
}
