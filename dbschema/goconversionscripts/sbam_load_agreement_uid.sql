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
	reactivated_datetime
	`status`
)SELECT
	FLOOR(GO_NUMBER / 10)								as agreement_id,
	CASE ucn
		WHEN NULL THEN
			0
		ELSE
			ucn
	END													as ucn,
	CASE ucn_suffix
		WHEN  NULL THEN
			0
		ELSE
			ucn_suffix
	END													as ucn_suffix,
	gopassword.GO_LOC_CODE								as site_loc_code,
	'uid'												as method_type,
	user_id												as method_key,
	''													as url,
	gopassword.USER_ID									as user_id,
	0													as ip_lo,
	0													as ip_hi,
	`password`											as `password`,
	CASE 
		WHEN gopassword.USE_PROXY = 'Y' THEN
			gopassword.GO_PROXY_NO
		ELSE 0	
	END 												as proxy_id,
	gopassword.USER_TYPE								as user_type,
	remote_flag											as remote,
	approved											as approved,
	'Y'													as validated,
	gopassword.ACTIVATED								as activated,
	''													as note,
	''													as org_path,
	date_added											AS created_datetime,
	date_updated										as updated_datetime,
	date_activated										as activated_datetime,
	NULL												as deactivated_datetime,
	null												as reactivated_datetime,
	'A'													as status
FROM
	gopassword
LEFT JOIN sbam.ucn_conversion
	ON gopassword.SHIP_TO = sbam.ucn_conversion.old_customer_code;

