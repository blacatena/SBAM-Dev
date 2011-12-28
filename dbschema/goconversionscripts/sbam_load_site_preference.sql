truncate table sbam.site_preference;
insert into sbam.site_preference (
	ucn,
	ucn_suffix,
	site_loc_code,
	pref_cat_code,
	pref_sel_code,
	created_datetime,
	`status`
) select
	ucn								as ucn,
	ucn_suffix						as ucn_suffix,
	lower(go_loc_code)				as site_loc_code,
	lower(GOPREFFREE.GO_PREF_CODE)	as pref_cat_code,
	gopreffree.GO_FREE_VAL			as pref_sel_code,
	GOPREFFREE.DATE_ADDED			as created_datetime,
	'A'								as status
from gopreffree, ucn_conversion_copy where gopreffree.SHIP_TO = ucn_conversion_copy.old_customer_code;
insert into sbam.site_preference (
	ucn,
	ucn_suffix,
	site_loc_code,
	pref_cat_code,
	pref_sel_code,
	created_datetime,
	`status`
) select
	ucn								as ucn,
	ucn_suffix						as ucn_suffix,
	lower(GOPREFSEL.GO_LOC_CODE)	as site_loc_code,
	lower(GOPREFSEL.GO_PREF_CODE)	as pref_cat_code,
	lower(GOPREFSEL.GO_PREF_VAL)	as pref_sel_code,
	GOPREFSEL.DATE_ADDED			as created_datetime,
	'A'								as status
from goprefsel, ucn_conversion_copy where GOPREFSEL.SHIP_TO = ucn_conversion_copy.old_customer_code
ON DUPLICATE KEY UPDATE
	pref_sel_code = lower(GOPREFSEL.GO_PREF_VAL);