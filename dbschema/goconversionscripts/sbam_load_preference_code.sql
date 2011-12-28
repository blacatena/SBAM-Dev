Truncate table `sbam`.`preference_code`;
INSERT INTO `sbam`.`preference_code`(
	pref_cat_code,
	pref_sel_code,
	description,
	export_value,
	seq,
	created_datetime,
	`STATUS`
)SELECT
	lower(GOPREFVAL.GO_pref_code)		AS pref_cat_code,
	lower(GO_pref_val)					AS pref_sel_code,
	goprefval.description 				AS description,
	go_export_val 						AS export_value,
	case 
		when `GOPREFVAL`.GO_PREF_VAL = `goprefcode`.DEFAULT_PREF_VAL then
			0
		else
			1 + goprefval.sort_code
	end									as seq,
	goprefval.date_added 				AS created_datetime,
	CASE goprefval.active
		WHEN 'Y' THEN
			'A'
		ELSE
			'I'
	END									AS `STATUS`
FROM
	`goprefval`, `goprefcode`
WHERE
	`goprefval`.GO_PREF_CODE = `GOPREFCODE`.go_pref_code
;

