INSERT INTO `sbam`.`site`(
	ucn,
	ucn_suffix,
	site_loc_code,
	description,
	commission_code,
	pseudo_site,
	note,
	created_datetime,
	`STATUS`
)SELECT
	ucn													as ucn,
	ucn_suffix											as ucn_suffix,
	lower(go_loc_code)									as site_loc_code,
	tcase(location_name)								as description,
	''													as commission_code,
	CASE ucn_suffix
		when 1 then 'n'
		else 'y'
	END													as pseudo_site,
	''													as note,
	date_added											AS created_datetime,
	'A'													AS `STATUS`
FROM
	goloc, sbam.ucn_conversion
where goloc.SHIP_TO = sbam.ucn_conversion.old_customer_code

