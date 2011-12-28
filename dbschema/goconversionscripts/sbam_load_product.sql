INSERT INTO `sbam`.`product`(
	product_code,
	description,
	short_name,
	created_datetime,
	default_term_type,
	`STATUS`
)SELECT
	lower(GO_product)AS product_code,
	tcase(description)AS description,
	tcase(brief_name)AS short_name,
	date_added AS created_datetime,
	lower(def_service_type) AS default_term_type,
	CASE active
WHEN 'Y' THEN
	'A'
ELSE
	'I'
END AS `STATUS`
FROM
	`goproduct`;

