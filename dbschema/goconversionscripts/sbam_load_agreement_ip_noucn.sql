INSERT IGNORE INTO `sbam`.`auth_method`(
	agreement_id,
	ucn,
	ucn_suffix,
	site_loc_code,
	method_type,
-- 	method_key,
	for_ucn,
	for_ucn_suffix,
	for_site_loc_code,
	url,
	user_id,
	ip_lo,
	ip_hi,
	ip_range_code,
	`password`,
	proxy_id,
	user_type,
	remote,
	approved,
	validated,
	activated,
	note,
	org_path,
	created_datetime,
	updated_datetime,
	activated_datetime,
	deactivated_datetime,
	reactivated_datetime,
	`status`
)SELECT
	FLOOR(goip.GO_NUMBER / 10)							as agreement_id,
	0													as ucn,
	0													as ucn_suffix,
	''													as site_loc_code,
	'ip'												as method_type,
-- 	concat(ip_lo,':',ip_hi)								as method_key,
	0													as for_ucn,
	0													as for_ucn_suffix,
	lower(goip.GO_LOC_CODE)								as for_site_loc_code,
	''													as url,
	''													as user_id,
	ip_lo												as ip_lo,
	ip_hi												as ip_hi,
	ip_range_coded(ip_lo,ip_hi,16)						as ip_range_code,
	''													as `password`,
	0													as proxy_id,
	'-'													as user_type,
	CASE remote_flag
		WHEN 'Y' THEN 'y'
		ELSE 'n'
	END													as remote,
	CASE approved
		WHEN 'Y' THEN 'y'
		ELSE 'n'
	END													as approved,
	CASE validated
		WHEN 'Y' THEN 'y'
		ELSE 'n'
	END													as validated,
	CASE activated
		WHEN 'Y' THEN 'y'
		ELSE 'n'
	END													as activated,
	CASE 
		WHEN goip.SHIP_TO IS NULL THEN
			`note`
		WHEN length(goip.SHIP_TO) = 0 THEN
 			`note`
 		WHEN `note` IS NULL or length(note) = 0  THEN
 			concat(goip.ship_to,':::')
		ELSE
			concat(goip.SHIP_TO,':::', note)
	END													as note,
	''													as org_path,
	date_added											AS created_datetime,
	date_updated										as updated_datetime,
	case
		when date_activated < '0001-01-01' THEN null 
		else DATE_ACTIVATED
	END													as activated_datetime,
	case
		when date_deactivated < '0001-01-01' THEN null 
		else DATE_DEACTIVATED
	END													as deactivated_datetime,
	case
		when date_reactivated < '0001-01-01' THEN null 
		else DATE_REACTIVATED
	END													as reactivated_datetime,
	'A'													as `status`
FROM
	goip
LEFT JOIN gonote_blob
ON
	goip.go_note_id > 0
and	goip.go_number = gonote_blob.go_number
and goip.go_note_id = gonote_blob.go_note_id
WHERE octet_1 > ' ' -- need to ignore bad data

