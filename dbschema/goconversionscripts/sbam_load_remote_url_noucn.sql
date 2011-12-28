INSERT IGNORE INTO `sbam`.`remote_url`(
	agreement_id,
	ucn,
	ucn_suffix,
	site_loc_code,
	url,
	approved,
	activated,
	note,
	org_path,
	created_datetime,
	`status`
)SELECT
	FLOOR(goremoteurl.GO_NUMBER / 10)					as agreement_id,
	0													as ucn,
	0													as ucn_suffix,
	goremoteurl.GO_LOC_CODE								as site_loc_code,
	GOREMOTEURL.REFERRER_URL							as url,
	lower(approved)										as approved,
	lower(activated)									as activated,
	CASE 
		WHEN goremoteurl.SHIP_TO IS NULL THEN
			`note`
		WHEN length(goremoteurl.SHIP_TO) = 0 THEN
 			`note`
 		WHEN `note` IS NULL or length(note) = 0  THEN
 			concat(goremoteurl.ship_to,':::')
		ELSE
			concat(goremoteurl.SHIP_TO,':::', note)
	END													as note,
	''													as org_path,
	date_added											AS created_datetime,
	'A'													as `status`
FROM
	goremoteurl
LEFT JOIN gonote_blob
ON
	goremoteurl.go_note_id > 0
and	goremoteurl.go_number = gonote_blob.go_number
and goremoteurl.go_note_id = gonote_blob.go_note_id

