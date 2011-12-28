truncate `sbam`.`agreement_contact`;
INSERT INTO `sbam`.`agreement_contact`(
	agreement_id,
	contact_id,
	renewal_contact,
	created_datetime,
	`STATUS`
)SELECT DISTINCT
	FLOOR(GOCONTACT.GO_NUMBER / 10)						as agreement_id,
	sbamcontact.contact_id								as contact_id,
	lower(GOCONTACT.RENEWAL_CONTACT)					as renewal_contact,
	CASE 
		WHEN date_added IS NULL THEN
			CURRENT_TIMESTAMP
		WHEN date_added < '0001-01-01' THEN
			CURRENT_TIMESTAMP
		ELSE date_added		
	END													AS created_datetime,
	'A' 												AS `STATUS`
FROM
	GOCONTACT, sbam.contact as sbamcontact
WHERE
	new_contact_type(GOCONTACT.CONTACT_ID)			= upper(sbamcontact.contact_type_code)
AND GOCONTACT.CONTACT_NAME			= upper(sbamcontact.full_name)
AND GOCONTACT.PHONE					= sbamcontact.phone
AND GOCONTACT.E_MAIL				= sbamcontact.e_mail
-- AND GOCONTACT.TITLE				= upper(sbamcontact.title)
-- AND GOCONTACT.INSTITUTION		= upper(sbamcontact.additional_info)
-- AND GOCONTACT.STREET_1			= upper(sbamcontact.address1)
-- AND GOCONTACT.CITY				= upper(sbamcontact.city)
-- AND GOCONTACT.ZIP				= upper(sbamcontact.zip)
-- AND GOCONTACT.FAX_PHONE			= upper(sbamcontact.fax)


