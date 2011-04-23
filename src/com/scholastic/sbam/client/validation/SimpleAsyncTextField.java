package com.scholastic.sbam.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * This simple extension of the text field class adds an ability to call an asynchronous validation service.
 * 
 * To implement any special synchronous validation, override the syncValidate method.
 * 
 * To implement any asynchronous validation, override the asyncValidate method, probably to do something like the following:
 * 
 * 	  validationService.validate(value, ++validationCounter, getValidationAsyncCallback());
 * 
 * @author Bob Lacatena
 *
 * @param <D>
 */
public abstract class SimpleAsyncTextField<D> extends TextField<D> {
	
	protected int	  					validationCounter	= 0;
	protected String					previousValue		= null;
	protected List<String>				asyncMessages		= null;
	protected boolean					asyncReady			= true;
	
	public SimpleAsyncTextField() {
		super();
	}
	
	
	@Override
	public boolean validateValue(String value) {
		boolean simpleValid = super.validateValue(value);
		if (!simpleValid) {
			return false;
		}
		
		List<String> messages = syncValidate(value);
		if (messages != null && messages.size() > 0) {
			markInvalid(messages);
			return false;
		}
		
		if (asyncReady) {
			if (previousValue == null || !previousValue.equals(value)) {
				previousValue = value;
				markInvalid("<em>Validating...</em>");
				asyncValidate(value);
			} else {
				if (asyncMessages != null && asyncMessages.size() > 0) {
					markInvalid(asyncMessages);
					return false;
				} else {
					clearInvalid();
					return true;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * This helper method can be used to easily create/append to a list of messages.
	 * 
	 * Declare the list, as in 
	 * 
	 * 		List<String> messages = null;
	 * 
	 * Then for each new message, 
	 * 
	 * 		messages = addToList(messages, "Your message here");
	 * 
	 * @param message
	 * The message to be added.
	 * @param messages
	 * The variable holding any previous list of messages.
	 * @return
	 * The list of messages, or null if there are no non-null messages so far.
	 */
	public List<String> addToList(String message, List<String> messages) {
		if (message == null || message.length() == 0)
			return null;
		if (messages == null)
			messages = new ArrayList<String>();
		messages.add(message);
		return messages;
	}
	
	/**
	 * Override this method to perform any immediate local validation.
	 * 
	 * By default, this method will do nothing.
	 * 
	 * @param value
	 * @return
	 * A list of all error messages relevant to the field.
	 */
	public  List<String> syncValidate(String value) {
		return null;
	}

	
	/**
	 * Override this method to initiate any asynchronous local validation.
	 * 
	 * By default, this method will do nothing.
	 * 
	 * Suggested implementation will call a remote service, such as:
	 * 
	 * 	  validationService.validate(value, ++validationCounter, getValidationAsyncCallback());
	 * 
	 * @param value
	 */
	public void asyncValidate(String value) {
//		validationService.validate(value, ++validationCounter, getValidationAsyncCallback());
	}
	
	public AsyncCallback<AsyncValidationResponse> getValidationAsyncCallback() {
		return new AsyncCallback<AsyncValidationResponse>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Validation failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(AsyncValidationResponse response) {
						//	Mark invalid if an error occurred, and if the response matches the current field validation count setting
						if (response.getValidationCounter() == validationCounter) {
							if (response.getMessages() != null && response.getMessages().size() > 0) {					
								asyncMessages = response.getMessages();
								markInvalid(asyncMessages);
							} else {
								asyncMessages = null;
								clearInvalid();
							}
						}
					}
			};
	}
	
	public void markInvalid(List<String> messages) {
		for (String message: messages)
			markInvalid(message);
	}
	
	public void reset() {
		super.reset();
		asyncReady			= false;
		validationCounter	= 0;
		asyncMessages		= null;
		previousValue		= null;
	//	clearInvalid();
	}

	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public int getValidationCounter() {
		return validationCounter;
	}

	public boolean isAsyncReady() {
		return asyncReady;
	}

	public void setAsyncReady(boolean asyncReady) {
		this.asyncReady = asyncReady;
	}
	
}
