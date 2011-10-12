package com.scholastic.sbam.client.util;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;
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
import com.scholastic.sbam.client.services.AgreementTypeListService;
import com.scholastic.sbam.client.services.AgreementTypeListServiceAsync;
import com.scholastic.sbam.client.services.CancelReasonListService;
import com.scholastic.sbam.client.services.CancelReasonListServiceAsync;
import com.scholastic.sbam.client.services.CommissionTypeListService;
import com.scholastic.sbam.client.services.CommissionTypeListServiceAsync;
import com.scholastic.sbam.client.services.ContactTypeListService;
import com.scholastic.sbam.client.services.ContactTypeListServiceAsync;
import com.scholastic.sbam.client.services.DeleteReasonListService;
import com.scholastic.sbam.client.services.DeleteReasonListServiceAsync;
import com.scholastic.sbam.client.services.InstitutionCountryListService;
import com.scholastic.sbam.client.services.InstitutionCountryListServiceAsync;
import com.scholastic.sbam.client.services.InstitutionStateListService;
import com.scholastic.sbam.client.services.InstitutionStateListServiceAsync;
import com.scholastic.sbam.client.services.LinkTypeListService;
import com.scholastic.sbam.client.services.LinkTypeListServiceAsync;
import com.scholastic.sbam.client.services.ProductListService;
import com.scholastic.sbam.client.services.ProductListServiceAsync;
import com.scholastic.sbam.client.services.TermTypeListService;
import com.scholastic.sbam.client.services.TermTypeListServiceAsync;
import com.scholastic.sbam.client.stores.BetterFilterListStore;
import com.scholastic.sbam.client.stores.ExtendedStoreSorter;
import com.scholastic.sbam.shared.objects.AgreementTypeInstance;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.ContactTypeInstance;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;
import com.scholastic.sbam.shared.objects.GenericCodeInstance;
import com.scholastic.sbam.shared.objects.InstitutionCountryInstance;
import com.scholastic.sbam.shared.objects.InstitutionStateInstance;
import com.scholastic.sbam.shared.objects.LinkTypeInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.TermTypeInstance;

public class UiConstants {
	public static final int				QUICK_TOOL_TIP_SHOW_DELAY		= 3000;
	public static final int				QUICK_TOOL_TIP_DISMISS_DELAY	= 2000;
	public static final int				LAZY_TOOL_TIP_SHOW_DELAY		= 8000;
	public static final int				LAZY_TOOL_TIP_DISMISS_DELAY		= 2000;
	public static final int				LAZY_TOOL_TIP_HIDE_DELAY		= 200;
	public static final DateTimeFormat	APP_DATE_LONG_FORMAT			= DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
	public static final DateTimeFormat	APP_DATE_TIME_FORMAT			= DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);	//	DateTimeFormat.getFormat("MM/dd/yy");
	public static final DateTimeFormat	APP_DATE_PLUS_TIME_FORMAT		= DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
	public static final CurrencyData	US_DOLLARS						= new DefaultCurrencyData("840", "$");
	public static final NumberFormat	INTEGER_FORMAT					= NumberFormat.getFormat("#");
	public static final NumberFormat	BLANK_WHILE_ZERO				= NumberFormat.getFormat("BWZ");
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

	public static final String []		OTHER_STATE_SORTS = {"countryCode", "description", "stateCode"};
	
	private static BetterFilterListStore<BeanModel>		agreementTypes		= new BetterFilterListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		commissionTypes 	= new BetterFilterListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		contactTypes		= new BetterFilterListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		deleteReasons		= new BetterFilterListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		cancelReasons 		= new BetterFilterListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		institutionCountries= new BetterFilterListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		institutionStates	= new BetterFilterListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		linkTypes			= new BetterFilterListStore<BeanModel>();
	private static BetterFilterListStore<BeanModel>		products 			= new BetterFilterListStore<BeanModel>();
	private static ListStore<BeanModel>					termTypes 			= new ListStore<BeanModel>();
	
	private static ListStore<BeanModel>					uidTypes			= getUidTypes();
	private static ListStore<BeanModel>					authMethodTypes 	= getAuthMethodTypes();
	
	private static Timer								refreshTimer;
	
	private static boolean								agreementTypesLoaded		= false;
	private static boolean								commissionTypesLoaded 		= false;
	private static boolean								contactTypesLoaded			= false;
	private static boolean								deleteReasonsLoaded			= false;
	private static boolean								cancelReasonsLoaded 		= false;
	private static boolean								institutionCountriesLoaded	= false;
	private static boolean								institutionStatesLoaded		= false;
	private static boolean								linkTypesLoaded				= false;
	private static boolean								productsLoaded 				= false;
	private static boolean								termTypesLoaded 			= false;
	
	public static void init() {
		setStoreAttributes();
		refresh();
		setTimer();
	}
	
	public static void setStoreAttributes() {
		agreementTypes.setKeyProvider(new SimpleKeyProvider("agreementTypeCode"));
		commissionTypes.setKeyProvider(new SimpleKeyProvider("commissionCode"));
		contactTypes.setKeyProvider(new SimpleKeyProvider("contactTypeCode"));
		deleteReasons.setKeyProvider(new SimpleKeyProvider("deleteReasonCode"));
		cancelReasons.setKeyProvider(new SimpleKeyProvider("cancelReasonCode"));
		institutionCountries.setKeyProvider(new SimpleKeyProvider("countryCode"));
		institutionStates.setKeyProvider(new SimpleKeyProvider("stateCode"));
		linkTypes.setKeyProvider(new SimpleKeyProvider("linkeTypeCode"));
		products.setKeyProvider(new SimpleKeyProvider("productCode"));
		termTypes.setKeyProvider(new SimpleKeyProvider("termTypeCode"));
		
		institutionStates.setStoreSorter(new ExtendedStoreSorter(OTHER_STATE_SORTS));
	}
	
	public static void refresh() {
		loadAgreementTypes();
		loadCommissionTypes();
		loadContactTypes();
		loadDeleteReasons();
		loadCancelReasons();
		loadInstitutionStates();	//Could be turned off, but causes problems with contact drop down menus
		loadInstitutionCountries();	//Could be turned off, but causes problems with contact drop down menus
		loadLinkTypes();
		loadProducts();
		loadTermTypes();
	}
	
	public static void setLoggedIn() {
		init();
	}
	
	public static void setLoggedOut() {
		cancel();
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
				termTypesLoaded = true;
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
				
				//	This is special... it doesn't exist in the database as an actual "code"
				deleteReasons.add(DeleteReasonInstance.obtainModel(DeleteReasonInstance.getNoneActiveInstance()));
				for (DeleteReasonInstance instance : list) {
					deleteReasons.add(DeleteReasonInstance.obtainModel(instance));	
				}
				deleteReasonsLoaded = true;
			}
		};
		
		deleteReasonListService.getDeleteReasons(null, callback);
	}
	
	public static ListStore<BeanModel> getDeleteReasons() {
		return deleteReasons;
	}
	
	public static void loadAgreementTypes() {
		AgreementTypeListServiceAsync agreementTypeListService = GWT.create(AgreementTypeListService.class);
		
		AsyncCallback<List<AgreementTypeInstance>> callback = new AsyncCallback<List<AgreementTypeInstance>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				if (caught instanceof IllegalArgumentException)
					MessageBox.alert("Alert", caught.getMessage(), null);
				else {
					MessageBox.alert("Alert", "AgreementTypes load failed unexpectedly.", null);
					System.out.println(caught.getClass().getName());
					System.out.println(caught.getMessage());
				}
			}

			public void onSuccess(List<AgreementTypeInstance> list) {
				agreementTypes.removeAll();
				for (AgreementTypeInstance instance : list) {
					agreementTypes.add(AgreementTypeInstance.obtainModel(instance));	
				}
				agreementTypesLoaded = true;
			}
		};
		
		agreementTypeListService.getAgreementTypes(null, callback);
	}
	
	public static ListStore<BeanModel> getAgreementTypes() {
		return agreementTypes;
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

				//	This is special... it doesn't exist in the database as an actual "code"
				cancelReasons.add(CancelReasonInstance.obtainModel(CancelReasonInstance.getNoneActiveInstance()));
				for (CancelReasonInstance instance : list) {
					cancelReasons.add(CancelReasonInstance.obtainModel(instance));	
				}
				cancelReasonsLoaded = true;
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
				commissionTypesLoaded = true;
			}
		};
		
		commissionTypeListService.getCommissionTypes(null, callback);
	}
	
	public static ListStore<BeanModel> getCommissionTypes() {
		return commissionTypes;
	}
	
	public static void loadContactTypes() {
		ContactTypeListServiceAsync contactTypeListService = GWT.create(ContactTypeListService.class);
		
		AsyncCallback<List<ContactTypeInstance>> callback = new AsyncCallback<List<ContactTypeInstance>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				if (caught instanceof IllegalArgumentException)
					MessageBox.alert("Alert", caught.getMessage(), null);
				else {
					MessageBox.alert("Alert", "Contact types load failed unexpectedly.", null);
					System.out.println(caught.getClass().getName());
					System.out.println(caught.getMessage());
				}
			}

			public void onSuccess(List<ContactTypeInstance> list) {
				contactTypes.removeAll();
				if (contactTypes.getKeyProvider() == null)
					contactTypes.setKeyProvider(new SimpleKeyProvider("contactTypeCode"));
				for (ContactTypeInstance instance : list) {
					contactTypes.add(ContactTypeInstance.obtainModel(instance));	
				}
				contactTypesLoaded = true;
			}
		};
		
		contactTypeListService.getContactTypes(null, callback);
	}
	
	public static ListStore<BeanModel> getContactTypes() {
		return contactTypes;
	}
	
	public static void loadInstitutionCountries() {
		InstitutionCountryListServiceAsync institutionCountryListService = GWT.create(InstitutionCountryListService.class);
		
		AsyncCallback<List<InstitutionCountryInstance>> callback = new AsyncCallback<List<InstitutionCountryInstance>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				if (caught instanceof IllegalArgumentException)
					MessageBox.alert("Alert", caught.getMessage(), null);
				else {
					MessageBox.alert("Alert", "Link types load failed unexpectedly.", null);
					System.out.println(caught.getClass().getName());
					System.out.println(caught.getMessage());
				}
			}

			public void onSuccess(List<InstitutionCountryInstance> list) {
				institutionCountries.removeAll();
				if (institutionCountries.getKeyProvider() == null)
					institutionCountries.setKeyProvider(new SimpleKeyProvider("countryCode"));

				institutionCountries.add(InstitutionCountryInstance.obtainModel(InstitutionCountryInstance.getNoneInstance()));
				for (InstitutionCountryInstance instance : list) {
					institutionCountries.add(InstitutionCountryInstance.obtainModel(instance));	
				}
				institutionCountries.sort("description", SortDir.ASC);
				institutionCountriesLoaded = true;
			}
		};
		
		institutionCountryListService.getInstitutionCountries(null, callback);
	}
	
	public static ListStore<BeanModel> getInstitutionCountries() {
		return institutionCountries;
	}
	
	public static void loadInstitutionStates() {
		InstitutionStateListServiceAsync institutionStateListService = GWT.create(InstitutionStateListService.class);
		
		AsyncCallback<List<InstitutionStateInstance>> callback = new AsyncCallback<List<InstitutionStateInstance>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				if (caught instanceof IllegalArgumentException)
					MessageBox.alert("Alert", caught.getMessage(), null);
				else {
					MessageBox.alert("Alert", "Link types load failed unexpectedly.", null);
					System.out.println(caught.getClass().getName());
					System.out.println(caught.getMessage());
				}
			}

			public void onSuccess(List<InstitutionStateInstance> list) {
				institutionStates.removeAll();
				if (institutionStates.getKeyProvider() == null)
					institutionStates.setKeyProvider(new SimpleKeyProvider("stateCode"));
				
				institutionStates.add(InstitutionStateInstance.obtainModel(InstitutionStateInstance.getNoneInstance()));
				for (InstitutionStateInstance instance : list) {
					institutionStates.add(InstitutionStateInstance.obtainModel(instance));	
				}
				institutionStates.sort("countryCode", SortDir.ASC);
				institutionStatesLoaded = true;
			}
		};
		
		institutionStateListService.getInstitutionStates(null, callback);
	}
	
	public static ListStore<BeanModel> getInstitutionStates() {
		if (!institutionCountriesLoaded)
			loadInstitutionCountries();
		return institutionStates;
	}
	
	public static void loadLinkTypes() {
		LinkTypeListServiceAsync linkTypeListService = GWT.create(LinkTypeListService.class);
		
		AsyncCallback<List<LinkTypeInstance>> callback = new AsyncCallback<List<LinkTypeInstance>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				if (caught instanceof IllegalArgumentException)
					MessageBox.alert("Alert", caught.getMessage(), null);
				else {
					MessageBox.alert("Alert", "Link types load failed unexpectedly.", null);
					System.out.println(caught.getClass().getName());
					System.out.println(caught.getMessage());
				}
			}

			public void onSuccess(List<LinkTypeInstance> list) {
				linkTypes.removeAll();
				if (linkTypes.getKeyProvider() == null)
					linkTypes.setKeyProvider(new SimpleKeyProvider("linkTypeCode"));
				for (LinkTypeInstance instance : list) {
					linkTypes.add(LinkTypeInstance.obtainModel(instance));	
				}
				linkTypesLoaded = true;
			}
		};
		
		linkTypeListService.getLinkTypes(null, callback);
	}
	
	/**
	 * States are not always kept in memory, because they're so infrequently needed.  This method lets an application clear them out.
	 */
	public static void flushInstitutionStates() {
		institutionStates.removeAll();
		institutionStatesLoaded = false;
	}
	
	/**
	 * Countries are not always kept in memory, because they're so infrequently needed.  This method lets an application clear them out.
	 */
	public static void flushInstitutionCountries() {
		institutionCountries.removeAll();
		institutionCountriesLoaded = false;
	}
	
	public static ListStore<BeanModel> getLinkTypes() {
		return linkTypes;
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
				productsLoaded = true;
			}
		};
		
		productListService.getProducts(null, callback);
	}
	
	public static ListStore<BeanModel> getProducts() {
		return products;
	}
	
	public static ListStore<BeanModel> getCommissionTypes(CommissionTypeTargets target) {
		if (target == null)
			return commissionTypes;
		
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
	
	public static ListStore<BeanModel> getUidTypes() {
		if (uidTypes != null)
			return uidTypes;
		ListStore<BeanModel> list = new ListStore<BeanModel>();
		list.setKeyProvider(new SimpleKeyProvider("code"));
		for (AuthMethodInstance.UserTypes type : AuthMethodInstance.UserTypes.values()) {
			GenericCodeInstance instance = new GenericCodeInstance(type.getCode(), type.getName());
			list.add(GenericCodeInstance.obtainModel(instance));
		}
		return list;
	}
	
	public static ListStore<BeanModel> getAuthMethodTypes() {
		if (authMethodTypes != null)
			return authMethodTypes;
		ListStore<BeanModel> list = new ListStore<BeanModel>();
		list.setKeyProvider(new SimpleKeyProvider("code"));
		
		GenericCodeInstance instance;
		
		instance = new GenericCodeInstance(AuthMethodInstance.AM_IP, "IP");
		list.add(GenericCodeInstance.obtainModel(instance));
		
		instance = new GenericCodeInstance(AuthMethodInstance.AM_UID, "UID");
		list.add(GenericCodeInstance.obtainModel(instance));
		
		instance = new GenericCodeInstance(AuthMethodInstance.AM_URL, "URL");
		list.add(GenericCodeInstance.obtainModel(instance));
		
		return list;
	}
	
	public static ListStore<BeanModel> getGenericCodeStore(String [] [] values) {
		ListStore<BeanModel> list = new ListStore<BeanModel>();
		list.setKeyProvider(new SimpleKeyProvider("code"));
		
		GenericCodeInstance instance;
		
		for (int i = 0; i < values.length; i++) {
			instance = new GenericCodeInstance(values [i] [0], values [i] [1]);
			list.add(GenericCodeInstance.obtainModel(instance));
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

	public static ToolTipConfig getLazyTip(String toolTip) {
		ToolTipConfig config = new ToolTipConfig();
		config.setText(toolTip);
		config.setShowDelay(LAZY_TOOL_TIP_SHOW_DELAY);
		config.setDismissDelay(LAZY_TOOL_TIP_DISMISS_DELAY);
		config.setHideDelay(LAZY_TOOL_TIP_HIDE_DELAY);
		config.setAnchor("center");
		config.setAnchorOffset(0);
		config.setAnchorToTarget(true);
		return config;
	}
	
	public static String formatDateLong(Date date) {
		return APP_DATE_LONG_FORMAT.format(date);
	}
	
	public static String formatDate(Date date) {
		return APP_DATE_TIME_FORMAT.format(date);
	}
	
	public static native String getUserAgent() /*-{
	return navigator.userAgent.toLowerCase();
	}-*/;
	
	public static boolean isInternetExplorer() {
		return getUserAgent().contains("msie");
	}

	public static boolean areAgreementTypesLoaded() {
		return agreementTypesLoaded;
	}

	public static boolean areCommissionTypesLoaded() {
		return commissionTypesLoaded;
	}

	public static boolean areContactTypesLoaded() {
		return contactTypesLoaded;
	}

	public static boolean areDeleteReasonsLoaded() {
		return deleteReasonsLoaded;
	}

	public static boolean areCancelReasonsLoaded() {
		return cancelReasonsLoaded;
	}

	public static boolean areInstitutionCountriesLoaded() {
		return institutionCountriesLoaded;
	}

	public static boolean areInstitutionStatesLoaded() {
		return institutionStatesLoaded;
	}

	public static boolean areLinkTypesLoaded() {
		return linkTypesLoaded;
	}

	public static boolean areProductsLoaded() {
		return productsLoaded;
	}

	public static boolean areTermTypesLoaded() {
		return termTypesLoaded;
	}
	
}
