INSERT INTO `ucn_conversion`(
	ucn,
	old_customer_code
)SELECT distinct
	ucn,
	ship_to_code as old_customer_code
from ucnxref 
where ucn is not null && ucn > 0
and ship_to_code > ' '
and ship_to_code in (
	select distinct ship_to_code from (
		select sold_to from GOAUTHUNIT
		union
		select ship_to from goauthunit
		union
		select parent  from goauthunit
	) allcodes
)

