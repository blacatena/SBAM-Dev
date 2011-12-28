DO NOT USE
INSERT IGNORE INTO `sbam`.`auth_method`(
	agreement_id,
	ucn,
	ucn_suffix,
	site_loc_code,
	method_type,
	method_key,
	url,
	user_id,
	ip_lo,
	ip_hi,
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
	FLOOR(GO_NUMBER / 10)								as agreement_id,
	CASE 
		WHEN ucn IS NULL THEN
			0
		ELSE
			ucn
	END													as ucn,
	CASE ucn_suffix
		WHEN NULL THEN
			0
		ELSE
			ucn_suffix
	END													as ucn_suffix,
	goip.GO_LOC_CODE									as site_loc_code,
	'ip'												as method_type,
	concat(ip_lo,':',ip_hi)								as method_key,
	''													as url,
	''													as user_id,
	ip_lo												as ip_lo,
	ip_hi												as ip_hi,
	''													as `password`,
	0													as proxy_id,
	''													as user_type,
	remote_flag											as remote,
	approved											as approved,
	validated											as validated,
	activated											as activated,
	''													as note,
	''													as org_path,
	date_added											AS created_datetime,
	date_updated										as updated_datetime,
	date_activated										as activated_datetime,
	date_deactivated									as deactivated_datetime,
	date_reactivated									as reactivated_datetime,
	'A'													as `status`
FROM
	goip
LEFT JOIN sbam.ucn_conversion
	ON goip.SHIP_TO = sbam.ucn_conversion.old_customer_code;

