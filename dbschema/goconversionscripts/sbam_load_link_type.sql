INSERT INTO `sbam`.`link_type`(
	link_type_code,
	description,
	created_datetime,
	`STATUS`
)SELECT
	lower(go_link_type)AS link_type_code,
	tcase(description)AS description,
	date_added AS created_datetime,
CASE active
WHEN 'Y' THEN
	'A'
ELSE
	'I'
END AS `STATUS`
FROM
	`golinktype`;

