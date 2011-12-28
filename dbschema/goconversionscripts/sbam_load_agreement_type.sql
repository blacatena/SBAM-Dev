INSERT INTO `sbam`.`agreement_type`(
	agreement_type_code,
	description,
	short_name,
	created_datetime,
	`STATUS`
)SELECT
	lower(go_type)AS agreement_type_code,
	tcase(description)AS description,
	tcase(description)AS short_name,
	date_added AS created_datetime,
CASE active
WHEN 'Y' THEN
	'A'
ELSE
	'I'
END AS `STATUS`
FROM
	`gotype`;

