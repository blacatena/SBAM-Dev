INSERT INTO `sbam`.`term_type`(
	term_type_code,
	description,
	activate,
	created_datetime,
	`STATUS`
)SELECT
	lower(service_type)AS term_type_code,
	tcase(description)AS description,
	lower(activate)AS activate,
	date_added AS created_datetime,
CASE active
WHEN 'Y' THEN
	'A'
ELSE
	'I'
END AS `STATUS`
FROM
	`gosvctype`;

