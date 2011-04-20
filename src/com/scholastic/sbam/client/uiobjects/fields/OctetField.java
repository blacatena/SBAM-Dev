package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.widget.form.TextField;

public class OctetField extends TextField<String> {
	public static final String WILDCARD = "*";
	
	OctetField prevField;
	OctetField nextField;
	
	boolean		wildcardAllowed =	true;
	boolean		allBlankAllowed =	false;
	
	public OctetField() {
		super();
		setAllowBlank(true);
	}
	
	public boolean isBlank() {
		return (getValue() == null || getValue().trim().length() == 0);
	}
	
	public boolean isWildcard() {
		return (getValue() == null || WILDCARD.equals(getValue().trim()));
	}
	
	@Override
	protected boolean validateValue(String octet) {
//		 if (!super.validateValue(octet))
//			 return false;
		if (octet == null || octet.trim().length() == 0) {
			if (prevField != null && prevField.isBlank()) {
				clearInvalid();
				return true;
			}
			if (prevField != null && prevField.isWildcard()) {
				clearInvalid();
				return true;
			}
			if (allBlankAllowed && prevField == null) {
				clearInvalid();
				return true;
			}
			markInvalid("May not be blank.");
			return false;
		}
		
		if (prevField != null && prevField.isBlank()) {
			markInvalid("Must be blank.");
			return false;
		}
		
		if (prevField != null && prevField.isWildcard()) {
			markInvalid("Must be blank.");
			return false;
		}
		
		octet = octet.trim();
		if (   (octet.matches("[0-9]"))
			|| (octet.matches("[0-9][0-9]"))
			|| (octet.matches("[0-1][0-9][0-9]"))
			|| (octet.matches("2[0-4][0-9]"))
			|| (octet.matches("25[0-5]")) ) {
				clearInvalid();
				return true;
		}
		
		if (wildcardAllowed && octet.equals(WILDCARD)) {
			clearInvalid();
			return true;
		}
		
		if (wildcardAllowed)
			markInvalid("Enter 0 to 255, or a *.");
		else
			markInvalid("Enter 0 to 255.");
		return false;
	}

	public OctetField getPrevField() {
		return prevField;
	}

	public void setPrevField(OctetField prevField) {
		this.prevField = prevField;
	}

	public OctetField getNextField() {
		return nextField;
	}

	public void setNextField(OctetField nextField) {
		this.nextField = nextField;
	}

	public boolean isWildcardAllowed() {
		return wildcardAllowed;
	}

	public void setWildcardAllowed(boolean wildcardAllowed) {
		this.wildcardAllowed = wildcardAllowed;
	}

	public boolean isAllBlankAllowed() {
		return allBlankAllowed;
	}

	public void setAllBlankAllowed(boolean allBlankAllowed) {
		this.allBlankAllowed = allBlankAllowed;
	}
	
}
