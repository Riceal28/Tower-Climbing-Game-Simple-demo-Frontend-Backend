/*
 Navicat Premium Dump SQL

 Source Server         : mysql 8.0.15
 Source Server Type    : MySQL
 Source Server Version : 80015 (8.0.15)
 Source Host           : localhost:3306
 Source Schema         : demo_tower

 Target Server Type    : MySQL
 Target Server Version : 80015 (8.0.15)
 File Encoding         : 65001

 Date: 07/04/2026 18:15:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for action_info
-- ----------------------------
DROP TABLE IF EXISTS `action_info`;
CREATE TABLE `action_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `action_id` bigint(20) UNSIGNED NOT NULL COMMENT '行为ID',
  `action_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ATTACK' COMMENT '类型: ATTACK/DEFENSE/HEAL/SPELL/BUFF',
  `action_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '行为名',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '描述',
  `target_is_for_self` tinyint(4) NOT NULL DEFAULT 0 COMMENT '作用对象是否自身,0对方,1自身',
  `for_hp` int(11) NOT NULL DEFAULT 0 COMMENT '对HP作用的数值(伤害或治疗)',
  `for_mp` int(11) NOT NULL DEFAULT 0 COMMENT '对MP作用的数值(消耗或回复)',
  `for_defend` int(11) NOT NULL DEFAULT 0 COMMENT '对格挡值作用的数值(获得或削弱)',
  `mp_cost` int(11) NOT NULL DEFAULT 0 COMMENT 'MP消耗',
  `is_continue` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否持续性',
  `continue_round` int(11) NOT NULL DEFAULT 0 COMMENT '持续回合数',
  `cd` int(11) NOT NULL DEFAULT 0 COMMENT '行为冷却回合数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '行为信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of action_info
-- ----------------------------
INSERT INTO `action_info` VALUES (1, 1, 'ATTACK', '基础攻击', '根据基础攻击力造成伤害，无消耗', 0, 0, 0, 0, 0, 0, 0, 0, '2026-04-07 10:49:01', '2026-04-07 10:51:33');
INSERT INTO `action_info` VALUES (2, 2, 'DEFENSE', '基础防御', '获得3点伤害格挡，一回合有效', 0, 0, 0, 3, 0, 0, 0, 0, '2026-04-07 10:50:39', '2026-04-07 10:51:39');
INSERT INTO `action_info` VALUES (3, 3, 'HEAL', '基础治疗', '消耗2点MP，回复4点HP，冷却1回合', 1, 4, 0, 0, 2, 0, 0, 1, '2026-04-07 10:53:27', '2026-04-07 10:53:27');
INSERT INTO `action_info` VALUES (4, 4, 'SPELL', '基础魔法', '消耗2点MP，对对方造成6点伤害，冷却2回合', 0, 6, 0, 0, 2, 0, 0, 2, '2026-04-07 10:54:21', '2026-04-07 10:57:01');
INSERT INTO `action_info` VALUES (5, 5, 'BUFF', '基础持续伤害', '消耗2点MP，对方每回合受到2点伤害，持续4回合，冷却4回合', 0, 2, 0, 0, 2, 1, 4, 4, '2026-04-07 10:56:51', '2026-04-07 10:57:26');
INSERT INTO `action_info` VALUES (6, 6, 'ATTACK', '史莱姆攻击', '对对方造成1点的固定伤害', 0, 1, 0, 0, 0, 0, 0, 0, '2026-04-07 10:59:32', '2026-04-07 10:59:32');

-- ----------------------------
-- Table structure for battle_info
-- ----------------------------
DROP TABLE IF EXISTS `battle_info`;
CREATE TABLE `battle_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID(战斗ID)',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户ID',
  `monster_id` bigint(20) UNSIGNED NOT NULL COMMENT '魔物ID',
  `user_current_hp` int(11) NOT NULL DEFAULT 0 COMMENT '用户当前HP',
  `user_current_mp` int(11) NOT NULL DEFAULT 0 COMMENT '用户当前MP',
  `user_current_defend` int(11) NOT NULL DEFAULT 0 COMMENT '用户当前格挡值',
  `monster_current_hp` int(11) NOT NULL DEFAULT 0 COMMENT '魔物当前HP',
  `monster_current_mp` int(11) NOT NULL DEFAULT 0 COMMENT '魔物当前MP',
  `monster_current_defend` int(11) NOT NULL DEFAULT 0 COMMENT '魔物当前格挡值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '战斗信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of battle_info
-- ----------------------------

-- ----------------------------
-- Table structure for level_info
-- ----------------------------
DROP TABLE IF EXISTS `level_info`;
CREATE TABLE `level_info`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `level` int(11) NOT NULL COMMENT '等级',
  `needed_exp` bigint(20) NOT NULL COMMENT '至下一级所需经验',
  `max_hp` int(11) NOT NULL COMMENT '生命值上限',
  `max_mp` int(11) NOT NULL COMMENT '法力值上限',
  `attack_base` int(11) NOT NULL COMMENT '基础攻击力',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `level`(`level` ASC) USING BTREE,
  INDEX `idx_level`(`level` ASC) USING BTREE COMMENT '等级索引'
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '等级信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of level_info
-- ----------------------------
INSERT INTO `level_info` VALUES (1, 1, 10, 6, 4, 1, '2026-03-26 14:19:38', '2026-04-07 09:59:32');
INSERT INTO `level_info` VALUES (2, 2, 20, 8, 5, 1, '2026-03-27 16:05:35', '2026-03-27 16:05:35');
INSERT INTO `level_info` VALUES (3, 3, 40, 10, 5, 2, '2026-03-27 16:06:19', '2026-03-27 16:06:19');
INSERT INTO `level_info` VALUES (4, 4, 60, 12, 6, 3, '2026-03-27 16:07:19', '2026-03-27 16:07:19');
INSERT INTO `level_info` VALUES (5, 5, 80, 14, 7, 4, '2026-03-27 16:07:52', '2026-03-27 16:07:52');
INSERT INTO `level_info` VALUES (6, 6, 120, 16, 8, 4, '2026-03-27 16:08:12', '2026-03-27 16:08:12');
INSERT INTO `level_info` VALUES (7, 7, 140, 20, 10, 5, '2026-03-27 16:08:38', '2026-03-27 16:08:38');
INSERT INTO `level_info` VALUES (8, 8, 160, 22, 11, 5, '2026-03-27 16:09:05', '2026-03-27 16:09:05');
INSERT INTO `level_info` VALUES (9, 9, 180, 24, 12, 5, '2026-03-27 16:09:47', '2026-03-27 16:09:47');
INSERT INTO `level_info` VALUES (10, 10, 240, 26, 13, 5, '2026-03-27 16:10:19', '2026-03-27 16:10:19');
INSERT INTO `level_info` VALUES (11, 11, 260, 30, 15, 6, '2026-03-27 16:10:41', '2026-03-27 16:10:41');

-- ----------------------------
-- Table structure for monster_action_info
-- ----------------------------
DROP TABLE IF EXISTS `monster_action_info`;
CREATE TABLE `monster_action_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `monster_id` bigint(20) UNSIGNED NOT NULL COMMENT '魔物ID',
  `action_id` bigint(20) UNSIGNED NOT NULL COMMENT '行为ID',
  `current_cd` int(11) NOT NULL DEFAULT 0 COMMENT '该行为当前冷却时间',
  `rest_continue_round` int(11) NOT NULL DEFAULT 0 COMMENT '剩余持续回合数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '魔物行为关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of monster_action_info
-- ----------------------------
INSERT INTO `monster_action_info` VALUES (1, 1, 6, 0, 0, '2026-04-07 10:58:03', '2026-04-07 10:59:46');

-- ----------------------------
-- Table structure for monster_info
-- ----------------------------
DROP TABLE IF EXISTS `monster_info`;
CREATE TABLE `monster_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `monster_id` bigint(20) UNSIGNED NOT NULL COMMENT '魔物ID',
  `monster_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '魔物名',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '描述',
  `hp` int(11) NOT NULL DEFAULT 6 COMMENT '魔物最大生命值',
  `mp` int(11) NOT NULL DEFAULT 4 COMMENT '魔物最大法力值',
  `attack_base` int(11) NOT NULL COMMENT '基础攻击力',
  `gain_exp` bigint(20) NOT NULL DEFAULT 0 COMMENT '击败后所得经验',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `monster_id`(`monster_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '魔物信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of monster_info
-- ----------------------------
INSERT INTO `monster_info` VALUES (1, 1, '史莱姆', '最弱小的魔物', 3, 0, 1, 5, '2026-04-07 10:46:47', '2026-04-07 10:46:47');

-- ----------------------------
-- Table structure for save_info
-- ----------------------------
DROP TABLE IF EXISTS `save_info`;
CREATE TABLE `save_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID(存档ID)',
  `user_id` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户ID',
  `level` int(11) NOT NULL DEFAULT 1 COMMENT '等级',
  `exp` int(11) NOT NULL DEFAULT 0 COMMENT '经验',
  `floor` int(10) UNSIGNED NOT NULL DEFAULT 1 COMMENT '所在层数',
  `progress` int(11) NOT NULL DEFAULT 0 COMMENT '层数进度',
  `monster_id` bigint(20) UNSIGNED NOT NULL COMMENT '当前对战的魔物ID',
  `is_active` tinyint(4) NOT NULL DEFAULT 0 COMMENT '激活状态(1激活或0未激活)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '游戏进程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of save_info
-- ----------------------------

-- ----------------------------
-- Table structure for tower_floor_info
-- ----------------------------
DROP TABLE IF EXISTS `tower_floor_info`;
CREATE TABLE `tower_floor_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `floor` int(10) UNSIGNED NOT NULL COMMENT '层数',
  `progress_needed` int(10) UNSIGNED NOT NULL COMMENT '下一层所需进度',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `floor`(`floor` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '魔塔-楼层配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tower_floor_info
-- ----------------------------
INSERT INTO `tower_floor_info` VALUES (1, 1, 10, '2026-04-07 10:45:38', '2026-04-07 10:45:38');
INSERT INTO `tower_floor_info` VALUES (2, 2, 15, '2026-04-07 10:47:47', '2026-04-07 10:47:47');

-- ----------------------------
-- Table structure for tower_floor_monster_info
-- ----------------------------
DROP TABLE IF EXISTS `tower_floor_monster_info`;
CREATE TABLE `tower_floor_monster_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `floor` int(10) UNSIGNED NOT NULL COMMENT '层数',
  `monster_id` bigint(20) UNSIGNED NOT NULL COMMENT '魔物ID',
  `battle_order` int(10) UNSIGNED NOT NULL COMMENT '遇敌顺序',
  `reward_progress` int(11) NOT NULL DEFAULT 0 COMMENT '击败后获得的层数进度(0-100)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_floor_monster_order`(`floor` ASC, `monster_id` ASC, `battle_order` ASC) USING BTREE COMMENT '楼层+怪物+顺序唯一索引，防止重复配置'
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '魔塔-楼层魔物配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tower_floor_monster_info
-- ----------------------------
INSERT INTO `tower_floor_monster_info` VALUES (1, 1, 1, 1, 3, '2026-04-07 10:47:06', '2026-04-07 10:47:21');
INSERT INTO `tower_floor_monster_info` VALUES (2, 1, 1, 2, 3, '2026-04-07 10:47:29', '2026-04-07 10:47:29');
INSERT INTO `tower_floor_monster_info` VALUES (3, 1, 1, 3, 4, '2026-04-07 10:47:37', '2026-04-07 10:47:37');

-- ----------------------------
-- Table structure for user_action_info
-- ----------------------------
DROP TABLE IF EXISTS `user_action_info`;
CREATE TABLE `user_action_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户ID',
  `action_id` bigint(20) UNSIGNED NOT NULL COMMENT '行为ID',
  `current_cd` int(11) NOT NULL DEFAULT 0 COMMENT '该行为当前冷却时间',
  `rest_continue_round` int(11) NOT NULL DEFAULT 0 COMMENT '剩余持续回合数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '按创建时间查询索引'
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户行为关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_action_info
-- ----------------------------
INSERT INTO `user_action_info` VALUES (1, 2, 1, 0, 0, '2026-04-07 11:37:12', '2026-04-07 11:37:12');
INSERT INTO `user_action_info` VALUES (2, 2, 2, 0, 0, '2026-04-07 11:37:12', '2026-04-07 11:37:12');
INSERT INTO `user_action_info` VALUES (3, 2, 3, 0, 0, '2026-04-07 11:37:12', '2026-04-07 11:37:12');
INSERT INTO `user_action_info` VALUES (4, 2, 4, 0, 0, '2026-04-07 11:37:12', '2026-04-07 11:37:12');
INSERT INTO `user_action_info` VALUES (5, 2, 5, 0, 0, '2026-04-07 11:37:12', '2026-04-07 11:37:12');
INSERT INTO `user_action_info` VALUES (6, 1, 1, 0, 0, '2026-04-07 11:37:52', '2026-04-07 11:37:52');
INSERT INTO `user_action_info` VALUES (7, 1, 2, 0, 0, '2026-04-07 11:37:52', '2026-04-07 11:37:52');
INSERT INTO `user_action_info` VALUES (8, 1, 3, 0, 0, '2026-04-07 11:37:52', '2026-04-07 11:37:52');
INSERT INTO `user_action_info` VALUES (9, 1, 4, 0, 0, '2026-04-07 11:37:52', '2026-04-07 11:37:52');
INSERT INTO `user_action_info` VALUES (10, 1, 5, 0, 0, '2026-04-07 11:37:52', '2026-04-07 11:37:52');

-- ----------------------------
-- Table structure for user_detail
-- ----------------------------
DROP TABLE IF EXISTS `user_detail`;
CREATE TABLE `user_detail`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联用户ID',
  `level` int(11) NOT NULL DEFAULT 1 COMMENT '等级',
  `exp` bigint(20) NOT NULL DEFAULT 0 COMMENT '经验',
  `attack_base` int(11) NOT NULL DEFAULT 1 COMMENT '基础攻击力',
  `current_hp` int(11) NOT NULL DEFAULT 6 COMMENT '当前生命值',
  `current_mp` int(11) NOT NULL DEFAULT 4 COMMENT '当前法力值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_detail
-- ----------------------------
INSERT INTO `user_detail` VALUES (3, 2, 1, 0, 1, 6, 4, '2026-04-07 11:37:12', '2026-04-07 11:37:12');
INSERT INTO `user_detail` VALUES (4, 1, 1, 0, 1, 6, 4, '2026-04-07 11:37:52', '2026-04-07 11:37:52');

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID(用户ID)',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱(唯一)',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名(唯一)',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '按创建时间查询索引'
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (1, '123@example.com', 'xiaowang', '123456', '2026-03-26 15:02:30', '2026-03-26 15:02:30');
INSERT INTO `user_info` VALUES (2, 'error@example.com', 'error', '123456', '2026-04-07 09:54:05', '2026-04-07 09:54:05');

SET FOREIGN_KEY_CHECKS = 1;
