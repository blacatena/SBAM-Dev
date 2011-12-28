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
	FLOOR(gourl.GO_NUMBER / 10)							as agreement_id,
	0													as ucn,
	0													as ucn_suffix,
	''													as site_loc_code,
	'url'												as method_type,
-- 	gourl.REFERRER_URL									as method_key,
	0													as for_ucn,
	0													as for_ucn_suffix,
	lower(gourl.GO_LOC_CODE)							as for_site_loc_code,
	gourl.REFERRER_URL									as url,
	''													as user_id,
	0													as ip_lo,
	0													as ip_hi,
	''													as `password`,
	0													as proxy_id,
	'-'													as user_type,
	lower(remote_flag)									as remote,
	lower(approved)										as approved,
	'y'													as validated,
	activated											as activated,
	CASE 
		WHEN gourl.SHIP_TO IS NULL THEN
			`note`
		WHEN length(gourl.SHIP_TO) = 0 THEN
 			`note`
 		WHEN `note` IS NULL or length(note) = 0  THEN
 			concat(gourl.ship_to,':::')
		ELSE
			concat(gourl.SHIP_TO,':::', note)
	END													as note,
	''													as org_path,
	date_added											AS created_datetime,
	date_updated										as updated_datetime,
	null												as activated_datetime,
	null												as deactivated_datetime,
	null												as reactivated_datetime,
	'A'													as `status`
FROM
	gourl
LEFT JOIN gonote_blob
ON
	gourl.go_note_id > 0
and	gourl.go_number = gonote_blob.go_number
and gourl.go_note_id = gonote_blob.go_note_id

