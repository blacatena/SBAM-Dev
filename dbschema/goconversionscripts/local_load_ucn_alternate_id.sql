insert into ucn_alternate_id (
UCN,
old_customer_code
) select distinct
	ucn,
	old_customer_code
from (
	select
		ucn,
		ship_to_code 	as old_customer_code
	from ucnxref
	union
	select 
		truncucn		as ucn,
		ship_to_code	as old_customer_code
	from truncated_ucn
) mapping