INSERT INTO `sbam`.`stats_admin`(
	ucn,
	admin_username,
	admin_password,
	access_group,
	created_datetime,
	`STATUS`
)SELECT DISTINCT
	ucn,
	admin_user_id as admin_username,
	min(admin_password) as admin_password,
	min(go_access_grp) as access_group,
	min(date_added) AS created_datetime,
	'A' AS status
FROM
	`goadmin` a1,`ucnxref` u1
where a1.ship_to = u1.ship_to_code and u1.ucn > 0
and not exists (select * from goadmin a2, ucnxref u2 where 
	a2.SHIP_TO = u2.ship_to_code and u2.ucn = u1.ucn and (a2.admin_user_id < a1.admin_user_id or (a2.admin_user_id = a1.admin_user_id and a2.go_access_grp < a1.go_access_grp)))
group by ucn, admin_user_id;