package com.scholastic.sbam.client.uiobjects.foundation;

import com.scholastic.sbam.client.uiobjects.uiapp.AppPortlet;

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
