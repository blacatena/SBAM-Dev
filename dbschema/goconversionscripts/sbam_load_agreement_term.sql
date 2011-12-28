INSERT INTO `sbam`.`agreement_term`(
	agreement_id,
	product_code,
	start_date,
	end_date,
	terminate_date,
	term_type,
	cancel_reason_code,
	cancel_date,
	dollar_value,
	workstations,
	buildings,
	population,
	enrollment,
	po_number,
	reference_sa_id,
	commission_code,
	org_path,
	primary_org_path,
	created_datetime,
	`STATUS`
)SELECT
	FLOOR(GO_NUMBER / 10)								as agreement_id,
	lower(GO_PRODUCT)									as product_code,
	CASE
		WHEN START_DATE IS NULL THEN
			NULL
		WHEN START_DATE  <= '0000-01-01' THEN
			NULL
		ELSE
			START_DATE									
	END													as start_date,
	CASE
		WHEN END_DATE IS NULL THEN
			NULL
		WHEN END_DATE  <= '0000-01-01' THEN
			NULL
		ELSE
			END_DATE									
	END													as end_date,
	CASE
		WHEN TERMINATE_DATE IS NULL THEN
			NULL
		WHEN TERMINATE_DATE  <= '0000-01-01' THEN
			NULL
		ELSE
			TERMINATE_DATE									
	END													as terminate_date,
	SERVICE_TYPE										as term_type,
	CANCEL_REASON										as cancel_reason_code,
	CASE
		WHEN CANCEL_REASON IS NOT NULL AND CANCEL_REASON > 0  AND TERMINATE_DATE > '0000-00-00' THEN
			TERMINATE_DATE
		ELSE
			NULL
	END													as cancel_date,
	EXTENSION											as dollar_value,
	NUM_WORKSTATIONS									as workstations,
	NUM_BUILDINGS										as buildings,
	POPULATION											as population,
	ENROLLMENT											as enrollment,
	PO_NUMBER											as po_number,
	REF_GO_NUMBER										as reference_sa_id,
	NULL												as commission_code,
	case
		when contract_grp = 0 THEN ''
		else concat('Group ', CONTRACT_GRP)
	end													as org_path,
	case
		when contract_grp = 0 THEN ''
		else concat('Group ',CONTRACT_GRP)
	end													as primary_org_path,
	date_added											AS created_datetime,
	CASE
		WHEN CANCEL_REASON IS NOT NULL AND CANCEL_REASON > 0 THEN
			'I'
		ELSE
			'A'
	END													as `status`
FROM
	goservice

