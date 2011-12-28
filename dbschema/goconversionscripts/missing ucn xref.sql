SELECT distinct
	ship_to_code
from ucnxref where 
ucn = 0
and
ship_to_code in (
	select distinct ship_to_code from (
		select sold_to from GOAUTHUNIT
		union
		select ship_to from goauthunit
		union
		select parent  from goauthunit
	) allcodes
)
