package com.scholastic.sbam.client.uiobjects.events;

import com.extjs.gxt.ui.client.event.BaseObservable;

public class AppEventBus extends BaseObservable {
	// singleton pattern
	private static AppEventBus instance;
	
	private AppEventBus() {
	}
	
	public static synchronized AppEventBus getSingleton() {
		if (instance == null)
			instance = new AppEventBus();
		return instance;
	}
}
