/*
 Navicat MySQL Data Transfer

 Source Server         : Main
 Source Server Version : 50149
 Source Host           : localhost
 Source Database       : sbam

 Target Server Version : 50149
 File Encoding         : iso-8859-1

 Date: 12/28/2011 08:31:35 AM
*/

SET NAMES latin1;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `ae_au`
-- ----------------------------
DROP TABLE IF EXISTS `ae_au`;
CREATE TABLE `ae_au` (
  `ae_id` int(11) NOT NULL,
  `au_id` int(12) NOT NULL,
  `site_parent_code` varchar(32) NOT NULL,
  `bill_code` varchar(32) NOT NULL,
  `site_code` varchar(32) NOT NULL,
  `site_loc_code` varchar(255) NOT NULL,
  PRIMARY KEY (`ae_id`,`au_id`),
  KEY `ae_au__au_id__idx` (`au_id`,`ae_id`),
  KEY `ae_au__site_parent_code__idx` (`site_parent_code`,`ae_id`),
  KEY `ae_au__bill_code__idx` (`bill_code`,`ae_id`),
  KEY `ae_au__site_code__idx` (`site_code`,`ae_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `ae_auth_unit`
-- ----------------------------
DROP TABLE IF EXISTS `ae_auth_unit`;
CREATE TABLE `ae_auth_unit` (
  `au_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_ucn` int(11) NOT NULL,
  `site_ucn_suffix` int(11) NOT NULL,
  `site_loc_code` varchar(16) NOT NULL,
  `bill_ucn` int(11) NOT NULL,
  `bill_ucn_suffix` int(11) NOT NULL,
  `site_parent_ucn` int(11) NOT NULL,
  `site_parent_ucn_suffix` int(11) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`au_id`),
  KEY `ae_au__site_ucn__idx` (`site_ucn`,`site_ucn_suffix`,`site_loc_code`,`bill_ucn`),
  KEY `ae_au__bill_ucn__idx` (`bill_ucn`,`bill_ucn_suffix`)
) ENGINE=MyISAM AUTO_INCREMENT=512817 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `ae_conflict`
-- ----------------------------
DROP TABLE IF EXISTS `ae_conflict`;
CREATE TABLE `ae_conflict` (
  `ae_id` int(11) NOT NULL,
  `conflict_id` int(11) NOT NULL AUTO_INCREMENT,
  `conflict_type` int(11) NOT NULL,
  `conflict_msg` varchar(255) NOT NULL,
  `conflict_key` varchar(255) NOT NULL,
  `method_type` varchar(16) NOT NULL,
  `au_id` int(11) NOT NULL,
  `reference_au_id` int(11) NOT NULL,
  `ip_lo` bigint(12) NOT NULL,
  `ip_hi` bigint(12) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `url` varchar(1024) NOT NULL,
  PRIMARY KEY (`ae_id`,`conflict_id`),
  KEY `ae_conflict__ae_au_id__idx` (`ae_id`,`au_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `ae_control`
-- ----------------------------
DROP TABLE IF EXISTS `ae_control`;
CREATE TABLE `ae_control` (
  `ae_id` int(11) NOT NULL AUTO_INCREMENT,
  `as_of_date` date NOT NULL,
  `initiated_datetime` datetime NOT NULL,
  `terminated_datetime` datetime DEFAULT NULL,
  `completed_datetime` datetime DEFAULT NULL,
  `elapsed_seconds` int(11) NOT NULL,
  `count_agreements` int(11) NOT NULL,
  `count_sites` int(11) NOT NULL,
  `count_ips` int(11) NOT NULL,
  `count_uids` int(11) NOT NULL,
  `count_proxy_uids` int(11) NOT NULL,
  `count_urls` int(11) NOT NULL,
  `count_products` int(11) NOT NULL,
  `count_errors` int(11) NOT NULL,
  `ucnMode` char(1) NOT NULL DEFAULT 'a',
  `status` char(1) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ae_id`),
  KEY `ae_control__status__idx` (`status`)
) ENGINE=MyISAM AUTO_INCREMENT=138 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `ae_cst`
-- ----------------------------
DROP TABLE IF EXISTS `ae_cst`;
CREATE TABLE `ae_cst` (
  `ae_id` int(11) NOT NULL,
  `customer_code` varchar(32) NOT NULL,
  `institution_name` varchar(255) NOT NULL,
  `address_1` varchar(255) NOT NULL,
  `address_2` varchar(255) NOT NULL,
  `city` varchar(255) NOT NULL,
  `state` varchar(255) NOT NULL,
  `zip` varchar(255) NOT NULL,
  `admin_uid` varchar(255) NOT NULL,
  `admin_password` varchar(255) NOT NULL,
  `stats_group` varchar(255) NOT NULL,
  PRIMARY KEY (`ae_id`,`customer_code`),
  KEY `ae_cst__customer_code__idx` (`customer_code`,`ae_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `ae_ip`
-- ----------------------------
DROP TABLE IF EXISTS `ae_ip`;
CREATE TABLE `ae_ip` (
  `ae_id` int(11) NOT NULL,
  `au_id` int(11) NOT NULL,
  `ip` varchar(255) NOT NULL,
  `remote` char(1) NOT NULL,
  `ip_lo` bigint(12) NOT NULL,
  `ip_hi` bigint(12) NOT NULL,
  `ip_range_code` varchar(32) NOT NULL,
  PRIMARY KEY (`ae_id`,`au_id`,`ip`),
  KEY `au_ip__ip__idx` (`ip`),
  KEY `au_ip__ip_range_code__idx` (`ip_range_code`,`ip_lo`,`ip_hi`),
  KEY `au_ip__ip_range_ae__idx` (`ip_range_code`,`ae_id`),
  KEY `au_ip__au_id__idx` (`au_id`,`ae_id`,`ip`),
  KEY `au_ip__au_id_ip__idx` (`au_id`,`ip`,`ae_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `ae_pref`
-- ----------------------------
DROP TABLE IF EXISTS `ae_pref`;
CREATE TABLE `ae_pref` (
  `ae_id` int(11) NOT NULL,
  `au_id` int(11) NOT NULL,
  `pref_code` varchar(255) NOT NULL,
  `pref_value` varchar(1024) NOT NULL,
  PRIMARY KEY (`ae_id`,`au_id`,`pref_code`),
  KEY `ae_pref__au_id_pref__idx` (`au_id`,`pref_code`,`ae_id`),
  KEY `ae_pref__au_id_ae_id__idx` (`au_id`,`ae_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `ae_pref_code`
-- ----------------------------
DROP TABLE IF EXISTS `ae_pref_code`;
CREATE TABLE `ae_pref_code` (
  `ae_id` int(11) NOT NULL,
  `pref_code` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `default_value` varchar(255) NOT NULL,
  PRIMARY KEY (`ae_id`,`pref_code`),
  KEY `ae_pref_code__pref_code__idx` (`pref_code`,`ae_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `ae_puid`
-- ----------------------------
DROP TABLE IF EXISTS `ae_puid`;
CREATE TABLE `ae_puid` (
  `ae_id` int(11) NOT NULL,
  `au_id` int(11) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `remote` char(1) NOT NULL,
  `user_type` char(1) NOT NULL,
  `ip` varchar(255) NOT NULL,
  `ip_lo` bigint(20) NOT NULL,
  `ip_hi` bigint(20) NOT NULL,
  `ip_range_code` varchar(32) NOT NULL,
  PRIMARY KEY (`ae_id`,`au_id`,`user_id`,`ip`),
  KEY `ae_puid__ip_range_code__idx` (`user_id`,`ip_range_code`,`ip_lo`,`ip_hi`),
  KEY `ae_puid__ip_range_code_ae__idx` (`user_id`,`ip_range_code`,`ae_id`),
  KEY `ae_puid__au_id__idx` (`au_id`,`ae_id`,`user_id`,`ip`),
  KEY `ae_puid__au_id_uid__idx` (`au_id`,`user_id`,`ip`,`ae_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `ae_rsurl`
-- ----------------------------
DROP TABLE IF EXISTS `ae_rsurl`;
CREATE TABLE `ae_rsurl` (
  `ae_id` int(11) NOT NULL,
  `au_id` int(11) NOT NULL,
  `url` varchar(989) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL COMMENT 'Note this collates with latin1_bin, because a URL may be case sensitive!!!!',
  PRIMARY KEY (`au_id`,`url`,`ae_id`),
  KEY `ae_rsurl__au_id__idx` (`au_id`,`ae_id`,`url`),
  KEY `ae_rsurl__au_id_url__idx` (`au_id`,`url`,`ae_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `ae_uid`
-- ----------------------------
DROP TABLE IF EXISTS `ae_uid`;
CREATE TABLE `ae_uid` (
  `ae_id` int(11) NOT NULL,
  `au_id` int(11) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `remote` char(1) NOT NULL,
  `user_type` char(1) NOT NULL,
  PRIMARY KEY (`ae_id`,`au_id`,`user_id`),
  KEY `ae_uid__au_id__idx` (`au_id`,`ae_id`,`user_id`),
  KEY `ae_uid__au_id_uid__idx` (`au_id`,`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `ae_url`
-- ----------------------------
DROP TABLE IF EXISTS `ae_url`;
CREATE TABLE `ae_url` (
  `ae_id` int(11) NOT NULL,
  `au_id` int(11) NOT NULL,
  `url` varchar(989) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `remote` char(1) NOT NULL,
  PRIMARY KEY (`ae_id`,`au_id`,`url`),
  KEY `ae_url__au_id__idx` (`au_id`,`ae_id`,`url`),
  KEY `ae_url__au_id_url__idx` (`au_id`,`url`,`ae_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `agreement`
-- ----------------------------
DROP TABLE IF EXISTS `agreement`;
CREATE TABLE `agreement` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_check_digit` int(1) NOT NULL,
  `bill_ucn` int(11) NOT NULL,
  `bill_ucn_suffix` int(11) NOT NULL,
  `agreement_link_id` int(11) NOT NULL DEFAULT '0' COMMENT 'A field to link together related Agreements for organization purposes',
  `link_id_check_digit` int(11) NOT NULL COMMENT 'This version of the field is included purely for search purposes',
  `agreement_type_code` varchar(32) NOT NULL,
  `commission_code` varchar(32) NOT NULL,
  `workstations` int(11) NOT NULL,
  `buildings` int(11) NOT NULL,
  `population` int(11) NOT NULL,
  `enrollment` int(11) NOT NULL,
  `delete_reason_code` varchar(16) NOT NULL,
  `org_path` varchar(1024) NOT NULL,
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`id`),
  KEY `agreement__id_check_digit__idx` (`id_check_digit`),
  KEY `agreement__link_id__idx` (`agreement_link_id`),
  FULLTEXT KEY `agreement__note__idx` (`note`)
) ENGINE=MyISAM AUTO_INCREMENT=15013 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `agreement_contact`
-- ----------------------------
DROP TABLE IF EXISTS `agreement_contact`;
CREATE TABLE `agreement_contact` (
  `agreement_id` int(11) NOT NULL,
  `contact_id` int(11) NOT NULL,
  `renewal_contact` char(1) NOT NULL DEFAULT 'N',
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`agreement_id`,`contact_id`),
  KEY `agreement_contact__contact__idx` (`contact_id`),
  KEY `agreement_contact__agreement__idx` (`agreement_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `agreement_link`
-- ----------------------------
DROP TABLE IF EXISTS `agreement_link`;
CREATE TABLE `agreement_link` (
  `link_id` int(11) NOT NULL AUTO_INCREMENT,
  `link_id_check_digit` int(11) NOT NULL,
  `ucn` int(11) NOT NULL,
  `link_type_code` varchar(32) NOT NULL,
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`link_id`),
  KEY `agreement_link__ucn__idx` (`ucn`),
  FULLTEXT KEY `agreement_link__note__idx` (`note`)
) ENGINE=MyISAM AUTO_INCREMENT=100315 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `agreement_site`
-- ----------------------------
DROP TABLE IF EXISTS `agreement_site`;
CREATE TABLE `agreement_site` (
  `agreement_id` int(11) NOT NULL,
  `site_ucn` int(11) NOT NULL,
  `site_ucn_suffix` int(11) NOT NULL,
  `site_loc_code` varchar(32) NOT NULL,
  `commission_code` varchar(32) NOT NULL,
  `org_path` varchar(1024) NOT NULL,
  `cancel_reason_code` varchar(32) NOT NULL,
  `active_date` date DEFAULT NULL,
  `inactive_date` date DEFAULT NULL,
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL,
  PRIMARY KEY (`agreement_id`,`site_ucn`,`site_ucn_suffix`,`site_loc_code`),
  KEY `agreement_site__id_ucn__idx` (`agreement_id`,`site_ucn`,`site_ucn_suffix`,`site_loc_code`),
  KEY `agreement_site__ucn__idx` (`site_ucn`,`site_ucn_suffix`,`site_loc_code`),
  FULLTEXT KEY `agreement_site__note__idx` (`note`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Identifies the sites associated with a service agreement. ';

-- ----------------------------
--  Table structure for `agreement_term`
-- ----------------------------
DROP TABLE IF EXISTS `agreement_term`;
CREATE TABLE `agreement_term` (
  `agreement_id` int(11) NOT NULL,
  `term_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_code` varchar(32) NOT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `terminate_date` date DEFAULT NULL,
  `term_type` varchar(32) NOT NULL,
  `cancel_reason_code` varchar(32) NOT NULL DEFAULT '',
  `cancel_date` date DEFAULT NULL,
  `dollar_value` decimal(12,2) NOT NULL DEFAULT '0.00',
  `workstations` int(11) NOT NULL DEFAULT '0',
  `buildings` int(11) NOT NULL DEFAULT '0',
  `population` int(11) NOT NULL DEFAULT '0',
  `enrollment` int(11) NOT NULL DEFAULT '0',
  `po_number` varchar(255) NOT NULL DEFAULT '',
  `reference_sa_id` int(11) NOT NULL DEFAULT '0',
  `commission_code` varchar(32) NOT NULL,
  `org_path` varchar(1024) NOT NULL COMMENT 'A folder hierarchy used for data organization/presentation within an agreement',
  `primary_org_path` varchar(1024) NOT NULL COMMENT 'The subset of the org path to be used for primary reporting purposes (i.e. group all terms together to determine main start/end dates)',
  `primary_term` char(1) NOT NULL DEFAULT 'N' COMMENT 'Flag (y/n) to determine if a product term, within a group of terms, represents the primary (reporting) dates for that group',
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`agreement_id`,`term_id`),
  KEY `agreement_term__agreement_id__idx` (`agreement_id`,`term_id`),
  KEY `agreement_term__start__idx` (`start_date`),
  KEY `agreement_term__end__idx` (`end_date`),
  KEY `agreement_term__terminate__idx` (`terminate_date`),
  KEY `agreement_term__product__idx` (`product_code`),
  FULLTEXT KEY `agreement_term__note__idx` (`note`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `agreement_type`
-- ----------------------------
DROP TABLE IF EXISTS `agreement_type`;
CREATE TABLE `agreement_type` (
  `agreement_type_code` varchar(32) NOT NULL,
  `description` varchar(255) NOT NULL,
  `short_name` varchar(31) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`agreement_type_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `alien_institution`
-- ----------------------------
DROP TABLE IF EXISTS `alien_institution`;
CREATE TABLE `alien_institution` (
  `ucn` int(11) NOT NULL,
  `parent_ucn` int(11) NOT NULL,
  `institution_name` varchar(255) NOT NULL,
  `address1` varchar(255) NOT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `address3` varchar(255) DEFAULT NULL,
  `city` varchar(255) NOT NULL,
  `state` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `country` varchar(255) NOT NULL,
  `web_url` varchar(1024) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `fax` varchar(255) DEFAULT NULL,
  `mail_address1` varchar(255) DEFAULT NULL,
  `mail_address2` varchar(255) DEFAULT NULL,
  `mail_address3` varchar(255) DEFAULT NULL,
  `mail_city` varchar(255) DEFAULT NULL,
  `mail_state` varchar(255) DEFAULT NULL,
  `mail_zip` varchar(255) DEFAULT NULL,
  `mail_country` varchar(255) DEFAULT NULL,
  `type_code` varchar(16) NOT NULL,
  `group_code` varchar(16) NOT NULL,
  `public_private_code` varchar(16) NOT NULL,
  `alternate_ids` varchar(255) NOT NULL,
  `source` varchar(16) NOT NULL DEFAULT 'TMS',
  `created_date` date DEFAULT NULL,
  `closed_date` date DEFAULT NULL,
  `status` char(1) NOT NULL,
  KEY `alien_institution__parent__idx` (`parent_ucn`,`ucn`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='This table holds institutions with invalid UCNs';

-- ----------------------------
--  Table structure for `auth_ip`
-- ----------------------------
DROP TABLE IF EXISTS `auth_ip`;
CREATE TABLE `auth_ip` (
  `agreement_id` int(11) NOT NULL,
  `site_ucn` int(11) NOT NULL DEFAULT '0',
  `site_ucn_suffix` int(11) NOT NULL,
  `site_loc_code` varchar(32) NOT NULL,
  `ip_lo` bigint(12) NOT NULL,
  `ip_hi` bigint(12) NOT NULL,
  `remote` char(1) NOT NULL DEFAULT 'N',
  `approved` char(1) NOT NULL DEFAULT 'N',
  `validated` char(1) NOT NULL DEFAULT 'N',
  `activated` char(1) NOT NULL,
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_datetime` timestamp NULL DEFAULT NULL,
  `activated_datetime` datetime NOT NULL,
  `deactivated_datetime` datetime NOT NULL,
  `reactivated_datetime` datetime NOT NULL,
  PRIMARY KEY (`agreement_id`,`site_ucn`,`site_ucn_suffix`,`site_loc_code`,`ip_lo`),
  KEY `auth_ip__ip__idx` (`ip_lo`,`ip_hi`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `auth_method`
-- ----------------------------
DROP TABLE IF EXISTS `auth_method`;
CREATE TABLE `auth_method` (
  `agreement_id` int(11) NOT NULL,
  `ucn` int(11) NOT NULL,
  `ucn_suffix` int(11) NOT NULL,
  `site_loc_code` varchar(32) NOT NULL,
  `method_type` varchar(8) NOT NULL,
  `method_key` int(11) NOT NULL AUTO_INCREMENT,
  `for_ucn` int(11) NOT NULL,
  `for_ucn_suffix` int(11) NOT NULL,
  `for_site_loc_code` varchar(32) NOT NULL,
  `url` varchar(512) NOT NULL DEFAULT '',
  `user_id` varchar(255) NOT NULL DEFAULT '',
  `ip_lo` bigint(12) NOT NULL DEFAULT '0',
  `ip_hi` bigint(12) NOT NULL DEFAULT '0',
  `ip_range_code` varchar(16) NOT NULL,
  `password` varchar(255) NOT NULL DEFAULT '',
  `proxy_id` int(11) NOT NULL DEFAULT '0',
  `user_type` char(1) NOT NULL DEFAULT 'P',
  `remote` char(1) NOT NULL DEFAULT 'N',
  `approved` char(1) NOT NULL DEFAULT 'N',
  `validated` char(1) NOT NULL DEFAULT 'N',
  `activated` char(1) NOT NULL DEFAULT 'N',
  `org_path` varchar(1024) NOT NULL,
  `note` varchar(4000) NOT NULL,
  `activated_datetime` datetime DEFAULT NULL,
  `deactivated_datetime` datetime DEFAULT NULL,
  `reactivated_datetime` datetime DEFAULT NULL,
  `updated_datetime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`agreement_id`,`ucn`,`ucn_suffix`,`site_loc_code`,`method_type`,`method_key`),
  KEY `auth_method__ip__idx` (`ip_lo`,`ip_hi`),
  KEY `auth_method__method_key__idx` (`method_type`,`method_key`),
  KEY `auth_method__agreement__idx` (`agreement_id`,`ucn`,`ucn_suffix`,`site_loc_code`),
  KEY `auth_method__ucn__idx` (`ucn`,`ucn_suffix`,`site_loc_code`,`agreement_id`),
  KEY `auth_method_ag_type__idx` (`agreement_id`,`method_type`,`method_key`),
  KEY `auth_method__for__idx` (`for_ucn`,`for_ucn_suffix`,`for_site_loc_code`,`agreement_id`),
  KEY `auth_method__ip_range__idx` (`ip_range_code`),
  KEY `auth_method__url__idx` (`url`),
  KEY `auth_method__uid__idx` (`user_id`,`password`),
  FULLTEXT KEY `auth_method__note__idx` (`note`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `auth_url`
-- ----------------------------
DROP TABLE IF EXISTS `auth_url`;
CREATE TABLE `auth_url` (
  `agreement_id` int(11) NOT NULL,
  `ucn` int(11) NOT NULL,
  `ucn_suffix` int(11) NOT NULL,
  `site_loc_code` varchar(32) NOT NULL,
  `url` varchar(900) NOT NULL,
  `proxy_id` int(11) NOT NULL,
  `remote` char(1) NOT NULL,
  `approved` char(1) NOT NULL,
  `activated` char(1) NOT NULL,
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_datetime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `activated_datetime` datetime NOT NULL,
  `deactivated_datetime` datetime NOT NULL,
  `reactivated_datetime` datetime NOT NULL,
  PRIMARY KEY (`agreement_id`,`ucn`,`ucn_suffix`,`site_loc_code`,`url`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `auth_user_id`
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_id`;
CREATE TABLE `auth_user_id` (
  `agreement_id` int(11) NOT NULL,
  `ucn` int(11) NOT NULL,
  `ucn_suffix` int(11) NOT NULL,
  `site_loc_code` varchar(32) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `proxy_id` int(11) NOT NULL,
  `user_type` char(1) NOT NULL DEFAULT 'P',
  `remote` char(1) NOT NULL,
  `approved` char(1) NOT NULL,
  `activated` char(1) NOT NULL,
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_datetime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `activated_datetime` datetime NOT NULL,
  `deactivated_datetime` datetime NOT NULL,
  `reactivated_datetime` datetime NOT NULL,
  PRIMARY KEY (`agreement_id`,`ucn`,`ucn_suffix`,`site_loc_code`,`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `cancel_reason`
-- ----------------------------
DROP TABLE IF EXISTS `cancel_reason`;
CREATE TABLE `cancel_reason` (
  `cancel_reason_code` varchar(32) NOT NULL,
  `description` varchar(255) NOT NULL,
  `change_not_cancel` char(1) NOT NULL DEFAULT 'N',
  `status` char(1) NOT NULL DEFAULT 'A',
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`cancel_reason_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `commission_type`
-- ----------------------------
DROP TABLE IF EXISTS `commission_type`;
CREATE TABLE `commission_type` (
  `commission_code` varchar(32) NOT NULL,
  `description` varchar(255) NOT NULL,
  `short_name` varchar(64) NOT NULL,
  `products` char(1) NOT NULL,
  `sites` char(1) NOT NULL,
  `agreements` char(1) NOT NULL,
  `agreement_terms` char(1) NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`commission_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `contact`
-- ----------------------------
DROP TABLE IF EXISTS `contact`;
CREATE TABLE `contact` (
  `contact_id` int(11) NOT NULL AUTO_INCREMENT,
  `contact_type_code` varchar(32) NOT NULL,
  `parent_ucn` int(11) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `additional_info` varchar(255) NOT NULL,
  `address1` varchar(255) NOT NULL,
  `address2` varchar(255) NOT NULL,
  `address3` varchar(255) NOT NULL,
  `city` varchar(255) NOT NULL,
  `state` varchar(255) NOT NULL,
  `zip` varchar(63) NOT NULL,
  `country` varchar(255) NOT NULL,
  `e_mail` varchar(255) NOT NULL,
  `e_mail_2` varchar(255) NOT NULL,
  `phone` varchar(63) NOT NULL,
  `phone_2` varchar(63) NOT NULL,
  `fax` varchar(63) NOT NULL,
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`contact_id`),
  KEY `contact__ucn__idx` (`parent_ucn`),
  FULLTEXT KEY `contact__full_text__idx` (`full_name`,`address1`,`city`,`zip`),
  FULLTEXT KEY `contact__full_text_2__idx` (`e_mail`,`e_mail_2`,`phone`,`phone_2`,`fax`),
  FULLTEXT KEY `contact__note__idx` (`note`)
) ENGINE=MyISAM AUTO_INCREMENT=26203 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `contact_type`
-- ----------------------------
DROP TABLE IF EXISTS `contact_type`;
CREATE TABLE `contact_type` (
  `contact_type_code` varchar(32) NOT NULL,
  `description` varchar(255) NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`contact_type_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `delete_reason`
-- ----------------------------
DROP TABLE IF EXISTS `delete_reason`;
CREATE TABLE `delete_reason` (
  `delete_reason_code` varchar(32) NOT NULL,
  `description` varchar(255) NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`delete_reason_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `documentation`
-- ----------------------------
DROP TABLE IF EXISTS `documentation`;
CREATE TABLE `documentation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `seq` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `types` varchar(255) NOT NULL,
  `link` varchar(1024) NOT NULL,
  `icon_image` varchar(1024) NOT NULL,
  `doc_version` varchar(32) NOT NULL,
  `description` varchar(4000) NOT NULL,
  `updated_datetime` datetime NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `help_text`
-- ----------------------------
DROP TABLE IF EXISTS `help_text`;
CREATE TABLE `help_text` (
  `id` varchar(32) NOT NULL,
  `parent_id` varchar(32) NOT NULL,
  `first_child_id` varchar(32) NOT NULL,
  `prev_sibling_id` varchar(32) NOT NULL,
  `next_sibling_id` varchar(32) NOT NULL,
  `title` varchar(255) NOT NULL,
  `icon_name` varchar(255) NOT NULL,
  `text` mediumtext NOT NULL,
  `related_ids` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `help_text__parent__idx` (`parent_id`),
  FULLTEXT KEY `help_text__text__idx` (`title`,`text`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `institution`
-- ----------------------------
DROP TABLE IF EXISTS `institution`;
CREATE TABLE `institution` (
  `ucn` int(11) NOT NULL,
  `parent_ucn` int(11) NOT NULL,
  `institution_name` varchar(255) NOT NULL,
  `address1` varchar(255) NOT NULL,
  `address2` varchar(255) NOT NULL DEFAULT '',
  `address3` varchar(255) NOT NULL DEFAULT '',
  `city` varchar(255) NOT NULL,
  `state` varchar(255) NOT NULL DEFAULT '',
  `zip` varchar(255) NOT NULL DEFAULT '',
  `country` varchar(255) NOT NULL,
  `web_url` varchar(1024) DEFAULT NULL,
  `phone` varchar(255) NOT NULL DEFAULT '',
  `fax` varchar(255) NOT NULL DEFAULT '',
  `mail_address1` varchar(255) DEFAULT NULL,
  `mail_address2` varchar(255) DEFAULT NULL,
  `mail_address3` varchar(255) DEFAULT NULL,
  `mail_city` varchar(255) DEFAULT NULL,
  `mail_state` varchar(255) DEFAULT NULL,
  `mail_zip` varchar(255) DEFAULT NULL,
  `mail_country` varchar(255) DEFAULT NULL,
  `type_code` varchar(16) NOT NULL,
  `group_code` varchar(16) NOT NULL,
  `public_private_code` varchar(16) NOT NULL,
  `alternate_ids` varchar(255) NOT NULL,
  `source` varchar(16) NOT NULL DEFAULT 'TMS',
  `created_date` date DEFAULT NULL,
  `closed_date` date DEFAULT NULL,
  `status` char(1) NOT NULL,
  PRIMARY KEY (`ucn`),
  KEY `institution__parent__idx` (`parent_ucn`,`ucn`),
  FULLTEXT KEY `institution__fulltext__idx` (`institution_name`,`address1`,`address2`,`address3`,`city`,`state`,`zip`,`country`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `institution_contact`
-- ----------------------------
DROP TABLE IF EXISTS `institution_contact`;
CREATE TABLE `institution_contact` (
  `ucn` int(11) NOT NULL,
  `contact_id` int(11) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`ucn`,`contact_id`),
  KEY `inst_contact__contact__idx` (`contact_id`),
  KEY `inst_contact__ucn__idx` (`ucn`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `institution_country`
-- ----------------------------
DROP TABLE IF EXISTS `institution_country`;
CREATE TABLE `institution_country` (
  `country_code` varchar(32) COLLATE latin1_bin NOT NULL,
  `description` varchar(255) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`country_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `institution_group`
-- ----------------------------
DROP TABLE IF EXISTS `institution_group`;
CREATE TABLE `institution_group` (
  `group_code` varchar(16) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`group_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `institution_pub_priv`
-- ----------------------------
DROP TABLE IF EXISTS `institution_pub_priv`;
CREATE TABLE `institution_pub_priv` (
  `pub_priv_code` varchar(16) NOT NULL,
  `short_name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`pub_priv_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `institution_state`
-- ----------------------------
DROP TABLE IF EXISTS `institution_state`;
CREATE TABLE `institution_state` (
  `state_code` varchar(32) COLLATE latin1_bin NOT NULL,
  `description` varchar(255) COLLATE latin1_bin NOT NULL,
  `country_code` varchar(32) COLLATE latin1_bin NOT NULL DEFAULT '001',
  PRIMARY KEY (`state_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `institution_type`
-- ----------------------------
DROP TABLE IF EXISTS `institution_type`;
CREATE TABLE `institution_type` (
  `type_code` varchar(16) NOT NULL,
  `description` varchar(255) NOT NULL,
  `long_description` varchar(4000) NOT NULL,
  `group_code` varchar(16) NOT NULL,
  PRIMARY KEY (`type_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `link_type`
-- ----------------------------
DROP TABLE IF EXISTS `link_type`;
CREATE TABLE `link_type` (
  `link_type_code` varchar(32) NOT NULL,
  `description` varchar(255) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`link_type_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `preference_category`
-- ----------------------------
DROP TABLE IF EXISTS `preference_category`;
CREATE TABLE `preference_category` (
  `pref_cat_code` varchar(32) NOT NULL,
  `description` varchar(255) NOT NULL,
  `seq` int(11) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`pref_cat_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `preference_code`
-- ----------------------------
DROP TABLE IF EXISTS `preference_code`;
CREATE TABLE `preference_code` (
  `pref_cat_code` varchar(32) NOT NULL,
  `pref_sel_code` varchar(32) NOT NULL,
  `description` varchar(255) NOT NULL,
  `seq` int(11) NOT NULL,
  `export_value` varchar(255) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`pref_cat_code`,`pref_sel_code`),
  KEY `prev_val__pref_code__idx` (`pref_sel_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `product`
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `product_code` varchar(32) NOT NULL,
  `description` varchar(255) NOT NULL,
  `short_name` varchar(32) NOT NULL,
  `default_term_type` varchar(32) DEFAULT NULL,
  `default_commission_code` varchar(32) NOT NULL,
  `org_path` varchar(1024) NOT NULL,
  `seq` int(11) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`product_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `product_service`
-- ----------------------------
DROP TABLE IF EXISTS `product_service`;
CREATE TABLE `product_service` (
  `product_code` varchar(32) NOT NULL,
  `service_code` varchar(32) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`product_code`,`service_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `proxy`
-- ----------------------------
DROP TABLE IF EXISTS `proxy`;
CREATE TABLE `proxy` (
  `proxy_id` int(11) NOT NULL AUTO_INCREMENT,
  `id_check_digit` int(11) NOT NULL,
  `description` varchar(255) NOT NULL,
  `search_keys` varchar(255) NOT NULL,
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`proxy_id`),
  KEY `proxy__id_cd__idx` (`id_check_digit`),
  FULLTEXT KEY `proxy__text__idx` (`description`,`search_keys`)
) ENGINE=MyISAM AUTO_INCREMENT=2930 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `proxy_ip`
-- ----------------------------
DROP TABLE IF EXISTS `proxy_ip`;
CREATE TABLE `proxy_ip` (
  `proxy_id` int(11) NOT NULL,
  `ip_id` int(11) NOT NULL AUTO_INCREMENT,
  `ip_lo` bigint(12) NOT NULL,
  `ip_hi` bigint(12) NOT NULL,
  `ip_range_code` varchar(16) NOT NULL,
  `approved` char(1) NOT NULL,
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`proxy_id`,`ip_id`),
  KEY `proxy_ip__ip__idx` (`ip_lo`,`ip_hi`,`proxy_id`),
  KEY `proxy_ip__id__idx` (`proxy_id`,`ip_id`),
  KEY `proxy_ip__ip_range__idx` (`ip_range_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `remote_setup_url`
-- ----------------------------
DROP TABLE IF EXISTS `remote_setup_url`;
CREATE TABLE `remote_setup_url` (
  `agreement_id` int(11) NOT NULL,
  `ucn` int(11) NOT NULL,
  `ucn_suffix` int(11) NOT NULL,
  `site_loc_code` varchar(32) NOT NULL,
  `url_id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(512) NOT NULL,
  `for_ucn` int(11) NOT NULL,
  `for_ucn_suffix` int(11) NOT NULL,
  `for_site_loc_code` varchar(32) NOT NULL,
  `approved` char(1) NOT NULL DEFAULT 'N',
  `activated` char(1) NOT NULL DEFAULT 'N',
  `note` varchar(4000) NOT NULL,
  `org_path` varchar(1024) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`agreement_id`,`ucn`,`ucn_suffix`,`site_loc_code`,`url_id`),
  KEY `remote_url__ucn__idx` (`ucn`,`ucn_suffix`,`site_loc_code`),
  KEY `remote_url__url__idx` (`url`),
  KEY `remote_url__agreement__idx` (`agreement_id`),
  FULLTEXT KEY `remote_url__note__idx` (`note`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `service`
-- ----------------------------
DROP TABLE IF EXISTS `service`;
CREATE TABLE `service` (
  `service_code` varchar(32) NOT NULL,
  `description` varchar(255) NOT NULL,
  `service_type` char(1) NOT NULL DEFAULT 'P' COMMENT 'P=primary, S=secondary/addon',
  `export_value` varchar(32) NOT NULL,
  `export_file` varchar(255) NOT NULL,
  `presentation_path` varchar(1024) NOT NULL,
  `seq` int(11) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`service_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `site`
-- ----------------------------
DROP TABLE IF EXISTS `site`;
CREATE TABLE `site` (
  `ucn` int(11) NOT NULL,
  `ucn_suffix` int(11) NOT NULL,
  `site_loc_code` varchar(32) NOT NULL,
  `description` varchar(255) NOT NULL,
  `commission_code` varchar(32) NOT NULL,
  `pseudo_site` char(1) NOT NULL,
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL,
  PRIMARY KEY (`ucn`,`ucn_suffix`,`site_loc_code`),
  KEY `site__ucn__idx` (`ucn`,`ucn_suffix`,`site_loc_code`),
  FULLTEXT KEY `site__note__idx` (`note`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `site_contact`
-- ----------------------------
DROP TABLE IF EXISTS `site_contact`;
CREATE TABLE `site_contact` (
  `ucn` int(11) NOT NULL,
  `contact_id` int(11) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`ucn`,`contact_id`),
  KEY `site_contact__contact__idx` (`contact_id`),
  KEY `site_contact__ucn__idx` (`ucn`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `site_preference`
-- ----------------------------
DROP TABLE IF EXISTS `site_preference`;
CREATE TABLE `site_preference` (
  `ucn` int(11) NOT NULL,
  `ucn_suffix` int(11) NOT NULL,
  `site_loc_code` varchar(32) NOT NULL,
  `pref_cat_code` varchar(32) NOT NULL,
  `pref_sel_code` varchar(1024) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL,
  PRIMARY KEY (`ucn`,`ucn_suffix`,`site_loc_code`,`pref_cat_code`),
  KEY `site_pref__ucn__idx` (`ucn`,`ucn_suffix`,`site_loc_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `snapshot`
-- ----------------------------
DROP TABLE IF EXISTS `snapshot`;
CREATE TABLE `snapshot` (
  `snapshot_id` int(11) NOT NULL AUTO_INCREMENT,
  `snapshot_name` varchar(255) NOT NULL,
  `snapshot_type` varchar(16) NOT NULL,
  `product_service_type` char(1) NOT NULL,
  `ucn_type` char(1) NOT NULL DEFAULT 'b',
  `snapshot_taken` datetime DEFAULT NULL,
  `snapshot_rows` int(11) NOT NULL,
  `excel_filename` varchar(255) DEFAULT NULL,
  `create_user_id` int(11) NOT NULL,
  `expire_datetime` datetime DEFAULT NULL,
  `seq` int(11) NOT NULL,
  `org_path` varchar(1024) NOT NULL,
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`snapshot_id`)
) ENGINE=MyISAM AUTO_INCREMENT=48 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `snapshot_parameter`
-- ----------------------------
DROP TABLE IF EXISTS `snapshot_parameter`;
CREATE TABLE `snapshot_parameter` (
  `snapshot_id` int(11) NOT NULL,
  `parameter_name` varchar(32) NOT NULL,
  `value_id` int(11) NOT NULL,
  `parameter_source` varchar(32) NOT NULL,
  `parameter_group` varchar(32) NOT NULL,
  `parameter_type` int(11) NOT NULL,
  `int_to_value` int(11) DEFAULT NULL,
  `int_from_value` int(11) DEFAULT NULL,
  `str_from_value` varchar(1024) DEFAULT NULL,
  `str_to_value` varchar(1024) DEFAULT NULL,
  `date_from_value` date DEFAULT NULL,
  `date_to_value` date DEFAULT NULL,
  `dbl_from_value` double DEFAULT NULL,
  `dbl_to_value` double DEFAULT NULL,
  PRIMARY KEY (`snapshot_id`,`parameter_name`,`value_id`),
  KEY `snapshot_parm__id__id` (`snapshot_id`),
  KEY `snapshot_parm__parm__idx` (`snapshot_id`,`parameter_name`),
  KEY `snapshot_parm__source__idx` (`snapshot_id`,`parameter_source`,`parameter_group`),
  KEY `snapshot_parm__group__idx` (`snapshot_id`,`parameter_group`,`parameter_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `snapshot_product_service`
-- ----------------------------
DROP TABLE IF EXISTS `snapshot_product_service`;
CREATE TABLE `snapshot_product_service` (
  `snapshot_id` int(11) NOT NULL,
  `product_service_type` char(1) NOT NULL,
  `product_service_code` varchar(32) NOT NULL,
  PRIMARY KEY (`snapshot_id`,`product_service_type`,`product_service_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `snapshot_term_data`
-- ----------------------------
DROP TABLE IF EXISTS `snapshot_term_data`;
CREATE TABLE `snapshot_term_data` (
  `snapshot_id` int(11) NOT NULL,
  `agreement_id` int(11) NOT NULL,
  `term_id` int(11) NOT NULL,
  `product_code` varchar(32) NOT NULL,
  `service_code` varchar(32) NOT NULL,
  `ucn` int(11) NOT NULL,
  `ucn_suffix` int(11) NOT NULL,
  `row_id` int(11) NOT NULL AUTO_INCREMENT,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `terminate_date` date DEFAULT NULL,
  `term_type` varchar(32) NOT NULL,
  `cancel_reason_code` varchar(32) NOT NULL DEFAULT '',
  `cancel_date` date DEFAULT NULL,
  `dollar_value` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT 'The value of the term, for the entire product ensemble and all customers',
  `workstations` int(11) NOT NULL DEFAULT '0',
  `buildings` int(11) NOT NULL DEFAULT '0',
  `population` int(11) NOT NULL DEFAULT '0',
  `enrollment` int(11) NOT NULL DEFAULT '0',
  `commission_code` varchar(32) NOT NULL,
  `primary_term` char(1) NOT NULL DEFAULT 'N' COMMENT 'Flag (y/n) to determine if a product term, within a group of terms, represents the primary (reporting) dates for that group',
  `service_fraction` decimal(10,6) NOT NULL COMMENT 'The fraction of the dollar value (0 to 1), in the case of a service within a product ensemble, for just this service (estimated)',
  `customer_fraction` decimal(10,6) NOT NULL COMMENT 'The fraction of the dollar value (0 to 1), in the event of multiple customers, prorated for just this customer (estimated)',
  PRIMARY KEY (`snapshot_id`,`agreement_id`,`term_id`,`product_code`,`service_code`,`ucn`,`ucn_suffix`,`row_id`),
  KEY `snapshot_term__ucn__idx` (`snapshot_id`,`ucn`,`ucn_suffix`),
  KEY `snapshot_term__prod_serv__idx` (`snapshot_id`,`product_code`,`ucn`,`ucn_suffix`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `stats_admin`
-- ----------------------------
DROP TABLE IF EXISTS `stats_admin`;
CREATE TABLE `stats_admin` (
  `ucn` int(11) NOT NULL,
  `admin_uid` varchar(255) NOT NULL,
  `admin_password` varchar(255) NOT NULL,
  `stats_group` varchar(255) NOT NULL,
  `note` varchar(4000) NOT NULL,
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`ucn`),
  KEY `stats_admin__username__idx` (`admin_uid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `sys_config`
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` varchar(32) NOT NULL,
  `seq` int(11) NOT NULL,
  `email_server` varchar(255) NOT NULL,
  `email_port` int(11) NOT NULL,
  `email_user` varchar(255) NOT NULL,
  `email_password` varchar(255) NOT NULL,
  `email_address` varchar(255) NOT NULL,
  `email_cc` varchar(1024) NOT NULL,
  `email_bcc` varchar(1024) NOT NULL,
  `instance_name` varchar(255) NOT NULL,
  `site_url` varchar(255) NOT NULL,
  `tech_contact_name` varchar(255) NOT NULL,
  `tech_contact_email` varchar(255) NOT NULL,
  `execution_mode` varchar(32) NOT NULL DEFAULT 'Production',
  `inst_config_inner` char(1) NOT NULL,
  `inst_config_pairs` char(1) NOT NULL,
  `inst_config_min_str` int(11) NOT NULL,
  `inst_config_min_inner` int(11) NOT NULL,
  `inst_config_max_pair` int(11) NOT NULL,
  `inst_config_max_list` int(11) NOT NULL,
  `inst_config_max_words` int(11) NOT NULL,
  `inst_config_load_limit` int(11) NOT NULL,
  `inst_config_load_watch` int(11) NOT NULL,
  `inst_config_load_gc` int(11) NOT NULL,
  `inst_config_load_status` varchar(16) NOT NULL DEFAULT 'A',
  `ae_ucn_mode` char(1) NOT NULL DEFAULT 'a',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `term_type`
-- ----------------------------
DROP TABLE IF EXISTS `term_type`;
CREATE TABLE `term_type` (
  `term_type_code` varchar(32) NOT NULL COMMENT 'Is the term Initial, Add-on, Renewal, Trial?',
  `description` varchar(255) NOT NULL,
  `activate` char(1) NOT NULL DEFAULT 'Y',
  `created_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`term_type_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Valid term types (initial, renewal, add-on, trial)';

-- ----------------------------
--  Table structure for `ucn_conversion`
-- ----------------------------
DROP TABLE IF EXISTS `ucn_conversion`;
CREATE TABLE `ucn_conversion` (
  `ucn` int(11) NOT NULL,
  `ucn_suffix` int(11) NOT NULL AUTO_INCREMENT,
  `old_customer_code` varchar(16) NOT NULL,
  PRIMARY KEY (`ucn`,`ucn_suffix`),
  UNIQUE KEY `ucn_conversion__old_code__idx` (`old_customer_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `first_name` varchar(32) DEFAULT NULL,
  `last_name` varchar(32) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `login_count` int(11) NOT NULL DEFAULT '0',
  `session_id` int(11) NOT NULL,
  `session_start_time` datetime DEFAULT '1972-01-01 00:00:00',
  `session_expire_time` datetime DEFAULT '1972-01-01 00:00:00',
  `created_datetime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user__user_name__idx` (`user_name`),
  KEY `user__session_id__idx` (`session_id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `user_cache`
-- ----------------------------
DROP TABLE IF EXISTS `user_cache`;
CREATE TABLE `user_cache` (
  `user_name` varchar(32) NOT NULL,
  `category` varchar(32) NOT NULL,
  `int_key` int(11) NOT NULL,
  `str_key` varchar(255) NOT NULL,
  `hint` varchar(1024) NOT NULL,
  `access_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_name`,`category`,`int_key`,`str_key`),
  KEY `user_cache__user__idx` (`user_name`,`category`,`int_key`,`str_key`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `user_message`
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) NOT NULL,
  `location_tag` varchar(32) NOT NULL,
  `text` text NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'A',
  `window_pos_x` int(11) NOT NULL,
  `window_pos_y` int(11) NOT NULL,
  `window_pos_z` int(11) NOT NULL,
  `window_width` int(11) NOT NULL,
  `window_height` int(11) NOT NULL,
  `restore_pos_x` int(11) NOT NULL,
  `restore_pos_y` int(11) NOT NULL,
  `restore_width` int(11) NOT NULL,
  `restore_height` int(11) NOT NULL,
  `minimized` char(1) NOT NULL,
  `maximized` char(1) NOT NULL,
  `collapsed` char(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_msg__user_name__idx` (`user_name`,`location_tag`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `user_portlet_cache`
-- ----------------------------
DROP TABLE IF EXISTS `user_portlet_cache`;
CREATE TABLE `user_portlet_cache` (
  `user_name` varchar(32) NOT NULL,
  `portlet_id` int(11) NOT NULL,
  `portlet_type` varchar(255) NOT NULL,
  `restore_column` int(11) NOT NULL,
  `restore_row` int(11) NOT NULL,
  `restore_height` int(11) NOT NULL,
  `restore_width` int(11) NOT NULL,
  `minimized` char(1) NOT NULL,
  `key_data` varchar(4000) NOT NULL,
  PRIMARY KEY (`user_name`,`portlet_id`),
  KEY `user_portlet_cache__user__idx` (`user_name`,`portlet_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `user_profile`
-- ----------------------------
DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile` (
  `user_name` varchar(32) NOT NULL,
  `tooltips` char(1) NOT NULL DEFAULT 'y',
  `recent_searches` int(11) NOT NULL DEFAULT '12',
  `recent_agreements` int(11) NOT NULL DEFAULT '-1',
  `recent_customers` int(11) NOT NULL DEFAULT '-1',
  `session_timeout_minutes` int(11) NOT NULL,
  `password_expire_days` int(11) NOT NULL,
  `restore_portlets` char(1) NOT NULL,
  `portal_dimensions` varchar(255) NOT NULL,
  PRIMARY KEY (`user_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `user_role`
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `user_name` varchar(32) NOT NULL,
  `role_name` varchar(32) NOT NULL,
  `read_or_write` int(11) NOT NULL,
  PRIMARY KEY (`user_name`,`role_name`),
  KEY `user_role__user_name__idx` (`user_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `welcome_message`
-- ----------------------------
DROP TABLE IF EXISTS `welcome_message`;
CREATE TABLE `welcome_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `post_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `title` varchar(1024) NOT NULL,
  `content` varchar(4000) NOT NULL,
  `priority` char(1) NOT NULL,
  `expire_date` datetime NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Function structure for `ip_coded`
-- ----------------------------
DROP FUNCTION IF EXISTS `ip_coded`;
delimiter ;;
CREATE DEFINER=`sbam`@`localhost` FUNCTION `ip_coded`(ip bigint(12), size bigint(12)) RETURNS varchar(32) CHARSET latin1
    NO SQL
BEGIN
	DECLARE l0, l1, l2 BIGINT;
	DECLARE c CHAR(1);
	DECLARE r VARCHAR(32);

	SET r  = '';
	SET l0 = ip;
	WHILE (l0 <> 0) DO
		SET l1 = l0 DIV size;
		SET l2 = l0 - (l1 * size);		
		SET l0 = l1;

		IF size <= 16 THEN
			CASE l2
				WHEN 0	THEN SET c = '0';
				WHEN 1	THEN SET c = '1';
				WHEN 2	THEN SET c = '2';
				WHEN 3	THEN SET c = '3';
				WHEN 4	THEN SET c = '4';
				WHEN 5	THEN SET c = '5';
				WHEN 6	THEN SET c = '6';
				WHEN 7	THEN SET c = '7';
				WHEN 8	THEN SET c = '8';
				WHEN 9	THEN SET c = '9';
				WHEN 10	THEN SET c = 'A';
				WHEN 11	THEN SET c = 'B';
				WHEN 12	THEN SET c = 'C';
				WHEN 13	THEN SET c = 'D';
				WHEN 14	THEN SET c = 'E';
				WHEN 15	THEN SET c = 'F';
				ELSE         SET c = '?';
			END CASE;
			SET r = concat(c,r);
		ELSE
			SET r = concat(l2,r);
			if l0 > 0 THEN
				SET r = concat('.',r);
			END IF;
		END IF;
	END WHILE;

	RETURN r;
END
 ;;
delimiter ;

-- ----------------------------
--  Function structure for `ip_max`
-- ----------------------------
DROP FUNCTION IF EXISTS `ip_max`;
delimiter ;;
CREATE DEFINER=`sbam`@`localhost` FUNCTION `ip_max`(ip VARCHAR(255)) RETURNS bigint(12)
    NO SQL
BEGIN

	DECLARE octet_1, octet_2, octet_3, octet_4 VARCHAR(255);
	DECLARE c CHAR(1);
	DECLARE i,o INT;

	DECLARE o1,o2,o3,o4 INT;
	DECLARE r BIGINT;

--  Strip the octets from the IP address string
	
	SET i = 1;
	SET o = 1;
	SET octet_1 = '';
	SET octet_2 = '';
	SET octet_3 = '';
	SET octet_4 = '';
	WHILE i <= length(ip) DO
		SET c = SUBSTRING(ip,i,1);
		IF c = '.' THEN
			SET o = o + 1;
		ELSE
			CASE o
				WHEN 1 THEN SET octet_1 = concat(octet_1, c);
				WHEN 2 THEN SET octet_2 = concat(octet_2, c);
				WHEN 3 THEN SET octet_3 = concat(octet_3, c);
				ELSE		SET octet_4 = concat(octet_4, c);
			END CASE;
		END IF;
		SET i = i + 1;
	END WHILE;
	
-- Convert octets to an IP address

	SET o1 = octet_max(octet_1);
	SET o2 = octet_max(octet_2);
	SET o3 = octet_max(octet_3);
	SET o4 = octet_max(octet_4);
	SET r = o1;
	SET r = o2 + (r * 256);
	SET r = o3 + (r * 256);
	SET r = o4 + (r * 256);
	return r;
END
 ;;
delimiter ;

-- ----------------------------
--  Function structure for `ip_min`
-- ----------------------------
DROP FUNCTION IF EXISTS `ip_min`;
delimiter ;;
CREATE DEFINER=`sbam`@`localhost` FUNCTION `ip_min`(ip VARCHAR(255)) RETURNS bigint(12)
    NO SQL
    DETERMINISTIC
BEGIN

	DECLARE octet_1, octet_2, octet_3, octet_4 VARCHAR(255);
	DECLARE c CHAR(1);
	DECLARE i,o INT;

	DECLARE o1,o2,o3,o4 INT;
	DECLARE r BIGINT;

--  Strip the octets from the IP address string
	
	SET i = 1;
	SET o = 1;
	SET octet_1 = '';
	SET octet_2 = '';
	SET octet_3 = '';
	SET octet_4 = '';
	WHILE i <= length(ip) DO
		SET c = SUBSTRING(ip,i,1);
		IF c = '.' THEN
			SET o = o + 1;
		ELSE
			CASE o
				WHEN 1 THEN SET octet_1 = concat(octet_1, c);
				WHEN 2 THEN SET octet_2 = concat(octet_2, c);
				WHEN 3 THEN SET octet_3 = concat(octet_3, c);
				ELSE		SET octet_4 = concat(octet_4, c);
			END CASE;
		END IF;
		SET i = i + 1;
	END WHILE;
	
-- Convert octets to an IP address

	SET o1 = octet_min(octet_1);
	SET o2 = octet_min(octet_2);
	SET o3 = octet_min(octet_3);
	SET o4 = octet_min(octet_4);
	SET r = o1;
	SET r = o2 + (r * 256);
	SET r = o3 + (r * 256);
	SET r = o4 + (r * 256);
	return r;
END
 ;;
delimiter ;

-- ----------------------------
--  Function structure for `ip_range_coded`
-- ----------------------------
DROP FUNCTION IF EXISTS `ip_range_coded`;
delimiter ;;
CREATE DEFINER=`sbam`@`localhost` FUNCTION `ip_range_coded`(ip_lo bigint(12), ip_hi bigint(12),size bigint(12)) RETURNS varchar(255) CHARSET latin1
    NO SQL
BEGIN
	DECLARE l0, l1, l2 BIGINT;
	DECLARE h0, h1, h2 BIGINT;
	DECLARE c CHAR(1);
	DECLARE rl,rh,r VARCHAR(32);
	DECLARE i,p INT;

	IF size <= 16 THEN
		SET p = 1;
	ELSEIF size < 100 THEN
		SET p = 2;
	ELSE
		SET p = 3;
	END IF;

	SET r  = '';
	SET rl = '';
	SET rh = '';
	SET l0 = ip_lo;
	SET h0 = ip_hi;
	WHILE l0 <> 0 AND h0 <> 0 DO
		SET l1 = l0 DIV size;
		SET l2 = l0 - (l1 * size);		
		SET l0 = l1;

		SET h1 = h0 DIV size;
		SET h2 = h0 - (h1 * size);		
		SET h0 = h1;

		IF size <= 16 THEN
			CASE l2
				WHEN 0	THEN SET c = '0';
				WHEN 1	THEN SET c = '1';
				WHEN 2	THEN SET c = '2';
				WHEN 3	THEN SET c = '3';
				WHEN 4	THEN SET c = '4';
				WHEN 5	THEN SET c = '5';
				WHEN 6	THEN SET c = '6';
				WHEN 7	THEN SET c = '7';
				WHEN 8	THEN SET c = '8';
				WHEN 9	THEN SET c = '9';
				WHEN 10	THEN SET c = 'A';
				WHEN 11	THEN SET c = 'B';
				WHEN 12	THEN SET c = 'C';
				WHEN 13	THEN SET c = 'D';
				WHEN 14	THEN SET c = 'E';
				WHEN 15	THEN SET c = 'F';
				ELSE         SET c = '?';
			END CASE;
			SET rl = concat(c,rl);
		ELSE
			SET rl = concat(lpad(l2,p,'0'),rl);
			if l0 > 0 THEN
				SET rl = concat('.',rl);
			END IF;
		END IF;

		IF size <= 16 THEN
			CASE h2
				WHEN 0	THEN SET c = '0';
				WHEN 1	THEN SET c = '1';
				WHEN 2	THEN SET c = '2';
				WHEN 3	THEN SET c = '3';
				WHEN 4	THEN SET c = '4';
				WHEN 5	THEN SET c = '5';
				WHEN 6	THEN SET c = '6';
				WHEN 7	THEN SET c = '7';
				WHEN 8	THEN SET c = '8';
				WHEN 9	THEN SET c = '9';
				WHEN 10	THEN SET c = 'A';
				WHEN 11	THEN SET c = 'B';
				WHEN 12	THEN SET c = 'C';
				WHEN 13	THEN SET c = 'D';
				WHEN 14	THEN SET c = 'E';
				WHEN 15	THEN SET c = 'F';
				ELSE         SET c = '?';
			END CASE;
			SET rh = concat(c,rh);
		ELSE
			SET rh = concat(lpad(h2,p,'0'),rh);
			if h0 > 0 THEN
				SET rh = concat('.',rh);
			END IF;
		END IF;
	END WHILE;

	IF p > 1 THEN
		SET p = p + 1;
	END IF;

	SET i = 1;
	WHILE i < LENGTH(rl) AND SUBSTRING(rl,i,1) = SUBSTRING(rh,i,1) DO
		SET i = i + 1;
	END WHILE;
	IF i > LENGTH(rl) THEN
		SET i = LENGTH(rl);
	END IF;
	WHILE i > 0 AND (SUBSTRING(rl,i,1) = '.' OR SUBSTRING(rl,i,1) <> SUBSTRING(rh,i,1)) DO
		SET i = i - 1;
	END WHILE;

	RETURN SUBSTRING(rl,1,i);
END
 ;;
delimiter ;

-- ----------------------------
--  Function structure for `octet_max`
-- ----------------------------
DROP FUNCTION IF EXISTS `octet_max`;
delimiter ;;
CREATE DEFINER=`sbam`@`localhost` FUNCTION `octet_max`(octet varchar(255)) RETURNS int(11)
    NO SQL
BEGIN
	DECLARE r INT;
	IF octet IS NULL THEN
		SET r = 255;
	ELSEIF LENGTH(TRIM(octet)) = 0 THEN
		SET r = 255;
	ELSEIF octet = '*' THEN
		SET r = 255;
	ELSE
		SET r = octet;
		IF r > 255 THEN
			SET r = 255;
		END IF;
		IF r < 0 THEN
			SET r = 0;
		END IF;
	END IF;
	RETURN r;
END
 ;;
delimiter ;

-- ----------------------------
--  Function structure for `octet_min`
-- ----------------------------
DROP FUNCTION IF EXISTS `octet_min`;
delimiter ;;
CREATE DEFINER=`sbam`@`localhost` FUNCTION `octet_min`(octet varchar(255)) RETURNS int(11)
    NO SQL
BEGIN
	DECLARE r INT;
	IF octet IS NULL THEN
		SET r = 0;
	ELSEIF octet = '*' THEN
		SET r = 0;
	ELSE
		SET r = octet;
		IF r > 255 THEN
			SET r = 255;
		END IF;
		IF r < 0 THEN
			SET r = 0;
		END IF;
	END IF;
	RETURN r;
END
 ;;
delimiter ;

