update `sbam`.`institution` as i
	set alternate_ids = 
		(select group_concat(old_customer_code)from ucn_alternate_id where ucn_alternate_id.ucn = i.ucn order by old_customer_code)
;