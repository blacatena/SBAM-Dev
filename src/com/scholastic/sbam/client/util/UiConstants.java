package com.scholastic.sbam.client.util;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.DefaultCurrencyData;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.CommissionTypeListService;
import com.scholastic.sbam.client.services.CommissionTypeListServiceAsync;
import com.scholastic.sbam.client.services.TermTypeListService;
import com.scholastic.sbam.client.services.TermTypeListServiceAsync;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.TermTypeInstance;

public class UiConstants {
	public static final int				QUICK_TOOL_TIP_SHOW_DELAY		= 3000;
	public static final int				QUICK_TOOL_TIP_DISMISS_DELAY	= 2000;
	public static final DateTimeFormat	APP_DATE_TIME_FORMAT			= DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);	//	DateTimeFormat.getFormat("MM/dd/yy");
	public static final CurrencyData	US_DOLLARS						=	new DefaultCurrencyData("840", "$");;
	
	public enum CommissionTypeTargets {
		PRODUCT, SITE, AGREEMENT, AGREEMENT_TERM;
	}
	
	private final static int REFRESH_PERIOD = 10 * 60 * 1000;	// Every 10 minutes
	
	private static ListStore<BeanModel>	termTypes = new ListStore<BeanModel>();
	private static ListStore<BeanModel>	commissionTypes = new ListStore<BeanModel>();
	private static Timer				refreshTimer;
	
	public static void init() {
		refresh();
		setTimer();
	}
	
	public static void refresh() {
		loadTermTypes();
		loadCommissionTypes();
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
	
	private static void loadCommissionTypes() {
		CommissionTypeListServiceAsync commissionTypeListService = GWT.create(CommissionTypeListService.class);
		
		AsyncCallback<List<CommissionTypeInstance>> callback = new AsyncCallback<List<CommissionTypeInstance>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				if (caught instanceof IllegalArgumentException)
					MessageBox.alert("Alert", caught.getMessage(), null);
				else {
					MessageBox.alert("Alert", "Commission types load failed unexpectedly.", null);
					System.out.println(caught.getClass().getName());
					System.out.println(caught.getMessage());
				}
			}

			public void onSuccess(List<CommissionTypeInstance> list) {
				commissionTypes.removeAll();
				if (commissionTypes.getKeyProvider() == null)
					commissionTypes.setKeyProvider(CommissionTypeInstance.obtainStaticModelKeyProvider());
				BeanModelFactory factory = null;
				for (CommissionTypeInstance instance : list) {
					if (factory == null)
						factory = BeanModelLookup.get().getFactory(instance.getClass());
					commissionTypes.add(factory.createModel(instance));	
				}
			}
		};
		
		commissionTypeListService.getCommissionTypes(null, callback);
	}
	
	public static ListStore<BeanModel> getCommissionTypes() {
		return commissionTypes;
	}
	
	public static ListStore<BeanModel> getCommissionTypes(CommissionTypeTargets target) {
		ListStore<BeanModel> list = new ListStore<BeanModel>();
		list.setKeyProvider(CommissionTypeInstance.obtainStaticModelKeyProvider());
		
		for (BeanModel model : commissionTypes.getModels()) {
			CommissionTypeInstance instance = (CommissionTypeInstance) model.getBean();
			if (target == CommissionTypeTargets.PRODUCT && instance.isProducts())
				list.add(model);
			else if (target == CommissionTypeTargets.SITE && instance.isSites())
				list.add(model);
			else if (target == CommissionTypeTargets.AGREEMENT && instance.isAgreements())
				list.add(model);
			else if (target == CommissionTypeTargets.AGREEMENT_TERM && instance.isAgreements())
				list.add(model);
		}
		
		return list;
	}

	public static ToolTipConfig getQuickTip(String toolTip) {
		ToolTipConfig config = new ToolTipConfig();
		config.setText(toolTip);
		config.setShowDelay(QUICK_TOOL_TIP_SHOW_DELAY);
		config.setDismissDelay(QUICK_TOOL_TIP_DISMISS_DELAY);
		config.setAnchor("right");
		config.setAnchorOffset(20);
		config.setAnchorToTarget(true);
		return config;
	}
}
