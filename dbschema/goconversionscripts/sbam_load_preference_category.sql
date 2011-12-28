INSERT INTO `sbam`.`preference_category`(
	pref_cat_code,
	description,
	seq,
	created_datetime,
	`STATUS`
)SELECT
	lower(go_pref_code)AS pref_cat_code,
	tcase(description)AS description,
	sort_code AS seq,
	date_added AS created_datetime,
	CASE active
WHEN 'Y' THEN
	'A'
ELSE
	'I'
END AS `STATUS`
FROM
	`goprefcode`;

