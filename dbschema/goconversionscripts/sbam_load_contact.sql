truncate `sbam`.`contact`;
INSERT INTO `sbam`.`contact`(
	contact_type_code,
	full_name,
	title,
	additional_info,
	address1,
	address2,
	address3,
	city,
	state,
	zip,
	country,
	e_mail,
	e_mail_2,
	phone,
	phone_2,
	fax,
	note,
	created_datetime,
	`STATUS`
)SELECT DISTINCT
	lower(new_contact_type(gocontact.contact_id))		as contact_type_code,
-- 	case 
-- 		when	gocontact.contact_id = 'LIBRN4' 
-- 		or	 	gocontact.contact_id = 'LIBRN5'
-- 		or		gocontact.contact_id = 'LIBRN6'
-- 		or		gocontact.contact_id = 'LIBRN7'
-- 		or		gocontact.contact_id = 'LIBRN8'
-- 		or		gocontact.contact_id = 'LIBRN9'
-- 		or		gocontact.contact_id = 'LIBRN0'
-- 		or		gocontact.contact_id = 'LIBRN11'
-- 		or		gocontact.contact_id = 'LIBRN12'
-- 		or		gocontact.contact_id = 'LIBRN13'
-- 		or		gocontact.contact_id = 'LIBRN14'
-- 		or		gocontact.contact_id = 'LIBRN15' then
-- 			'librn'
-- 		when gocontact.contact_id like 'CONV%' then
-- 			'conv1'
-- 		when	gocontact.contact_id = 'TECHC1'
-- 		or		gocontact.contact_id = 'TECH2'
-- 		or		gocontact.contact_id = 'TECH3'
-- 		or		gocontact.contact_id = 'TECH4'
-- 		or		gocontact.contact_id = 'TECH5' then
-- 			'tech'
-- 		else
-- 			lower(gocontact.contact_id)
-- 	END													as contact_type_code,
	tcase(GOCONTACT.CONTACT_NAME)						as full_name,
	tcase(GOCONTACT.TITLE)								as title,
	tcase(GOCONTACT.INSTITUTION)						as additional_info,
	tcase(GOCONTACT.STREET_1)							as address1,
	tcase(GOCONTACT.STREET_2)							as address2,
	tcase(GOCONTACT.STREET_3)							as address3,
	tcase(GOCONTACT.CITY)								as city,
	GOCONTACT.STATE										as state,
	GOCONTACT.ZIP										as zip,
	tcase(GOCONTACT.COUNTRY)							as country,
	GOCONTACT.E_MAIL									as e_mail,
	''													as e_mail_2,
	GOCONTACT.PHONE										as phone,
	''													as phone_2,
	GOCONTACT.FAX_PHONE									as fax,
	CASE note WHEN null THEN '' ELSE note END			as note,
	CASE 
		WHEN date_added IS NULL THEN
			CURRENT_TIMESTAMP
		WHEN date_added < '0001-01-01' THEN
			CURRENT_TIMESTAMP
		ELSE date_added		
	END													AS created_datetime,
	'A' 												AS `STATUS`
FROM
	GOCONTACT
LEFT JOIN gonote_blob
ON
	GOCONTACT.go_note_id > 0
and	GOCONTACT.go_number = gonote_blob.go_number
and GOCONTACT.go_note_id = gonote_blob.go_note_id,
(SELECT MAX(GO_NUMBER) AS GO_NUMBER,CONTACT_ID,CONTACT_NAME,E_MAIL,PHONE
	FROM GOCONTACT
	GROUP BY CONTACT_ID,CONTACT_NAME,E_MAIL,PHONE) uniqueContacts
WHERE GOCONTACT.GO_NUMBER = uniqueContacts.GO_NUMBER
AND   GOCONTACT.CONTACT_ID = uniqueContacts.CONTACT_ID

