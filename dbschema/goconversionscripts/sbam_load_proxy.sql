truncate table `sbam`.`proxy`;
INSERT INTO `sbam`.`proxy`(
	proxy_id,
	id_check_digit,
	description,
	search_keys,
	note,
	created_datetime,
	`STATUS`
)SELECT
	FLOOR(GOPROXY.GO_PROXY_NO / 10)						as id,
	goproxy.go_proxy_no									as id_check_digit,
	tcase(goproxy.DESCRIPTION)							as description,
	trim(concat(goproxy.go_proxy_no,' ',goproxy.SEARCH_KEY_1,' ',goproxy.search_key_2))
														as search_keys,
	''													as note,
-- 	CASE 
-- 		WHEN `note` IS NULL or length(note) = 0  THEN
--  			''
-- 		ELSE
-- 			note
-- 	END													as note,
	date_added											AS created_datetime,
	CASE active
		WHEN 'N' THEN 'X'
		ELSE		  'A'
	END 												AS `STATUS`
FROM
	goproxy
where goproxy.GO_PROXY_NO > 0
-- LEFT JOIN gonote_blob
-- ON
-- 	goproxy.go_note_id > 0
-- and	goproxy.go_number = gonote_blob.go_number
-- and goproxy.go_note_id = gonote_blob.go_note_id

