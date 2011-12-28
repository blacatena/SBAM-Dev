truncate `sbam`.`contact_type`;
INSERT INTO `sbam`.`contact_type`(
	contact_type_code,
	description,
	created_datetime,
	`STATUS`
)SELECT
	lower(new_contact_type(contact_id))		AS contact_type_code,
	tcase(description)						AS description,
	date_added								AS created_datetime,
	'A'										AS `STATUS`
FROM
	`GOCONTACT_ID`
WHERE new_contact_type(contact_id) = contact_id
-- where contact_id not like '%11' and contact_id not like '%12' and contact_id not like '%13'
-- and contact_id not like '%4' and contact_id not like '%5' and contact_id not like '%6' and contact_id not like '%7'
-- and contact_id not like '%8' and contact_id not like '%9' and contact_id not like '%0'
-- and (contact_id  not like 'CONV%' or contact_id = 'CONV1')
-- and contact_id <> 'TECHC1' and contact_id <> 'CONV2' and contact_id <> 'CONV3'
;