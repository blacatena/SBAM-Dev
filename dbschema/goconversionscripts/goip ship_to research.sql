select distinct ucnxref.ucn,shcst_3.ship_to_code,shcst_3.INSTITUTION,shcst_3.street_1,shcst_3.CITY,shcst_3.state,shcst_3.street_2,shcst_3.zip,shcst_3.STREET_3,
i.Institution_Name,i.ADDRESS1,i.ADDRESS2,i.CITY,i.COUNTY
from goip, ucnxref, shcst_3,tms_exports.Institutions i
where shcst_3.SHIP_TO_CODE = goip.ship_to and goip.SHIP_TO = ucnxref.SHIP_TO_CODE and goip.SHIP_TO is not null and ucnxref.ucn > 0 and i.Institution_UCN = ucnxref.ucn
and length(trim(shcst_3.STREET_3)) > 0
order by ucn,institution,ship_to_code