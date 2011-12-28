select distinct ucn,shcst_3.ship_to_code,shcst_3.INSTITUTION,shcst_3.street_1,shcst_3.CITY,shcst_3.state,shcst_3.street_2,shcst_3.zip,shcst_3.STREET_3
from goip, ucnxref, shcst_3 
where shcst_3.SHIP_TO_CODE = goip.ship_to and goip.SHIP_TO = ucnxref.SHIP_TO_CODE and goip.SHIP_TO is not null and ucn in 
	(select ucn from (select distinct ucn,ship_to_code from goip, ucnxref where goip.SHIP_TO = ucnxref.SHIP_TO_CODE and goip.SHIP_TO is not null) x 
	group by ucn having count(*) > 1)
order by ucn,institution,ship_to_code