/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80024
 Source Host           : localhost:3306
 Source Schema         : nobody_place

 Target Server Type    : MySQL
 Target Server Version : 80024
 File Encoding         : 65001

 Date: 04/02/2022 16:43:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for info_common_storage
-- ----------------------------
DROP TABLE IF EXISTS `info_common_storage`;
CREATE TABLE `info_common_storage` (
                                       `key` varchar(20) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
                                       `value` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                       PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- ----------------------------
-- Table structure for info_count_down
-- ----------------------------
DROP TABLE IF EXISTS `info_count_down`;
CREATE TABLE `info_count_down` (
                                   `id` varchar(20) NOT NULL COMMENT '创建的时间戳',
                                   `event_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                   `expiration_date` datetime DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Table structure for info_csgo_detailed_transaction
-- ----------------------------
DROP TABLE IF EXISTS `info_csgo_detailed_transaction`;
CREATE TABLE `info_csgo_detailed_transaction` (
                                                  `item_id` int NOT NULL,
                                                  `assetid` varchar(15) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '检视编号，用于组成id',
                                                  `sold_price` float NOT NULL,
                                                  `transact_time` int NOT NULL COMMENT '交易时间',
                                                  `item_wear` varchar(30) CHARACTER SET ascii COLLATE ascii_bin DEFAULT NULL COMMENT '磨损',
                                                  `fade_percent` varchar(30) CHARACTER SET ascii COLLATE ascii_bin DEFAULT NULL COMMENT '如果是渐变皮肤，有渐变百分比',
                                                  `added_time` int unsigned DEFAULT NULL COMMENT '入数据库时间戳',
                                                  PRIMARY KEY (`item_id`,`transact_time`,`assetid`) USING BTREE,
                                                  KEY `item_id` (`item_id`),
                                                  CONSTRAINT `detailed_transaction_item_id` FOREIGN KEY (`item_id`) REFERENCES `info_csgo_item` (`item_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- ----------------------------
-- Table structure for info_csgo_income_addup
-- ----------------------------
DROP TABLE IF EXISTS `info_csgo_income_addup`;
CREATE TABLE `info_csgo_income_addup` (
                                          `overall_earning_addup` double NOT NULL COMMENT '总收益（持有收益+租赁收益+买卖收益）（冗余字段？）',
                                          `holding_earning_addup` double NOT NULL,
                                          `selling_earning_addup` double NOT NULL,
                                          `lease_earning_addup` double NOT NULL,
                                          `addup_date` int NOT NULL COMMENT '截止日期时间戳，以天为单位',
                                          PRIMARY KEY (`addup_date`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- ----------------------------
-- Table structure for info_csgo_item
-- ----------------------------
DROP TABLE IF EXISTS `info_csgo_item`;
CREATE TABLE `info_csgo_item` (
                                  `item_id` int NOT NULL COMMENT 'buff唯一id',
                                  `item_type` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '例如：AK-47',
                                  `item_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '例如：火蛇',
                                  `item_wear_type` tinyint DEFAULT NULL COMMENT '0:崭新出场；1:以此类推...',
                                  `display_url` varchar(255) CHARACTER SET ascii COLLATE ascii_bin DEFAULT NULL COMMENT '图片网络地址',
                                  `added_time` int unsigned NOT NULL,
                                  `is_stat_trak` bit(1) DEFAULT b'0' COMMENT '1：是',
                                  `desc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                  PRIMARY KEY (`item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- ----------------------------
-- Table structure for info_csgo_price_history
-- ----------------------------
DROP TABLE IF EXISTS `info_csgo_price_history`;
CREATE TABLE `info_csgo_price_history` (
                                           `transact_time` int NOT NULL,
                                           `item_id` int NOT NULL,
                                           `sold_price` float DEFAULT NULL,
                                           PRIMARY KEY (`item_id`,`transact_time`) USING BTREE,
                                           CONSTRAINT `prize_history_item_id` FOREIGN KEY (`item_id`) REFERENCES `info_csgo_item` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- ----------------------------
-- Table structure for info_csgo_user_property
-- ----------------------------
DROP TABLE IF EXISTS `info_csgo_user_property`;
CREATE TABLE `info_csgo_user_property` (
                                           `item_id` int NOT NULL,
                                           `bought_price` float DEFAULT NULL,
                                           PRIMARY KEY (`item_id`),
                                           CONSTRAINT `user_property_item_id` FOREIGN KEY (`item_id`) REFERENCES `info_csgo_item` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- ----------------------------
-- Table structure for info_csgo_user_transaction
-- ----------------------------
DROP TABLE IF EXISTS `info_csgo_user_transaction`;
CREATE TABLE `info_csgo_user_transaction` (
                                              `item_id` int NOT NULL,
                                              `transact_time` int NOT NULL COMMENT '交易时间戳，八天只能交易一次',
                                              `transact_price` float NOT NULL,
                                              `transact_type` tinyint NOT NULL COMMENT '0:租赁；1:卖出',
                                              `duration` int DEFAULT NULL COMMENT '如果是租赁，多少天',
                                              PRIMARY KEY (`item_id`,`transact_time`),
                                              CONSTRAINT `user_transaction_item_id` FOREIGN KEY (`item_id`) REFERENCES `info_csgo_item` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` int unsigned NOT NULL AUTO_INCREMENT,
                        `username` varchar(255) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

SET FOREIGN_KEY_CHECKS = 1;
