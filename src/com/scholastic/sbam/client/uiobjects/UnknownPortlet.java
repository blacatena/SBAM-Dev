package com.scholastic.sbam.client.uiobjects;

import com.scholastic.sbam.shared.objects.UserCacheTarget;

public class UnknownPortlet extends AppPortlet {

	public UnknownPortlet(String helpTextId) {
		super(helpTextId);
	}

	@Override
	public UserCacheTarget getUserCacheTarget() {
		return null;
	}

}
