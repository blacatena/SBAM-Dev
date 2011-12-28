INSERT INTO `sbam`.`alien_institution`(
	ucn,
	institution_name,
	address1,
	address2,
	address3,
	city,
	state,
	zip,
	country,
	parent_ucn,
	public_private_code,
	type_code,
	group_code,
	`source`,
	created_date,
	`status`
)SELECT
	t.truncucn											as ucn,
	tcase(s.institution)								as institution_name,
	tcase(s.street_1)									as address1,
	tcase(s.street_2)									as address2,
	tcase(s.street_3)									as address3,
	tcase(s.city)										as city,
	s.state												as state,
	s.zip												as zip,
	s.country											as country,
	900000000 + (xp.ucn - ((floor(xp.ucn / 100000000)) * 100000000))	
														as parent_ucn,
	'00'												as public_private_code,
	'???'												as type_code,
	'O'													as group_code,
	'Global'											as source,
	s.created											AS created_datetime,
	'A'													AS `STATUS`
FROM
	truncated_ucn t, ucnxref x, shcst_3 s, shcst_3 p, ucnxref xp
where t.ucn = x.ucn
and x.SHIP_TO_CODE = s.ship_to_code 
and s.sold_to_cust = p.ship_to_code
and x.ucn > 0 and x.ucn >= 1000000000 -- 2147483647
and p.ship_to_code = xp.SHIP_TO_CODE 
and s.ship_to_code in (
	select GOAUTHUNIT.PARENT from GOAUTHUNIT
	UNION
	select GOAUTHUNIT.SHIP_TO from GOAUTHUNIT
	UNION
	select GOAUTHUNIT.SOLD_TO from GOAUTHUNIT
	UNION
	select GOSHIP.SHIP_TO from GOSHIP
	UNION
	select GOMAIN.SOLD_TO from GOMAIN
	UNION 
	select GOLINK.SOLD_TO from GOLINK
	UNION
	select SOLD_TO_CUST from SHCST_3 sp WHERE SHIP_TO_CODE IN (
		select GOAUTHUNIT.PARENT from GOAUTHUNIT
		UNION
		select GOAUTHUNIT.SHIP_TO from GOAUTHUNIT
		UNION
		select GOAUTHUNIT.SOLD_TO from GOAUTHUNIT
		UNION
		select GOSHIP.SHIP_TO from GOSHIP
		UNION
		select GOMAIN.SOLD_TO from GOMAIN
		UNION 
		select GOLINK.SOLD_TO from GOLINK
	)
)

