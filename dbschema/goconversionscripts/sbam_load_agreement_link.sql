INSERT INTO `sbam`.`agreement_link`(
	link_id,
	link_id_check_digit,
	ucn,
	link_type_code,
	note,
	created_datetime,
	`STATUS`
)SELECT DISTINCT
	FLOOR(GO_LINK_NO / 10)								as link_id,
	go_link_no											as link_id_check_digit,
	ucn													as ucn,
	lower(go_link_type)									as link_type_code,
	''													as note,
	CASE 
		WHEN date_added IS NULL THEN
			CURRENT_TIMESTAMP
		WHEN date_added < '0001-01-01' THEN
			CURRENT_TIMESTAMP
		ELSE date_added		
	END													AS created_datetime,
	'A' 												AS `STATUS`
FROM
	GOLINK, sbam.ucn_conversion ucnx
WHERE
	GOLINK.SOLD_TO = ucnx.old_customer_code


