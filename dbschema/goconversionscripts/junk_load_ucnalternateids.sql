INSERT INTO ucnalternateids(
	ucn, alternate_ids
)SELECT ucn, alternate_ids from
	(
		select ucn, group_concat(ship_to_code) as alternate_ids from (select distinct ucn, ship_to_code from ucnxref order by ucn, ship_to_code) codes group by ucn
	) inserts;

