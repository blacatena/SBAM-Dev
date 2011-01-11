package com.scholastic.sbam.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.FieldValidationServiceAsync;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

public class AsyncTextField<D> extends TextField<D> implements AsyncField {
	
	private int	  					validationCounter	= 0;
	private Validator				validator 			= null;
	private BetterRowEditInstance	dataInstance		= null;
	private String					previousValue		= null;
	private List<String>			asyncMessages		= null;
	private boolean					asyncReady			= false;
	
	private FieldValidationServiceAsync validationService	= null;
	
	public AsyncTextField() {
		super();
	}
	
	public AsyncTextField(FieldValidationServiceAsync validationService) {
		super();
		this.validationService = validationService;
	}
	
	public AsyncTextField(FieldValidationServiceAsync validationService, Validator validator) {
		this(validationService);
		this.validator			=	validator;
	}
	
//	public void addListener() {
//		this.addListener(Events.Valid, new Listener<FieldEvent>() {
//			public void handleEvent(FieldEvent fe) {
//				if (fe.getType() == Events.Valid) {
//					System.out.println("Got Valid Event on " + fe.getField().getName());
//				}
//			}
//		});
//	}
	
	
	@Override
	public boolean validateValue(String value) {
		List<String> messages = syncValidate(value);
		if (messages != null && messages.size() > 0) {
			for (String message : messages)
				markInvalid(message);
			return false;
		}
		if (asyncReady && validationService != null) {
			if (previousValue == null || !previousValue.equals(value)) {
				previousValue = value;
				markInvalid("<em>Validating...</em>");
				asyncValidate(value);
			} else {
			//	System.out.println("No async validate needed");
				if (asyncMessages != null && asyncMessages.size() > 0) {
					markInvalid(asyncMessages);
					return false;
				}
			}
		} //else System.out.println("ready? " + asyncReady);
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
	 * Override this method to perform any immediate validation.
	 * 
	 * By default, this method will do validation using any set Validator instance.
	 * 
	 * @param value
	 * @return
	 * A list of all error messages relevant to the field.
	 */
	public  List<String> syncValidate(String value) {
	//	System.out.println("syncValidate " + validationCounter);
		if (validator != null)
			return addToList(validator.validate(this, value), null);
		else
			return null;
	}
	
	public void asyncValidate(String value) {
		validationService.validate(value, dataInstance, ++validationCounter,
				new AsyncCallback<AsyncValidationResponse>() {
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
			});
	}
	
	public void markInvalid(List<String> messages) {
		for (String message: messages)
			markInvalid(message);
	}
	
	public void reset(BetterRowEditInstance instance) {
		asyncReady			= false;
		validationCounter	= 0;
		asyncMessages		= null;
		previousValue		= null;
	//	clearInvalid();
		setDataInstance(instance);
	}

	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public FieldValidationServiceAsync getValidationService() {
		return validationService;
	}

	public void setValidationService(FieldValidationServiceAsync validationService) {
		this.validationService = validationService;
	}

	public BetterRowEditInstance getDataInstance() {
		return dataInstance;
	}

	public void setDataInstance(BetterRowEditInstance dataInstance) {
		this.dataInstance = dataInstance;
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
