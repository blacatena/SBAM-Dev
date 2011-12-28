insert into truncated_ucn (ucn, truncucn, ship_to_code)
select ucn, 900000000 + (ucn - ((floor(ucn / 100000000)) * 100000000)) as truncucn, ship_to_code
from UCNXREF
where ucn > 0 and ucn >= 1000000000 -- 2147483647