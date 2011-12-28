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
	lower(new_contact_type(GOSHIPCONTACT.CONTACT_ID))		as contact_type_code,
	tcase(GOSHIPCONTACT.CONTACT_NAME)						as full_name,
	tcase(GOSHIPCONTACT.TITLE)								as title,
	tcase(GOSHIPCONTACT.INSTITUTION)						as additional_info,
	tcase(GOSHIPCONTACT.STREET_1)							as address1,
	tcase(GOSHIPCONTACT.STREET_2)							as address2,
	tcase(GOSHIPCONTACT.STREET_3)							as address3,
	tcase(GOSHIPCONTACT.CITY)								as city,
	GOSHIPCONTACT.STATE										as state,
	GOSHIPCONTACT.ZIP										as zip,
	tcase(GOSHIPCONTACT.COUNTRY)							as country,
	GOSHIPCONTACT.E_MAIL									as e_mail,
	''														as e_mail_2,
	GOSHIPCONTACT.PHONE										as phone,
	''														as phone_2,
	GOSHIPCONTACT.FAX_PHONE									as fax,
	concat(GOSHIPCONTACT.SHIP_TO,':::')						as note,
	CASE 
		WHEN date_added IS NULL THEN
			CURRENT_TIMESTAMP
		WHEN date_added < '0001-01-01' THEN
			CURRENT_TIMESTAMP
		ELSE date_added		
	END														AS created_datetime,
	'A' 													AS `STATUS`
FROM
	GOSHIPCONTACT
-- LEFT JOIN gonote_blob
-- ON
-- 	GOSHIPCONTACT.go_note_id > 0
-- and	GOSHIPCONTACT.go_number = gonote_blob.go_number
-- and GOSHIPCONTACT.go_note_id = gonote_blob.go_note_id 
-- , (SELECT MAX(GO_NUMBER) AS GO_NUMBER,CONTACT_ID,CONTACT_NAME,E_MAIL,PHONE
-- 	FROM GOCONTACT
-- 	GROUP BY CONTACT_ID,CONTACT_NAME,E_MAIL,PHONE) uniqueContacts
-- WHERE GOCONTACT.GO_NUMBER = uniqueContacts.GO_NUMBER
-- AND   GOCONTACT.CONTACT_ID = uniqueContacts.CONTACT_ID

