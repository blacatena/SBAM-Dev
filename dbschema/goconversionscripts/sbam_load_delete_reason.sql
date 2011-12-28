INSERT INTO `sbam`.`delete_reason`(
	delete_reason_code,
	description,
	created_datetime,
	`STATUS`
)SELECT
	delete_reason AS delete_reason_code,
	tcase(description)AS description,
	date_added AS created_datetime,
	CASE active
WHEN 'Y' THEN
	'A'
ELSE
	'I'
END AS `STATUS`
FROM
	`godelreas`;

