SELECT 
	ucn,
	admin_user_id as admin_username,
	admin_password as admin_password,
	go_access_grp as access_group,
	date_added AS created_datetime,
	'A' AS status,
	ship_to
FROM
	`goadmin` a1,`ucnxref` u1
where a1.ship_to = u1.ship_to_code and u1.ucn > 0
and  exists (select * from goadmin a2, ucnxref u2 where 
	a2.SHIP_TO = u2.ship_to_code and u2.ucn = u1.ucn and (a2.admin_user_id <> a1.admin_user_id or a2.admin_password <> a1.admin_password or a2.go_access_grp <> a1.go_access_grp))
order by ucn, admin_user_id, access_group;