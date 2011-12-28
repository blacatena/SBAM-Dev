update `sbam`.`institution` as i
	set alternate_ids = 
		(select alternate_ids from ucnalternateids where ucnalternateids.ucn = i.ucn ) 
;