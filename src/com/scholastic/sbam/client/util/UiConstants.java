package com.scholastic.sbam.client.util;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.TermTypeListService;
import com.scholastic.sbam.client.services.TermTypeListServiceAsync;
import com.scholastic.sbam.shared.objects.TermTypeInstance;

public class UiConstants {
	private final static int REFRESH_PERIOD = 10 * 60 * 1000;	// Every 10 minutes
	
	private static ListStore<BeanModel>	termTypes = new ListStore<BeanModel>();
	private static Timer				refreshTimer;
	
	public static void init() {
		refresh();
		setTimer();
	}
	
	public static void refresh() {
		loadTermTypes();
	}
	
	public static void setTimer() {
		if (refreshTimer == null) {
			refreshTimer = new Timer() {
			      @Override
			      public void run() {
			        refresh();
			      }
			    };
		}
	
	    // Schedule the timer to run once in 5 minutes.
	    refreshTimer.schedule(REFRESH_PERIOD);

	}
	
	public static void cancel() {
		cancelTimer();
	}
	
	public static void cancelTimer() {
		if (refreshTimer != null)
			refreshTimer.cancel();
	}
	
	private static void loadTermTypes() {
		TermTypeListServiceAsync termTypeListService = GWT.create(TermTypeListService.class);
		
		AsyncCallback<List<TermTypeInstance>> callback = new AsyncCallback<List<TermTypeInstance>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				if (caught instanceof IllegalArgumentException)
					MessageBox.alert("Alert", caught.getMessage(), null);
				else {
					MessageBox.alert("Alert", "Term types load failed unexpectedly.", null);
					System.out.println(caught.getClass().getName());
					System.out.println(caught.getMessage());
				}
			}

			public void onSuccess(List<TermTypeInstance> list) {
				termTypes.removeAll();
				BeanModelFactory factory = null;
				for (TermTypeInstance instance : list) {
					if (factory == null)
						factory = BeanModelLookup.get().getFactory(instance.getClass());
					termTypes.add(factory.createModel(instance));	
				}
			}
		};
		
		termTypeListService.getTermTypes(null, callback);
	}
	
	public static ListStore<BeanModel> getTermTypes() {
		return termTypes;
	}
}
