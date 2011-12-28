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
	FLOOR(gopassword.GO_NUMBER / 10)					as agreement_id,
	0													as ucn,
	0													as ucn_suffix,
	''													as site_loc_code,
	'uid'												as method_type,
-- 	user_id												as method_key,
	0													as for_ucn,
	0													as for_ucn_suffix,
	lower(gopassword.GO_LOC_CODE)						as for_site_loc_code,
	''													as url,
	gopassword.USER_ID									as user_id,
	0													as ip_lo,
	0													as ip_hi,
	`password`											as `password`,
	CASE 
		WHEN gopassword.USE_PROXY = 'Y' THEN
			FLOOR(gopassword.GO_PROXY_NO / 10)
		ELSE 0	
	END 												as proxy_id,
	gopassword.USER_TYPE								as user_type,
	lower(remote_flag)									as remote,
	lower(approved)										as approved,
	'y'													as validated,
	gopassword.ACTIVATED								as activated,
	CASE 
		WHEN gopassword.SHIP_TO IS NULL THEN
			`note`
		WHEN length(gopassword.SHIP_TO) = 0 THEN
 			`note`
 		WHEN `note` IS NULL or length(note) = 0  THEN
 			concat(gopassword.ship_to,':::')
		ELSE
			concat(gopassword.SHIP_TO,':::', note)
	END													as note,
	''													as org_path,
	date_added											AS created_datetime,
	date_updated										as updated_datetime,
	case
		when date_activated < '0001-01-01' THEN null 
		else DATE_ACTIVATED
	END													as activated_datetime,
	NULL												as deactivated_datetime,
	null												as reactivated_datetime,
	'A'													as status
FROM
	gopassword
LEFT JOIN gonote_blob
ON
	gopassword.go_note_id > 0
and	gopassword.go_number = gonote_blob.go_number
and gopassword.go_note_id = gonote_blob.go_note_id

