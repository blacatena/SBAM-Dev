package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.scholastic.sbam.client.uiobjects.fields.BoundDateField;
import com.scholastic.sbam.client.uiobjects.fields.DateDefaultBinder;
import com.scholastic.sbam.client.uiobjects.fields.DateRangeBinder;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedCheckBoxGroup;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.reporting.SnapshotParameterNames;
import com.scholastic.sbam.shared.util.AppConstants;

public class TermCriteriaCard extends SnapshotCriteriaCardBase implements AppSleeper {

	protected RadioGroup			ucnTypeGroup			=	new RadioGroup();
	protected RadioGroup			productServiceGroup		=	new RadioGroup();
	
	protected CheckBoxGroup			termTypeCheckGroup		=	new EnhancedCheckBoxGroup();
	protected CheckBoxGroup			prodCommCheckGroup		=	new EnhancedCheckBoxGroup();
	protected CheckBoxGroup			agreementCommCheckGroup	=	new EnhancedCheckBoxGroup();
	protected CheckBoxGroup			termCommCheckGroup		=	new EnhancedCheckBoxGroup();
	
	protected BoundDateField		startFromDate			=	getDateField();
	protected BoundDateField		startToDate				=	getDateField();

	protected BoundDateField		endFromDate				=	getDateField();
	protected BoundDateField		endToDate				=	getDateField();

	protected BoundDateField		terminateFromDate		=	getDateField();
	protected BoundDateField		terminateToDate			=	getDateField();

	protected DateRangeBinder		startRangeBinder		= new DateRangeBinder();
	protected DateRangeBinder		endRangeBinder			= new DateRangeBinder();
	protected DateRangeBinder		terminateRangeBinder	= new DateRangeBinder();
	protected DateDefaultBinder		terminateDefaultBinder	= new DateDefaultBinder(60);
	
	public TermCriteriaCard() {
		super();
	}

	@Override
	public String getPanelTitle() {
		return "Snapshot Terms Selector";
	}
	
	@Override
	public String getPanelToolTip() {
		return "Use this panel to specify term criteria for the snapshot.";
	}

	@Override
	public void populateFields() {
		ucnTypeGroup.setName(SnapshotParameterNames.UCN_TYPE);
		
		Radio byBillUcn = new Radio();
		byBillUcn.setName("byBillUcn");
		byBillUcn.setBoxLabel("by Bill To UCN");
		byBillUcn.setValueAttribute("b");
		ucnTypeGroup.add(byBillUcn);

		Radio bySiteUcn = new Radio();
		bySiteUcn.setName("bySiteUcn");
		bySiteUcn.setBoxLabel("by Site UCN");
		bySiteUcn.setValueAttribute("s");
		ucnTypeGroup.add(bySiteUcn);
		
		
		productServiceGroup.setName(SnapshotParameterNames.PRODUCT_SERVICE_TYPE);
		
		Radio byProduct = new Radio();
		byProduct.setName("byProduct");
		byProduct.setBoxLabel("by Product");
		byProduct.setValueAttribute("p");
		productServiceGroup.add(byProduct);

		Radio byService = new Radio();
		byService.setName("byService");
		byService.setBoxLabel("by Service");
		byService.setValueAttribute("s");
		productServiceGroup.add(byService);
		
		termTypeCheckGroup.setName(SnapshotParameterNames.TERM_TYPES);
		for (BeanModel termTypeBean : UiConstants.getTermTypes().getModels()) {
			TermTypeInstance termType = termTypeBean.getBean();
			if (termType.getStatus() == AppConstants.STATUS_ACTIVE) {
				CheckBox checkBox = new CheckBox();
				checkBox.setName(termType.getTermTypeCode());
				checkBox.setBoxLabel(termType.getDescription());
				checkBox.setValueAttribute(termType.getTermTypeCode());
				termTypeCheckGroup.add(checkBox);
			}
		}
		
		prodCommCheckGroup.setName(SnapshotParameterNames.PROD_COMM_CODES);
		for (BeanModel commTypeBean : UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.PRODUCT).getModels()) {
			CommissionTypeInstance commType = commTypeBean.getBean();
			if (commType.getStatus() == AppConstants.STATUS_ACTIVE) {
				CheckBox checkBox = new CheckBox();
				checkBox.setName(commType.getCommissionCode());
				checkBox.setBoxLabel(commType.getDescription());
				checkBox.setValueAttribute(commType.getCommissionCode());
				prodCommCheckGroup.add(checkBox);
			}
		}
		
		agreementCommCheckGroup.setName(SnapshotParameterNames.AGREEMENT_COMM_CODES);
		for (BeanModel commTypeBean : UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.AGREEMENT).getModels()) {
			CommissionTypeInstance commType = commTypeBean.getBean();
			if (commType.getStatus() == AppConstants.STATUS_ACTIVE) {
				CheckBox checkBox = new CheckBox();
				checkBox.setName(commType.getCommissionCode());
				checkBox.setBoxLabel(commType.getDescription());
				checkBox.setValueAttribute(commType.getCommissionCode());
				agreementCommCheckGroup.add(checkBox);
			}
		}
		
		termCommCheckGroup.setName(SnapshotParameterNames.PROD_COMM_CODES);
		for (BeanModel commTypeBean : UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.AGREEMENT_TERM).getModels()) {
			CommissionTypeInstance commType = commTypeBean.getBean();
			if (commType.getStatus() == AppConstants.STATUS_ACTIVE) {
				CheckBox checkBox = new CheckBox();
				checkBox.setName(commType.getCommissionCode());
				checkBox.setBoxLabel(commType.getDescription());
				checkBox.setValueAttribute(commType.getCommissionCode());
				termCommCheckGroup.add(checkBox);
			}
		}
	}

	@Override
	public String getPanelIconName() {
		return IconSupplier.getTermTypeIconName();
	}

	@Override
	protected void addCriteriaFields() {
		//  Start / end / terminate range binding
		
		startFromDate.bindLow(startRangeBinder);
		startToDate.bindHigh(startRangeBinder);
		
		endFromDate.bindLow(endRangeBinder);
		endToDate.bindHigh(endRangeBinder);
		
		terminateFromDate.bindLow(terminateRangeBinder);
		terminateToDate.bindHigh(terminateRangeBinder);
		
		/* Selection Control */
		
		addDividerRow();
		addSingleField("Customer Selection:", ucnTypeGroup);
		addSingleField("Product Selection:", productServiceGroup);
		
		/* Dates */
		
		addDividerRow();
		addDateRange("Start Date:", 	startFromDate,		startToDate);
		addDateRange("End Date:",		endFromDate,		endToDate);
		addDateRange("Terminate Date:",	terminateFromDate,	terminateToDate);

		/* Term Types */

		addDividerRow();
		addSingleField("Term Types:", termTypeCheckGroup);

		/* Product Commission Types */

		addDividerRow();
		addSingleField("Product Commission Codes:", prodCommCheckGroup);

		/* Agreeement Commission Types */

		addDividerRow();
		addSingleField("Agreement Commission Codes:", agreementCommCheckGroup);

		/* Agreement Term Commission Types */

		addDividerRow();
		addSingleField("Term Commission Codes:", termCommCheckGroup);
	}

	@Override
	protected void setFields(SnapshotParameterSetInstance snapshotParameterSet) {
		setDateFieldRange(snapshotParameterSet, SnapshotParameterNames.START_DATE,		startFromDate,		startToDate);
		setDateFieldRange(snapshotParameterSet, SnapshotParameterNames.END_DATE,		endFromDate,		endToDate);
		setDateFieldRange(snapshotParameterSet, SnapshotParameterNames.TERMINATE_DATE, terminateFromDate,	terminateToDate);
		
		setCheckBoxes(snapshotParameterSet, SnapshotParameterNames.TERM_TYPES,		 		termTypeCheckGroup);
		setCheckBoxes(snapshotParameterSet, SnapshotParameterNames.PROD_COMM_CODES, 		prodCommCheckGroup);
		setCheckBoxes(snapshotParameterSet, SnapshotParameterNames.AGREEMENT_COMM_CODES,	agreementCommCheckGroup);
		setCheckBoxes(snapshotParameterSet, SnapshotParameterNames.TERM_COMM_CODES, 		termCommCheckGroup);
		
		setCheckBoxes(snapshot.getUcnType(), SnapshotParameterNames.UCN_TYPE, ucnTypeGroup);
		setCheckBoxes(snapshot.getProductServiceType(), SnapshotParameterNames.PRODUCT_SERVICE_TYPE, productServiceGroup);
	};

	@Override
	public void addParametersFromFields(SnapshotParameterSetInstance snapshotParameterSet) {
		snapshotParameterSet.addValue(SnapshotParameterNames.START_DATE,		SnapshotParameterNames.DATES_GROUP, startFromDate.getValue(),		startToDate.getValue());
		snapshotParameterSet.addValue(SnapshotParameterNames.END_DATE,			SnapshotParameterNames.DATES_GROUP, endFromDate.getValue(),		endToDate.getValue());
		snapshotParameterSet.addValue(SnapshotParameterNames.TERMINATE_DATE,	SnapshotParameterNames.DATES_GROUP, terminateFromDate.getValue(),	terminateToDate.getValue());
		
		addParametersFrom(termTypeCheckGroup, snapshotParameterSet, SnapshotParameterNames.TERM_TYPES, SnapshotParameterNames.TERM_TYPES);
		addParametersFrom(prodCommCheckGroup, snapshotParameterSet, SnapshotParameterNames.PROD_COMM_CODES, SnapshotParameterNames.PROD_COMM_CODES);
		addParametersFrom(agreementCommCheckGroup, snapshotParameterSet, SnapshotParameterNames.AGREEMENT_COMM_CODES, SnapshotParameterNames.AGREEMENT_COMM_CODES);
		addParametersFrom(termCommCheckGroup, snapshotParameterSet, SnapshotParameterNames.TERM_COMM_CODES, SnapshotParameterNames.TERM_COMM_CODES);

		addParametersFrom(ucnTypeGroup, snapshotParameterSet, SnapshotParameterNames.UCN_TYPE, SnapshotParameterNames.UCN_TYPE);
		addParametersFrom(productServiceGroup, snapshotParameterSet, SnapshotParameterNames.PRODUCT_SERVICE_TYPE, SnapshotParameterNames.PRODUCT_SERVICE_TYPE);
	}
	
	@Override
	public void setSnapshot(SnapshotInstance snapshot) {
		super.setSnapshot(snapshot);
	}
}
