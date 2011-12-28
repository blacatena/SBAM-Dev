truncate table `sbam`.`agreement`;
INSERT INTO `sbam`.`agreement`(
	id,
	id_check_digit,
	bill_ucn,
	bill_ucn_suffix,
	agreement_link_id,
	link_id_check_digit,
	agreement_type_code,
	commission_code,
	delete_reason_code,
	workstations,
	buildings,
	enrollment,
	population,
	note,
	created_datetime,
	`STATUS`
)SELECT
	FLOOR(gomain.GO_NUMBER / 10)						as id,
	gomain.go_number									as id_check_digit,
	ucn													as bill_ucn,
	ucn_suffix											as bill_ucn_suffix,
	FLOOR(gomain.go_link_no / 10)						as agreement_link_id,
	gomain.go_link_no									as link_id_check_digit,
	go_type												as agreement_type,
	CASE go_type
		WHEN 'EDG' 	THEN 'ED'
		WHEN 'SCLG' THEN 'SCLG'
		WHEN 'SHR'  THEN 'Share'
		WHEN 'WW'	THEN 'WW'
		ELSE 'NONE'
	END													AS commission_code,
	CASE deleted
		WHEN 'Y'	THEN	'9999'
		ELSE				''
	END													AS delete_reason_code,
	gomain_profile.num_workstations						AS workstations,
	gomain_profile.num_buildings						AS buildings,
	gomain_profile.enrollment							AS enrollment,
	gomain_profile.population							AS population,
	CASE 
		WHEN `note` IS NULL or length(note) = 0  THEN
 			''
		ELSE
			note
	END													as note,
	date_added											AS created_datetime,
	CASE deleted
		WHEN 'Y' THEN 'X'
		ELSE		  'A'
	END 												AS `STATUS`
FROM
	gomain
LEFT JOIN gonote_blob
ON
	gomain.go_note_id > 0
and	gomain.go_number = gonote_blob.go_number
and gomain.go_note_id = gonote_blob.go_note_id
LEFT JOIN gomain_profile
	ON gomain_profile.go_number = gomain.go_number
, sbam.ucn_conversion
	where gomain.SOLD_TO = sbam.ucn_conversion.old_customer_code;

