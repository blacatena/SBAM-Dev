package com.scholastic.sbam.client.util;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.DefaultCurrencyData;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.CancelReasonListService;
import com.scholastic.sbam.client.services.CancelReasonListServiceAsync;
import com.scholastic.sbam.client.services.CommissionTypeListService;
import com.scholastic.sbam.client.services.CommissionTypeListServiceAsync;
import com.scholastic.sbam.client.services.DeleteReasonListService;
import com.scholastic.sbam.client.services.DeleteReasonListServiceAsync;
import com.scholastic.sbam.client.services.ProductListService;
import com.scholastic.sbam.client.services.ProductListServiceAsync;
import com.scholastic.sbam.client.services.TermTypeListService;
import com.scholastic.sbam.client.services.TermTypeListServiceAsync;
import com.scholastic.sbam.client.stores.BetterFilterListStore;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.TermTypeInstance;

public class UiConstants {
	public static final int				QUICK_TOOL_TIP_SHOW_DELAY		= 3000;
	public static final int				QUICK_TOOL_TIP_DISMISS_DELAY	= 2000;
	public static final DateTimeFormat	APP_DATE_LONG_FORMAT			= DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
	public static final DateTimeFormat	APP_DATE_TIME_FORMAT			= DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);	//	DateTimeFormat.getFormat("MM/dd/yy");
	public static final DateTimeFormat	APP_DATE_PLUS_TIME_FORMAT		= DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
	public static final CurrencyData	US_DOLLARS						= new DefaultCurrencyData("840", "$");
	public static final NumberFormat	INTEGER_FORMAT					= NumberFormat.getFormat("#");
	public static final NumberFormat	DOLLARS_FORMAT					= NumberFormat.getCurrencyFormat(US_DOLLARS);
	
	/**
	 * This enumeration helps to support the fact that different commission type lists will be delivered for different targets.
	 * @author Bob Lacatena
	 *
	 */
	public enum CommissionTypeTargets {
		PRODUCT, SITE, AGREEMENT, AGREEMENT_TERM;
	}
	
	private final static int			REFRESH_PERIOD = 10 * 60 * 1000;	// Every 10 minutes
	
	private static ListStore<BeanModel>					termTypes = new ListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		commissionTypes = new BetterFilterListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		deleteReasons = new BetterFilterListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		cancelReasons = new BetterFilterListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		products = new BetterFilterListStore<BeanModel>();
	
	private static Timer								refreshTimer;
	
	public static void init() {
		refresh();
		setTimer();
	}
	
	public static void refresh() {
		loadTermTypes();
		loadCommissionTypes();
		loadDeleteReasons();
		loadCancelReasons();
		loadProducts();
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
	
	public static void loadTermTypes() {
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
				for (TermTypeInstance instance : list) {
					termTypes.add(TermTypeInstance.obtainModel(instance));	
				}
			}
		};
		
		termTypeListService.getTermTypes(null, callback);
	}
	
	public static ListStore<BeanModel> getTermTypes() {
		return termTypes;
	}
	
	public static void loadDeleteReasons() {
		DeleteReasonListServiceAsync deleteReasonListService = GWT.create(DeleteReasonListService.class);
		
		AsyncCallback<List<DeleteReasonInstance>> callback = new AsyncCallback<List<DeleteReasonInstance>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				if (caught instanceof IllegalArgumentException)
					MessageBox.alert("Alert", caught.getMessage(), null);
				else {
					MessageBox.alert("Alert", "Delete reasons load failed unexpectedly.", null);
					System.out.println(caught.getClass().getName());
					System.out.println(caught.getMessage());
				}
			}

			public void onSuccess(List<DeleteReasonInstance> list) {
				deleteReasons.removeAll();
				for (DeleteReasonInstance instance : list) {
					deleteReasons.add(DeleteReasonInstance.obtainModel(instance));	
				}
			}
		};
		
		deleteReasonListService.getDeleteReasons(null, callback);
	}
	
	public static ListStore<BeanModel> getDeleteReasons() {
		return deleteReasons;
	}
	
	public static void loadCancelReasons() {
		CancelReasonListServiceAsync cancelReasonListService = GWT.create(CancelReasonListService.class);
		
		AsyncCallback<List<CancelReasonInstance>> callback = new AsyncCallback<List<CancelReasonInstance>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				if (caught instanceof IllegalArgumentException)
					MessageBox.alert("Alert", caught.getMessage(), null);
				else {
					MessageBox.alert("Alert", "Cancel reasons load failed unexpectedly.", null);
					System.out.println(caught.getClass().getName());
					System.out.println(caught.getMessage());
				}
			}

			public void onSuccess(List<CancelReasonInstance> list) {
				cancelReasons.removeAll();
				for (CancelReasonInstance instance : list) {
					cancelReasons.add(CancelReasonInstance.obtainModel(instance));	
				}
			}
		};
		
		cancelReasonListService.getCancelReasons(null, callback);
	}
	
	public static ListStore<BeanModel> getCancelReasons() {
		return cancelReasons;
	}
	
	public static void loadCommissionTypes() {
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
					commissionTypes.setKeyProvider(new SimpleKeyProvider("commissionCode"));
				for (CommissionTypeInstance instance : list) {
					commissionTypes.add(CommissionTypeInstance.obtainModel(instance));	
				}
			}
		};
		
		commissionTypeListService.getCommissionTypes(null, callback);
	}
	
	public static ListStore<BeanModel> getCommissionTypes() {
		return commissionTypes;
	}
	
	public static void loadProducts() {
		ProductListServiceAsync productListService = GWT.create(ProductListService.class);
		
		AsyncCallback<List<ProductInstance>> callback = new AsyncCallback<List<ProductInstance>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				if (caught instanceof IllegalArgumentException)
					MessageBox.alert("Alert", caught.getMessage(), null);
				else {
					MessageBox.alert("Alert", "Products load failed unexpectedly.", null);
					System.out.println(caught.getClass().getName());
					System.out.println(caught.getMessage());
				}
			}

			public void onSuccess(List<ProductInstance> list) {
				products.removeAll();
				for (ProductInstance instance : list) {
					products.add(ProductInstance.obtainModel(instance));	
				}
			}
		};
		
		productListService.getProducts(null, callback);
	}
	
	public static ListStore<BeanModel> getProducts() {
		return products;
	}
	
	public static ListStore<BeanModel> getCommissionTypes(CommissionTypeTargets target) {
		ListStore<BeanModel> list = new BetterFilterListStore<BeanModel>();
		list.setKeyProvider(new SimpleKeyProvider("commissionTypeCode"));
//		list.setKeyProvider(CommissionTypeInstance.obtainStaticModelKeyProvider());
		
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
		config.setAnchorOffset(0);
		config.setAnchorToTarget(true);
		return config;
	}
}
