INSERT INTO `sbam`.`service`(
	service_code,
	description,
	export_value,
	export_file,
	created_datetime,
	service_type,
	`STATUS`
)SELECT
	lower(GO_server)AS service_code,
	tcase(description)AS description,
	lower(go_pref_code)AS export_value,
	serverfile as export_file,
	date_added AS created_datetime,
	CASE primary_svc
WHEN 'Y' THEN
	'I'
ELSE
	'A'
END AS service_type,
CASE active
WHEN 'Y' THEN
	'A'
ELSE
	'I'
END AS `STATUS`
FROM
	`goserverid`;

