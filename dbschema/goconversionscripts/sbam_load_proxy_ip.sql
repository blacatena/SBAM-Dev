truncate table `sbam`.`proxy_ip`;
INSERT INTO `sbam`.`proxy_ip`(
	proxy_id,
	ip_lo,
	ip_hi,
	approved,
	note,
	created_datetime,
	`STATUS`
)SELECT
	FLOOR(goproxyip.GO_PROXY_NO / 10)					as proxy_id,
	goproxyip.ip_lo										as ip_lo,
	goproxyip.IP_HI										as ip_hi,
	lower(goproxyip.approved)							as approved,
	''													as note,
	date_added											AS created_datetime,
	'A'													AS `STATUS`
FROM
	goproxyip
where goproxyip.GO_PROXY_NO > 0

