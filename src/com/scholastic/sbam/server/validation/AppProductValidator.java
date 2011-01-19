package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.objects.DbTermType;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppProductValidator {
	
	private List<String> messages = new ArrayList<String>();
	
	private	ProductInstance original;
	private Product		 product;

	public List<String> validateProduct(ProductInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateProductCode(instance.getProductCode());
		validateDescription(instance.getDescription());
		validateShortName(instance.getShortName());
		validateDefaultTermType(instance.getDefaultTermType());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateProductCode(String value) {
		if (original.getProductCode() != null && original.getProductCode().length() > 0) {
			validateOldProductCode(value);
		} else {
			validateNewProductCode(value);
		}
		return messages;
	}
	
	public List<String> validateOldProductCode(String value) {
		if (!loadProduct())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A product code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(2)).validate(value));
		
		if (!product.getProductCode().equals(value))
			addMessage("Product code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewProductCode(String value) {
		addMessage((new CodeValidator()).validate(value));
		if (value != null && value.length() > 0) {
			Product conflict = DbProduct.getByCode(value);
			if (conflict != null) {
				addMessage("Product code already exists.");
			}
		}
		return messages;
	}
	
	public List<String> validateDescription(String description) {
		addMessage(new NameValidator().validate(description));
		return messages;
	}
	
	public List<String> validateShortName(String shortName) {
		addMessage(new NameValidator("short name").validate(shortName));
		return messages;
	}
	
	public List<String> validateDefaultTermType(char defaultTermType) {
		if (defaultTermType == (char) 0)
			addMessage("A default term type is required.");
		else {
			if (DbTermType.getByCode(defaultTermType) == null)
				addMessage("Default term type '" + defaultTermType + "' not found in the database.");
		}
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != 0 && status != 'A' && status != 'I' && status != 'X')
			addMessage("Invalid status " + status);
		return messages;
	}
	
	private boolean loadProduct() {
		if (product == null) {
			product = DbProduct.getByCode(original.getProductCode());
			if (product == null) {
				addMessage("Unexpected Error: Original product code not found in the database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public ProductInstance getOriginal() {
		return original;
	}

	public void setOriginal(ProductInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
