package com.scholastic.sbam.client.uiobjects;

public class UnknownPortlet extends AppPortlet {

	public UnknownPortlet(String helpTextId) {
		super(helpTextId);
	}

//	@Override
//	public UserCacheTarget getUserCacheTarget() {
//		return null;
//	}

	@Override
	public void setFromKeyData(String keyData) {
		
	}

	@Override
	public String getKeyData() {
		return "";
	}

}
