truncate table `sbam`.`agreement_site`;
INSERT INTO `sbam`.`agreement_site`(
	agreement_id,
	site_ucn,
	site_ucn_suffix,
	site_loc_code,
	note,
	created_datetime,
	`STATUS`
)SELECT
	FLOOR(goship.GO_NUMBER / 10)						as agreement_id,
	ucn													as site_ucn,
	ucn_suffix											as site_ucn_suffix,
	''													as site_loc_code,
	note												as note,
	goship.date_added									AS created_datetime,
	'A'													AS `STATUS`
FROM
	goship
LEFT JOIN gonote_blob
ON
	goship.go_note_id > 0
and	goship.go_number = gonote_blob.go_number
and goship.go_note_id = gonote_blob.go_note_id, 
sbam.ucn_conversion
where goship.SHIP_TO = sbam.ucn_conversion.old_customer_code

