INSERT INTO `sbam`.`product_service`(
	product_code,
	service_code,
	created_datetime,
	`STATUS`
)SELECT
	lower(GO_product)AS product_code,
	lower(GO_server)AS service_code,
	date_added AS created_datetime,
	'A' AS `STATUS`
FROM
	`goserver`;

