INSERT INTO `sbam`.`cancel_reason`(
	cancel_reason_code,
	description,
	change_not_cancel,
	created_datetime,
	`STATUS`
)SELECT
	cancel_reason AS cancel_reason_code,
	tcase(description)AS description,
	lower(`change`) as change_not_cancel,
	date_added AS created_datetime,
	CASE active
WHEN 'Y' THEN
	'A'
ELSE
	'I'
END AS `STATUS`
FROM
	`gocanreas`;

