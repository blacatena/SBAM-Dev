select a.truncucn, a.ucn, b.ucn, a.ship_to_code, b.ship_to_code 
from truncated_ucn a, truncated_ucn b 
where a.truncucn = b.truncucn
and a.ucn <> b.ucn 
order by a.truncucn,a.ucn,a.ship_to_code