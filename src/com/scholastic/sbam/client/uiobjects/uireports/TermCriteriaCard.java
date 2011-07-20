package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.scholastic.sbam.client.uiobjects.fields.BoundDateField;
import com.scholastic.sbam.client.uiobjects.fields.DateDefaultBinder;
import com.scholastic.sbam.client.uiobjects.fields.DateRangeBinder;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedCheckBoxGroup;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class TermCriteriaCard extends SnapshotCriteriaCardBase implements AppSleeper {
	
	protected final String			DATES_GROUP					=	"Dates";
	protected final String			START_DATE					=	"startDate";
	protected final String			END_DATE					=	"endDate";
	protected final String			TERMINATE_DATE				=	"terminateDate";
	protected final String			TERM_TYPES					=	"termTypes";
	protected final String			PROD_COMM_CODES				=	"productCommCodes";
	protected final String			AGREEMENT_COMM_CODES		=	"agreementCommCodes";
	protected final String			TERM_COMM_CODES				=	"termCommCodes";
	
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
		termTypeCheckGroup.setName(TERM_TYPES);
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
		
		prodCommCheckGroup.setName(PROD_COMM_CODES);
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
		
		agreementCommCheckGroup.setName(AGREEMENT_COMM_CODES);
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
		
		termCommCheckGroup.setName(PROD_COMM_CODES);
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
		setDateFieldRange(snapshotParameterSet, START_DATE,		startFromDate,		startToDate);
		setDateFieldRange(snapshotParameterSet, END_DATE,		endFromDate,		endToDate);
		setDateFieldRange(snapshotParameterSet, TERMINATE_DATE, terminateFromDate,	terminateToDate);
		
		setCheckBoxes(snapshotParameterSet, TERM_TYPES,		 		termTypeCheckGroup);
		setCheckBoxes(snapshotParameterSet, PROD_COMM_CODES, 		prodCommCheckGroup);
		setCheckBoxes(snapshotParameterSet, AGREEMENT_COMM_CODES,	agreementCommCheckGroup);
		setCheckBoxes(snapshotParameterSet, TERM_COMM_CODES, 		termCommCheckGroup);
	}

	@Override
	public void addParametersFromFields(SnapshotParameterSetInstance snapshotParameterSet) {
		snapshotParameterSet.addValue(START_DATE,		DATES_GROUP, startFromDate.getValue(),		startToDate.getValue());
		snapshotParameterSet.addValue(END_DATE,			DATES_GROUP, endFromDate.getValue(),		endToDate.getValue());
		snapshotParameterSet.addValue(TERMINATE_DATE,	DATES_GROUP, terminateFromDate.getValue(),	terminateToDate.getValue());
		
		addParametersFrom(termTypeCheckGroup, snapshotParameterSet, TERM_TYPES, TERM_TYPES);
		addParametersFrom(prodCommCheckGroup, snapshotParameterSet, PROD_COMM_CODES, PROD_COMM_CODES);
		addParametersFrom(agreementCommCheckGroup, snapshotParameterSet, AGREEMENT_COMM_CODES, AGREEMENT_COMM_CODES);
		addParametersFrom(termCommCheckGroup, snapshotParameterSet, TERM_COMM_CODES, TERM_COMM_CODES);
		
//		for (CheckBox checkBox : termTypeCheckGroup.getValues()) {
//			if (checkBox.getValue())
//				snapshotParameterSet.addValue(TERM_TYPES, TERM_TYPES, checkBox.getValueAttribute());
//		}
//		
//		for (CheckBox checkBox : prodCommCheckGroup.getValues()) {
//			if (checkBox.getValue())
//				snapshotParameterSet.addValue(PROD_COMM_CODES, PROD_COMM_CODES, checkBox.getValueAttribute());
//		}
//		
//		for (CheckBox checkBox : agreementCommCheckGroup.getValues()) {
//			if (checkBox.getValue())
//				snapshotParameterSet.addValue(AGREEMENT_COMM_CODES, AGREEMENT_COMM_CODES, checkBox.getValueAttribute());
//		}
//		
//		for (CheckBox checkBox : termCommCheckGroup.getValues()) {
//			if (checkBox.getValue())
//				snapshotParameterSet.addValue(TERM_COMM_CODES, TERM_COMM_CODES, checkBox.getValueAttribute());
//		}
	}
	
	
}
