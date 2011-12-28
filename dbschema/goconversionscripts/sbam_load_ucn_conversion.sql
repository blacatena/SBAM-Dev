truncate table `sbam`.`ucn_conversion`;
INSERT INTO `sbam`.`ucn_conversion`(
	ucn,
	old_customer_code
) SELECT DISTINCT UCN, OLD_CUSTOMER_CODE FROM (
	SELECT distinct
		ucn				as ucn,
		ship_to_code 	as old_customer_code
	from ucnxref 
	where ucn is not null && ucn > 0 && ucn < 2147483647
	and ship_to_code > ' '
	and ship_to_code in (
		select distinct ship_to_code from (
			select sold_to from GOAUTHUNIT
			union
			select ship_to from goauthunit
			union
			select parent  from goauthunit
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
		) allcodes
	)
UNION
	SELECT distinct
		truncucn		as ucn,
		ship_to_code	as old_customer_code
	from truncated_ucn 
	where truncucn is not null && truncucn > 0 && truncucn < 2147483647
	and ship_to_code > ' '
	and ship_to_code in (
		select distinct ship_to_code from (
			select sold_to from GOAUTHUNIT
			union
			select ship_to from goauthunit
			union
			select parent  from goauthunit
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
		) allcodes
	)
) conversions;
truncate ucn_conversion_copy;
insert into ucn_conversion_copy (ucn, old_customer_code) select ucn, old_customer_code from `sbam`.`ucn_conversion`;

