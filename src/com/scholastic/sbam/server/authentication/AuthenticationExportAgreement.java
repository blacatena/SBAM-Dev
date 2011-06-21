package com.scholastic.sbam.server.authentication;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AeAuthUnit;
import com.scholastic.sbam.server.database.codegen.AeControl;
import com.scholastic.sbam.server.database.codegen.AePref;
import com.scholastic.sbam.server.database.codegen.AePrefId;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.ProductService;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrl;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.codegen.SiteId;
import com.scholastic.sbam.server.database.objects.DbAeAuthUnit;
import com.scholastic.sbam.server.database.objects.DbAePref;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbProductService;
import com.scholastic.sbam.server.database.objects.DbRemoteSetupUrl;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.exceptions.AuthenticationExportException;
import com.scholastic.sbam.shared.objects.ExportProcessReport;
import com.scholastic.sbam.shared.util.AppConstants;

public class AuthenticationExportAgreement {
	
	public		static String		YES_FOR_ALL_PRODUCTS		=	"y";
	
	protected ExportProcessReport				exportReport;
	protected ExportController					controller;
	
	protected Agreement							agreement;
	protected HashMap<String, ProductService>	productServices = new HashMap<String, ProductService>();
	
	public AuthenticationExportAgreement(int agreementId, ExportController controller, ExportProcessReport exportProcessReport) {
		this.exportReport	= exportProcessReport;
		this.controller		=	controller;
		controller.consoleOutput("Agreement " + agreementId);
		loadAgreement(agreementId);
	}
	
	public void loadAgreement(int agreementId) {
		agreement = DbAgreement.getById(agreementId);
		if (agreement == null)
			throw new AuthenticationExportException("Agreement not found for ID " + agreementId);
	}
	
	public void exportSites() throws AuthenticationExportException {
		if (agreement == null)
			throw new AuthenticationExportException("No agreement loaded.");
		
		loadProductServices();
		
		//	If no active services, ignore this
		if (productServices.size() == 0)
			return;
		
		exportReport.countAgreement();
		
		//	Authenticate site related methods
		
		for (AgreementSite agreementSite : DbAgreementSite.findByAgreementId(agreement.getId(), AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED)) {
			if (agreementSite.getId().getSiteLocCode().length() == 0) {
				
				//	All sites
				for (Site site : DbSite.findByUcn(agreementSite.getId().getSiteUcn(), agreementSite.getId().getSiteUcnSuffix(), AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED)) {
					Institution institution = getInstitution(site);
					AeAuthUnit authUnit = getAuthUnit(site, institution);
					new AuthenticationExportSite(agreement, authUnit, site, institution, controller, exportReport).exportSite();
				}
				
			} else {
				
				//	Specific site
				Site site = DbSite.getById(agreementSite.getId().getSiteUcn(), agreementSite.getId().getSiteUcnSuffix(), agreementSite.getId().getSiteLocCode());
				if (site == null) {
					throw new AuthenticationExportException("Site " +
															agreementSite.getId().getSiteUcn() + "-" +
															agreementSite.getId().getSiteUcnSuffix() + ":" +
															agreementSite.getId().getSiteLocCode() + 
															" not found for agreement " + agreementSite.getId().getAgreementId() + ".");
				}
				
				Institution institution = getInstitution(site);
				AeAuthUnit authUnit = getAuthUnit(site, institution);
				new AuthenticationExportSite(agreement, authUnit, site, institution, controller, exportReport).exportSite();
			}
		}
		
		//	Authenticate agreement related methods not associated with a site
		
		Site		site		=	null;
		Institution institution	=	null;
		AeAuthUnit authUnit		= null;
		
		for (AuthMethod authMethod : DbAuthMethod.findBySite(agreement.getId(), 0, 0, "", null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED)) {
			if (site == null) {
				site = getAgreementSite();
				institution = getInstitution(site);
				authUnit = getAuthUnit(site, institution);
			}
			new AuthenticationExportMethod(agreement, authUnit, site, institution, authMethod, controller, exportReport).exportMethod();
		}
		
		//	Authenticate agreement related remote setup urls not associated with a site
		
		for (RemoteSetupUrl remoteSetupUrl : DbRemoteSetupUrl.findBySite(agreement.getId(), 0, 0, "", null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED)) {
			if (site == null) {
				site = getAgreementSite();
				institution = getInstitution(site);
				authUnit = getAuthUnit(site, institution);
			}
			new AuthenticationExportRemoteSetupUrl(agreement, authUnit, site, institution, remoteSetupUrl, controller, exportReport).exportRemoteSetupUrl();
		}
	}
	
	/**
	 * Determine the product services currently active for this agreement
	 */
	protected void loadProductServices() {
		List<Object []> terms = DbAgreementTerm.findActive(agreement.getId(), controller.getAeControl().getAsOfDate());
		
		for (Object [] term : terms) {
			Product product = (Product) term [2];
			List<ProductService> productServiceList = DbProductService.findByProduct(product.getProductCode(), AppConstants.STATUS_DELETED);
			for (ProductService productService : productServiceList) {
				productServices.put(productService.getId().getServiceCode(), productService);
			}
		}
	}
	
	/**
	 * Load the default site for any methods not associated with a specific site
	 */
	protected Site getAgreementSite() {
		List<AgreementSite> sites = DbAgreementSite.findByAgreementId(agreement.getId(), AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED);
		
		Site site;
		int ucn;
		int ucnSuffix;
		String siteLocCode;
		if (sites.size() == 0 || sites.size() > 1) {
			//	Create a site entry to use, because we can't actually find one single site to clue in on
			
			ucn = agreement.getBillUcn();
			ucnSuffix = agreement.getBillUcnSuffix();
			siteLocCode = "";
			
			site = new Site();
			SiteId siteId = new SiteId();
			
			siteId.setUcn(ucn);
			siteId.setUcnSuffix(ucnSuffix);
			siteId.setSiteLocCode(siteLocCode);
			
			site.setId(siteId);
			site.setStatus(AppConstants.STATUS_ACTIVE);
			site.setPseudoSite(AppConstants.ANSWER_NO);
			site.setDescription("Temporary/do not persist: Created as agreement default for agreement " + agreement.getId());
			
			exportReport.noDefaultSiteAgreementCount();
			
			// The site will not be persisted
		} else {
			ucn = sites.get(0).getId().getSiteUcn();
			ucnSuffix = sites.get(0).getId().getSiteUcnSuffix();
			siteLocCode = sites.get(0).getId().getSiteLocCode();
			
			site = DbSite.getById(ucn, ucnSuffix, siteLocCode);
			if (site == null) {
				site = new Site();
				
				SiteId siteId = new SiteId();
				siteId.setUcn(ucn);
				siteId.setUcnSuffix(ucnSuffix);
				siteId.setSiteLocCode(siteLocCode);
				
				site.setId(siteId);
				site.setStatus(AppConstants.STATUS_ACTIVE);
				site.setPseudoSite(AppConstants.ANSWER_NO);
				site.setDescription("Temporary/do not persist: Created as agreement default for agreement " + agreement.getId());
//				throw new AuthenticationExportException("Default site " + ucn + "-" + ucnSuffix + ":" + siteLocCode + " not found for agreement " + agreement.getId() + ".");
			}
			exportReport.singleSiteAgreementCount();
		}
		
		return site;
	}
	
	protected Institution getInstitution(Site site) {
		return getInstitution(site.getId().getUcn());
	}
	
	protected Institution getInstitution(int ucn) {
		Institution institution = DbInstitution.getByCode(ucn);
		if (institution == null)
			throw new AuthenticationExportException("Missing institution for site UCN " + ucn + " for agreement " + agreement.getId() + ".");
		return institution;
	}
	
	/**
	 * Get the authentication unit to use with this agreement.
	 * 
	 * Preference is given first to any AU with the right products, already being used this run.
	 * 
	 * Next preference is to the AU with the same products on the previous run.
	 * 
	 * Next preference is to the AU not yet being used with the closest product match.
	 * 
	 * Next preference is for any AU that already exists but hasn't yet been used.
	 * 
	 * Last option is to create a brand new AU.
	 * 
	 * @param site
	 * @param institution
	 * @return
	 */
	protected AeAuthUnit getAuthUnit(Site site, Institution institution) {
		
//		System.out.println("Products " +  productServices);
		
		//	Look for current match, i.e. an AU already in use with exactly the products needed
		List<AeAuthUnit> authUnits = DbAeAuthUnit.findBySite(
				site.getId().getUcn(), site.getId().getUcnSuffix(), site.getId().getSiteLocCode(),
				agreement.getBillUcn(), agreement.getBillUcnSuffix(), 
				institution.getParentUcn(), 1	/* TODO Handle legacy parent problems */
				);
		
		//	Cycle through the auth units, looking for a product match (i.e. one already being used for this export for these products)
		for (AeAuthUnit authUnit : authUnits)
			if (correctAuthUnit(authUnit)) {
				exportReport.auCountExistingAgreement();
				return authUnit;
			}
		
		exportReport.countAuthUnit();
		
		//	Look for last execution exact match not already being using for other products
		AeControl lastCompleteAeControl = controller.getLastCompleteAeControl();
		if (lastCompleteAeControl != null) {
			for (AeAuthUnit authUnit : authUnits) {
				if (unusedAuthUnit(authUnit) && correctAuthUnit(authUnit, lastCompleteAeControl)) {
//					controller.forceConsoleOutput("Use match from previous execution " + authUnit.getAuId() + " for agreement " + agreement.getId());
					exportReport.auCountReuseSameProduct();
					loadProducts(authUnit);
					return authUnit;
				}
			}
			
			//	Look for last execution, closest match not already being used for other products
			int matchDiffs = -1;
			int matches    = 0;
			AeAuthUnit chooseAuthUnit = null;
			for (AeAuthUnit authUnit : authUnits) {
				if (unusedAuthUnit(authUnit)) {
					int [] newMatchDiffs = countAuthUnitDiffs(authUnit, lastCompleteAeControl);
					if (newMatchDiffs [0] > 0) { // Must have at least one product match
						if (chooseAuthUnit == null || newMatchDiffs [1] < matchDiffs || (newMatchDiffs [1] == matchDiffs && newMatchDiffs [0] > matches)) {
							matches    = newMatchDiffs [0];
							matchDiffs = newMatchDiffs [1];
							chooseAuthUnit = authUnit;
						}
					}
				}
			}
			if (chooseAuthUnit != null) {
//				controller.forceConsoleOutput("Use close match from previous execution " + chooseAuthUnit.getAuId() + " for agreement " + agreement.getId());
				exportReport.auCountReuseSimilarProducts();
				loadProducts(chooseAuthUnit);
				return chooseAuthUnit;
			}
			
			//	Look for any unused last time and this time
			for (AeAuthUnit authUnit : authUnits) {
				if (unusedAuthUnit(authUnit) && unusedAuthUnit(authUnit, lastCompleteAeControl)) {
//					controller.forceConsoleOutput("Use unused from previous execution " + authUnit.getAuId() + " for agreement " + agreement.getId());
					exportReport.auCountReusePrevUnusedAuCount();
					loadProducts(authUnit);
					return authUnit;
				}
			}
		}
		
		//	Look for any unused this time, but used last time
		for (AeAuthUnit authUnit : authUnits) {
			if (unusedAuthUnit(authUnit)) {
//				controller.forceConsoleOutput("Reuse non-match from previous execution " + authUnit.getAuId() + " for agreement " + agreement.getId());
				exportReport.auCountReusePrevRandom();
				loadProducts(authUnit);
				return authUnit;
			}
		}
		
		//	Create new
		AeAuthUnit authUnit = new AeAuthUnit();
		
		authUnit.setSiteUcn(site.getId().getUcn());
		authUnit.setSiteUcnSuffix(site.getId().getUcnSuffix());
		authUnit.setSiteLocCode(site.getId().getSiteLocCode());
		
		authUnit.setBillUcn(agreement.getBillUcn());
		authUnit.setBillUcnSuffix(agreement.getBillUcnSuffix());
		
		int parentUcn = institution.getParentUcn();
		int parentUcnSuffix = 1;
		
		if (parentUcn == 0) {
			parentUcn = site.getId().getUcn();
			parentUcnSuffix = site.getId().getUcnSuffix();
		}
		
		authUnit.setSiteParentUcn(parentUcn);
		authUnit.setSiteParentUcnSuffix(parentUcnSuffix);
		
		authUnit.setCreatedDatetime(new Date());
		
		DbAeAuthUnit.persist(authUnit);
		
		exportReport.auCountCreatedThisExport();
		
		loadProducts(authUnit);
		
//		controller.forceConsoleOutput("Au created " + authUnit.getAuId() + " for agreement " + agreement.getId());
		
		return authUnit;
	}
	
	/**
	 * This auth unit is correct to use for a site if it exactly matches the product list needed for this agreement on the current export.
	 * 
	 * It's assumed that the auth unit already matches the institutions for this agreement and site.
	 * 
	 * @param authUnit
	 * @return
	 */
	protected boolean correctAuthUnit(AeAuthUnit authUnit) {
		return correctAuthUnit(authUnit, controller.getAeControl());
	}
	
	/**
	 * This auth unit is correct to use for a site if it exactly matches the product list needed for this agreement on an export.
	 * 
	 * It's assumed that the auth unit already matches the institutions for this agreement and site.
	 * 
	 * @param authUnit
	 * @param aeControl
	 * @return
	 */
	protected boolean correctAuthUnit(AeAuthUnit authUnit, AeControl aeControl) {
		int matches = 0;
		
		List<AePref> aePrefs = DbAePref.findProductServices(aeControl.getAeId(), authUnit.getAuId());
		
		for (AePref aePref : aePrefs) {
//			System.out.println("Correct? " + aePref.getId().getAeId() + " : " + aePref.getId().getAuId() + " : " + aePref.getId().getPrefCode());
			if (productServices.containsKey(aePref.getId().getPrefCode()))
				matches++;
			else
				return false;
//			System.out.println("Matches = " + matches);
		}
		
//		if (matches > 0) System.out.println("Final " + matches + " vs " + productServices.size());
		
		return (matches == productServices.size());

	}
	
	protected boolean unusedAuthUnit(AeAuthUnit authUnit) {
		return unusedAuthUnit(authUnit, controller.getAeControl());
	}
	
	protected boolean unusedAuthUnit(AeAuthUnit authUnit, AeControl aeControl) {

		List<AePref> aePrefs = DbAePref.findProductServices(aeControl.getAeId(), authUnit.getAuId());
		
		return (aePrefs.size() == 0);

	}

	protected int [] countAuthUnitDiffs(AeAuthUnit authUnit, AeControl aeControl) {
		int matches = 0;
		int missing = 0;
		
		List<AePref> aePrefs = DbAePref.findProductServices(aeControl.getAeId(), authUnit.getAuId());
		
		for (AePref aePref : aePrefs) {
			
		//	System.out.println("Diff match " + aePref.getId().getAeId() + " : " + aePref.getId().getAuId() + " : " + aePref.getId().getPrefCode());
			if (productServices.containsKey(aePref.getId().getPrefCode()))
				matches++;	//  Found
			else
				missing++;	//  Not found
		//	System.out.println("matches " + matches + " missing " + missing);
			
		}
		
		//	This is 
		return new int [] { matches, (productServices.size() - matches) + missing};
	}
	
	protected void loadProducts(AeAuthUnit authUnit) {
		AePref aePref;
		
		for (ProductService productService : productServices.values()) {
			
			aePref = new AePref();
			
			AePrefId aePrefId = new AePrefId();
			aePrefId.setAeId(controller.getAeControl().getAeId());
			aePrefId.setAuId(authUnit.getAuId());
			aePrefId.setPrefCode(productService.getId().getServiceCode());
			
			aePref.setId(aePrefId);
			aePref.setPrefValue(YES_FOR_ALL_PRODUCTS);
			
			HibernateAccessor.persist(aePref);
			
		}
	}
}
