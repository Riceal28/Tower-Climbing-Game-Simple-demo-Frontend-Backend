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

 Date: 21/04/2026 19:03:37
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
  `is_target_player` tinyint(4) NOT NULL DEFAULT 0 COMMENT '作用对象是否玩家,0魔物,1玩家',
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
INSERT INTO `action_info` VALUES (2, 2, 'DEFENSE', '基础防御', '获得3点伤害格挡（仅对ATTACK类攻击有效），一回合有效', 1, 0, 0, 3, 0, 1, 1, 0, '2026-04-07 10:50:39', '2026-04-14 11:36:54');
INSERT INTO `action_info` VALUES (3, 3, 'HEAL', '基础治疗', '消耗2点MP，回复4点HP，冷却1回合', 1, 4, 0, 0, 2, 0, 0, 1, '2026-04-07 10:53:27', '2026-04-07 10:53:27');
INSERT INTO `action_info` VALUES (4, 4, 'SPELL', '基础魔法', '消耗2点MP，对对方造成6点伤害，冷却2回合', 0, 6, 0, 0, 2, 0, 0, 2, '2026-04-07 10:54:21', '2026-04-07 10:57:01');
INSERT INTO `action_info` VALUES (5, 5, 'BUFF', '基础持续伤害', '消耗2点MP，对方每回合受到2点伤害，持续4回合，冷却4回合', 0, 2, 0, 0, 2, 1, 4, 4, '2026-04-07 10:56:51', '2026-04-07 10:57:26');
INSERT INTO `action_info` VALUES (6, 6, 'ATTACK', '史莱姆攻击', '对对方造成1点的固定伤害', 0, -1, 0, 0, 0, 0, 0, 0, '2026-04-07 10:59:32', '2026-04-20 14:59:55');

-- ----------------------------
-- Table structure for battle_info
-- ----------------------------
DROP TABLE IF EXISTS `battle_info`;
CREATE TABLE `battle_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID(战斗ID)',
  `save_id` bigint(20) UNSIGNED NOT NULL COMMENT '存档ID',
  `monster_id` bigint(20) UNSIGNED NOT NULL COMMENT '魔物ID',
  `player_current_hp` int(11) NOT NULL DEFAULT 0 COMMENT '角色当前HP',
  `player_current_mp` int(11) NOT NULL DEFAULT 0 COMMENT '角色当前MP',
  `player_current_defend` int(11) NOT NULL DEFAULT 0 COMMENT '角色当前格挡值',
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
  `player_class` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'SABER' COMMENT '职阶:SABER/ARCHER/CASTER',
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '等级信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of level_info
-- ----------------------------

-- ----------------------------
-- Table structure for monster_action_info
-- ----------------------------
DROP TABLE IF EXISTS `monster_action_info`;
CREATE TABLE `monster_action_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `battle_id` bigint(20) UNSIGNED NOT NULL COMMENT '对应的战斗ID',
  `monster_id` bigint(20) UNSIGNED NOT NULL COMMENT '魔物ID',
  `action_id` bigint(20) UNSIGNED NOT NULL COMMENT '行为ID',
  `current_cd` int(11) NOT NULL DEFAULT 0 COMMENT '该行为当前冷却时间',
  `rest_continue_round` int(11) NOT NULL DEFAULT 0 COMMENT '剩余持续回合数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '魔物行为关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of monster_action_info
-- ----------------------------

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
-- Table structure for player_action_info
-- ----------------------------
DROP TABLE IF EXISTS `player_action_info`;
CREATE TABLE `player_action_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `battle_id` bigint(20) UNSIGNED NOT NULL COMMENT '对应的战斗ID',
  `player_id` bigint(20) UNSIGNED NOT NULL COMMENT '角色ID',
  `action_id` bigint(20) UNSIGNED NOT NULL COMMENT '技能ID',
  `current_cd` int(11) NOT NULL DEFAULT 0 COMMENT '该行为当前冷却时间',
  `rest_continue_round` int(11) NOT NULL DEFAULT 0 COMMENT '剩余持续回合数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色技能关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of player_action_info
-- ----------------------------

-- ----------------------------
-- Table structure for save_info
-- ----------------------------
DROP TABLE IF EXISTS `save_info`;
CREATE TABLE `save_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID(存档ID)',
  `user_id` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户ID',
  `player_id` bigint(20) UNSIGNED NOT NULL COMMENT '角色ID',
  `level` int(11) NOT NULL DEFAULT 1 COMMENT '等级',
  `exp` bigint(20) NOT NULL DEFAULT 0 COMMENT '经验',
  `current_hp` int(11) NOT NULL DEFAULT 6 COMMENT '当前生命值',
  `current_mp` int(11) NOT NULL DEFAULT 4 COMMENT '当前法力值',
  `floor` int(10) UNSIGNED NOT NULL DEFAULT 1 COMMENT '所在层数',
  `battle_order` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '当前遇敌顺序序号',
  `progress` int(11) NOT NULL DEFAULT 0 COMMENT '层数进度',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '游戏存档表' ROW_FORMAT = Dynamic;

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
  `battle_order` int(10) UNSIGNED NOT NULL COMMENT '遇敌顺序',
  `monster_id` bigint(20) UNSIGNED NOT NULL COMMENT '魔物ID',
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
INSERT INTO `tower_floor_monster_info` VALUES (2, 1, 2, 1, 3, '2026-04-07 10:47:29', '2026-04-07 10:47:29');
INSERT INTO `tower_floor_monster_info` VALUES (3, 1, 3, 1, 4, '2026-04-07 10:47:37', '2026-04-07 10:47:37');

-- ----------------------------
-- Table structure for user_action_info
-- ----------------------------
DROP TABLE IF EXISTS `user_action_info`;
CREATE TABLE `user_action_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `battle_id` bigint(20) UNSIGNED NOT NULL COMMENT '对应的战斗ID',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户ID',
  `action_id` bigint(20) UNSIGNED NOT NULL COMMENT '行为ID',
  `current_cd` int(11) NOT NULL DEFAULT 0 COMMENT '该行为当前冷却时间',
  `rest_continue_round` int(11) NOT NULL DEFAULT 0 COMMENT '剩余持续回合数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '按创建时间查询索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户行为关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_action_info
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (1, '123@123456.com', 'xiaowang', '123456', '2026-04-21 19:01:39', '2026-04-21 19:01:39');

-- ----------------------------
-- Table structure for user_palyer_info
-- ----------------------------
DROP TABLE IF EXISTS `user_palyer_info`;
CREATE TABLE `user_palyer_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID(角色ID)',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联用户ID',
  `player_class` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'SABER' COMMENT '职阶:SABER/ARCHER/CASTER',
  `level` int(11) NOT NULL DEFAULT 1 COMMENT '等级',
  `exp` bigint(20) NOT NULL DEFAULT 0 COMMENT '经验',
  `attack_base` int(11) NOT NULL DEFAULT 1 COMMENT '基础攻击力',
  `current_hp` int(11) NOT NULL DEFAULT 6 COMMENT '当前生命值',
  `current_mp` int(11) NOT NULL DEFAULT 4 COMMENT '当前法力值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_palyer_info
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
