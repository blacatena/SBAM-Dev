package com.scholastic.sbam.client.uiobjects.uiapp;

import com.scholastic.sbam.client.uiobjects.foundation.UnknownPortlet;
import com.scholastic.sbam.client.util.IconSupplier;

public enum AppPortletIds {
 
	FULL_INSTITUTION_SEARCH				(
				"Full Institution Search", 		
				IconSupplier.getInstitutionIconName(),		
				"InstitutionSearch"	, 				
				InstitutionSearchPortlet.class.getName()
			),
	CUSTOMER_SEARCH						(
				"Customer Search",
				IconSupplier.getCustomerIconName(),
				"CustomerSearch"	,
				CustomerSearchPortlet.class.getName()
			),
	SITE_INSTITUTION_SEARCH				(
				"Site Institution Search",
				IconSupplier.getSiteIconName(),
				"SiteIntitutionSearch",
				SiteInstitutionSearchPortlet.class.getName()
			),
	AGREEMENT_SEARCH					(
				"Agreement Search",
				IconSupplier.getAgreementIconName(),
				"AgreementSearch",
				AgreementSearchPortlet.class.getName()
			),
	AGREEMENT_DISPLAY					(
				"Agreement Display",
				IconSupplier.getAgreementIconName(),
				"AgreementDisplay"	,
				AgreementPortlet.class.getName()
			),
	AGREEMENT_AUTH_METHOD_SEARCH		(
				"Agreement Auth Method Search",
				IconSupplier.getAccessMethodIconName(),
				"AgreementMethodSearch",
				AgreementAuthMethodSearchPortlet.class.getName()
			),
	AGREEMENT_CONTACT_SEARCH			(
				"Agreement Contact Search",
				IconSupplier.getContactsIconName(),
				"AgreementContactSearch",
				AgreementContactSearchPortlet.class.getName()
			),
	AGREEMENT_LINK_DISPLAY					(
					"Agreement Link Display",
					IconSupplier.getAgreementLinkIconName(),
					"AgreementLinkDisplay"	,
					AgreementLinkPortlet.class.getName()
				),
	AGREEMENT_LINK_SEARCH					(
			"Agreement Link Search",
			IconSupplier.getAgreementLinkIconName(),
			"AgreementLinkSearch"	,
			AgreementLinkSearchPortlet.class.getName()
		),
	AGREEMENT_NOTES_SEARCH				(
				"Agreement Notes Search",
				IconSupplier.getNoteIconName(),
				"AgreementNotesSearch",
				AgreementNotesSearchPortlet.class.getName()
			),
	AGREEMENT_REMOTE_SETUP_URL_SEARCH	(
				"Agreement Remote Setup Search",
				IconSupplier.getRemoteIconName(),
				"AgreementRemoteSetupUrlSearch",
				AgreementRemoteSetupUrlSearchPortlet.class.getName()
			),
	AGREEMENT_SITE_SEARCH				(
				"Agreement Site Search",
				IconSupplier.getSiteIconName(),
				"AgreementSiteSearch",
				AgreementSiteSearchPortlet.class.getName()
			),
	AGREEMENT_TERM_SEARCH				(
				"Agreement Terms Search",
				IconSupplier.getAgreementTermIconName(),
				"AgreementTermSearch",
				AgreementTermSearchPortlet.class.getName()
			),
	SITE_LOCATION_DISPLAY				(
				"Site Location Display",
				IconSupplier.getSiteIconName(),
				"SiteLocationDisplay",
				SiteLocationPortlet.class.getName()
			),
	RECENT_AGREEMENTS_DISPLAY			(
				"Recent Agreements",
				IconSupplier.getAgreementIconName(),
				"RecentAgreements"	,
				RecentAgreementsPortlet.class.getName()
			),
	RECENT_INSTITUTIONS_DISPLAY			(
				"Recent Institutions",
				IconSupplier.getInstitutionIconName(),
				"RecentInstitutions",
				RecentInstitutionsPortlet.class.getName()
			),
	RECENT_CUSTOMERS_DISPLAY			(
				"Recent Customers",
				IconSupplier.getCustomerIconName(),
				"RecentCustomers",
				RecentCustomersPortlet.class.getName()
			),
	RECENT_SITES_DISPLAY				(
				"Recent Sites",
				IconSupplier.getSiteIconName(),
				"RecentSites"	,
				RecentSiteLocationsPortlet.class.getName()
			),
	UNKNOWN_PORTLET						(
				"The Unknown Portlet",
				IconSupplier.getAlertIconName(),
				"UnknownPortlet",
				UnknownPortlet.class.getName()
			);
	
	String name;
	String iconName;
	String helpTextId;
	String className;
	
	AppPortletIds(String name, String iconName, String helpTextId, String className) {
		this.name			= name;
		this.iconName		= iconName;
		this.helpTextId		= helpTextId;
		this.className		= className;
	}

	public String getName() {
		return name;
	}

	public String getIconName() {
		return iconName;
	}

	public String getHelpTextId() {
		return helpTextId;
	}

	public String getClassName() {
		return className;
	};

}
