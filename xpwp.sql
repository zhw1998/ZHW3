/*
Navicat MySQL Data Transfer

Source Server         : 1
Source Server Version : 50610
Source Host           : 127.0.0.1:3306
Source Database       : xpwp

Target Server Type    : MYSQL
Target Server Version : 50610
File Encoding         : 65001

Date: 2019-09-11 20:19:36
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for cut_files
-- ----------------------------
DROP TABLE IF EXISTS `cut_files`;
CREATE TABLE `cut_files` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_md5` varchar(40) NOT NULL,
  `file_path` varchar(255) NOT NULL COMMENT '文件实际路径',
  `cut_size` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=592 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for share_files
-- ----------------------------
DROP TABLE IF EXISTS `share_files`;
CREATE TABLE `share_files` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_id` int(11) NOT NULL,
  `down_code` varchar(255) NOT NULL,
  `over_time` datetime DEFAULT NULL COMMENT '到期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(50) NOT NULL,
  `username` varchar(50) NOT NULL,
  `mail` varchar(100) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `register_time` datetime NOT NULL,
  `login_time` datetime DEFAULT NULL,
  `headimg_path` varchar(255) DEFAULT 'http://img.zouhongwei.com/Fjkky7epOo3VQO1IykIhFdLrSbQB',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_cut_files
-- ----------------------------
DROP TABLE IF EXISTS `user_cut_files`;
CREATE TABLE `user_cut_files` (
  `cut_fid` int(11) NOT NULL,
  `user_fid` int(11) NOT NULL,
  `ordernum` int(11) NOT NULL DEFAULT '1' COMMENT '文件的组合排序 '
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_files
-- ----------------------------
DROP TABLE IF EXISTS `user_files`;
CREATE TABLE `user_files` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名  为空时代表文件夹',
  `file_path` varchar(255) DEFAULT NULL COMMENT '文件路径  每个用户都以  / 为根目录',
  `file_size` int(11) DEFAULT NULL COMMENT '用户上传文件的大小',
  `upload_size` int(11) DEFAULT NULL COMMENT '文件已上传大小',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '文件每次修改的时间',
  `file_sign` varchar(50) DEFAULT NULL COMMENT '用户每个文件的唯一标志',
  `state` int(2) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=698 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_third
-- ----------------------------
DROP TABLE IF EXISTS `user_third`;
CREATE TABLE `user_third` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) NOT NULL,
  `identity_type` varchar(255) NOT NULL COMMENT '身份类型（站内username 邮箱email 手机mobile 或者第三方的qq weibo weixin等等）',
  `identifier` varchar(255) NOT NULL COMMENT '身份唯一标识（存储唯一标识，比如账号、邮箱、手机号、第三方获取的唯一标识等）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
