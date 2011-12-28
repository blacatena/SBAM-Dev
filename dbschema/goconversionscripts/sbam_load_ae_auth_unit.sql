truncate table `sbam`.`ae_auth_unit`;

INSERT INTO `sbam`.`ae_auth_unit`(
	au_id,
	site_ucn,
	site_ucn_suffix,
	site_loc_code,
	bill_ucn,
	bill_ucn_suffix,
	site_parent_ucn,
	site_parent_ucn_suffix,
	created_datetime
)SELECT
	GO_AU.GO_AUTH_UNIT_ID								as au_id,
	-1													as site_ucn,				-- IFNULL(ucn_site.ucn,-1)								as site_ucn,
	-1													as site_ucn_suffix,			-- IFNULL(ucn_site.ucn_suffix,-1)						as site_ucn_suffix,
	LOWER(go_au.go_loc_code)							as site_loc_code,
	-1													as bill_ucn,				-- IFNULL(ucn_bill.ucn,-1)								as bill_ucn,
	-1													as bill_ucn_suffix,			-- IFNULL(ucn_bill.ucn_suffix, -1)						as bill_ucn_suffix,
	-1													as site_parent_ucn,			-- IFNULL(ucn_parent.ucn,-1)							as site_parent_ucn,
	-1													as site_parent_ucn_suffix,	-- IFNULL(ucn_parent.ucn_suffix, -1)					as site_parent_ucn_suffix,
	date_added											AS created_datetime
FROM
	 goglobal.goauthunit go_au
where GO_AUTH_UNIT_ID > 0;

-- Completed insert

INSERT INTO SBAM.ae_auth_unit (
	au_id,
	site_ucn,
	site_ucn_suffix
) SELECT
	GO_AUTH_UNIT_ID										as au_id, 
	ucn													as site_ucn, 
	ucn_suffix											as site_ucn_suffix
	FROM goauthunit, ucn_conversion_copy 
	WHERE old_customer_code = GOAUTHUNIT.SHIP_TO
ON DUPLICATE KEY UPDATE
	site_ucn			= ucn_conversion_copy.ucn,
	site_ucn_suffix		= ucn_conversion_copy.ucn_suffix
;

INSERT INTO SBAM.ae_auth_unit (
	au_id,
	bill_ucn,
	bill_ucn_suffix
) SELECT
	GO_AUTH_UNIT_ID										as au_id, 
	ucn													as bill_ucn, 
	ucn_suffix											as bill_ucn_suffix
	FROM goauthunit, ucn_conversion_copy 
	WHERE old_customer_code = GOAUTHUNIT.SOLD_TO
ON DUPLICATE KEY UPDATE
	bill_ucn			= ucn_conversion_copy.ucn,
	bill_ucn_suffix		= ucn_conversion_copy.ucn_suffix
;

INSERT INTO SBAM.ae_auth_unit (
	au_id,
	site_parent_ucn,
	site_parent_ucn_suffix
) SELECT
	GO_AUTH_UNIT_ID										as au_id, 
	ucn													as site_parent_ucn, 
	ucn_suffix											as site_parent_ucn_suffix
	FROM goauthunit, ucn_conversion_copy 
	WHERE old_customer_code = GOAUTHUNIT.PARENT
ON DUPLICATE KEY UPDATE
	site_parent_ucn				= ucn_conversion_copy.ucn,
	site_parent_ucn_suffix		= ucn_conversion_copy.ucn_suffix
;

