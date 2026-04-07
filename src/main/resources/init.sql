-- 用户信息表, 用于登录注册
CREATE TABLE IF NOT EXISTS user_info
(
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID(用户ID)',
    email       VARCHAR(50)  NOT NULL UNIQUE COMMENT '邮箱(唯一)',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名(唯一)',
    password    VARCHAR(255) NOT NULL COMMENT '密码',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX       idx_create_time (create_time) COMMENT '按创建时间查询索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户信息表';

-- 用户详情表, 用户展示详细信息(状态)
CREATE TABLE IF NOT EXISTS user_detail
(
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id     BIGINT UNSIGNED NOT NULL COMMENT '关联用户ID',
    level       INT      NOT NULL DEFAULT 1 COMMENT '等级',
    exp         BIGINT   NOT NULL DEFAULT 0 COMMENT '经验',
    attack_base INT      NOT NULL DEFAULT 1 COMMENT '基础攻击力',
    current_hp  INT      NOT NULL DEFAULT 6 COMMENT '当前生命值',
    current_mp  INT      NOT NULL DEFAULT 4 COMMENT '当前法力值',
--     money       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '金币(暂不做开发)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX       idx_user_id (user_id) COMMENT '用户ID索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户详情表';

-- 等级信息表,
CREATE TABLE IF NOT EXISTS level_info
(
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    level       INT UNIQUE NOT NULL COMMENT '等级',
    needed_exp  BIGINT     NOT NULL COMMENT '至下一级所需经验',
    max_hp      INT        NOT NULL COMMENT '生命值上限',
    max_mp      INT        NOT NULL COMMENT '法力值上限',
    attack_base INT        NOT NULL COMMENT '基础攻击力',
    create_time DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX       idx_level (level) COMMENT '等级索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '等级信息表';

-- 游戏进程表(非即时,仅存储战斗开始时的状态), 用于保存用户进度
CREATE TABLE IF NOT EXISTS save_info
(
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID(存档ID)',
    user_id     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户ID',
    level       INT      NOT NULL DEFAULT 1 COMMENT '等级',
    exp         INT      NOT NULL DEFAULT 0 COMMENT '经验',
--     current_hp  INT      NOT NULL DEFAULT 6 COMMENT '当前生命值',
--     current_mp  INT      NOT NULL DEFAULT 4 COMMENT '当前法力值',
--     money       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '金币',
    floor       INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '所在层数',
    progress    INT      NOT NULL DEFAULT 0 COMMENT '层数进度',
    monster_id  BIGINT UNSIGNED NOT NULL COMMENT '当前对战的魔物ID',
    is_active   TINYINT  NOT NULL DEFAULT 0 COMMENT '激活状态(1激活或0未激活)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX       idx_user_id (user_id) COMMENT '用户ID索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '游戏进程表';

-- 战斗信息表, 展示单场战斗中的信息
CREATE TABLE IF NOT EXISTS battle_info
(
    id                     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID(战斗ID)',
    user_id                BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    monster_id             BIGINT UNSIGNED NOT NULL COMMENT '魔物ID',
    user_current_hp        INT      NOT NULL DEFAULT 0 COMMENT '用户当前HP',
    user_current_mp        INT      NOT NULL DEFAULT 0 COMMENT '用户当前MP',
    user_current_defend    INT      NOT NULL DEFAULT 0 COMMENT '用户当前格挡值',
    monster_current_hp     INT      NOT NULL DEFAULT 0 COMMENT '魔物当前HP',
    monster_current_mp     INT      NOT NULL DEFAULT 0 COMMENT '魔物当前MP',
    monster_current_defend INT      NOT NULL DEFAULT 0 COMMENT '魔物当前格挡值',
    create_time            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '战斗信息表';

-- 行为信息表(抽象化), 用于定义行为
CREATE TABLE IF NOT EXISTS action_info
(
    id                 BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    action_id          BIGINT UNSIGNED NOT NULL COMMENT '行为ID',
    action_type        VARCHAR(20)  NOT NULL DEFAULT 'ATTACK' COMMENT '类型: ATTACK/DEFENSE/HEAL/SPELL/BUFF',
    action_name        VARCHAR(50)  NOT NULL DEFAULT "" COMMENT '行为名',
    description        VARCHAR(255) NOT NULL DEFAULT "" COMMENT '描述',
    target_is_for_self TINYINT      NOT NULL DEFAULT 0 COMMENT '作用对象是否自身,0对方,1自身',
--     target_num         INT          NOT NULL DEFAULT 0 COMMENT '可作用对象数量(单体/AOE)',
    for_hp             INT          NOT NULL DEFAULT 0 COMMENT '对HP作用的数值(伤害或治疗)',
    for_mp             INT          NOT NULL DEFAULT 0 COMMENT '对MP作用的数值(消耗或回复)',
    for_defend         INT          NOT NULL DEFAULT 0 COMMENT '对格挡值作用的数值(获得或削弱)',
    mp_cost            INT          NOT NULL DEFAULT 0 COMMENT 'MP消耗',
    is_continue        TINYINT      NOT NULL DEFAULT 0 COMMENT '是否持续性',
    continue_round     INT          NOT NULL DEFAULT 0 COMMENT '持续回合数',
    cd                 INT          NOT NULL DEFAULT 0 COMMENT '行为冷却回合数',
    create_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '行为信息表';

-- 用户行为关联表, 用于关联用户与行为
CREATE TABLE IF NOT EXISTS user_action_info
(
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id             BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    action_id           BIGINT UNSIGNED NOT NULL COMMENT '行为ID',
    current_cd          INT      NOT NULL DEFAULT 0 COMMENT '该行为当前冷却时间',
    rest_continue_round INT      NOT NULL DEFAULT 0 COMMENT '剩余持续回合数',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX               idx_user_id (user_id) COMMENT '按创建时间查询索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户行为关联表';

-- 魔物信息表, 用于定义行为
CREATE TABLE IF NOT EXISTS monster_info
(
    id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    monster_id   BIGINT UNSIGNED NOT NULL UNIQUE COMMENT '魔物ID',
--     monster_type ENUM('normal', 'elite', 'boss', 'event') NOT NULL COMMENT '魔物类别',
    monster_name VARCHAR(50)  NOT NULL DEFAULT "" COMMENT '魔物名',
    description  VARCHAR(255) NOT NULL DEFAULT "" COMMENT '描述',
    hp           INT          NOT NULL DEFAULT 6 COMMENT '魔物最大生命值',
    mp           INT          NOT NULL DEFAULT 4 COMMENT '魔物最大法力值',
    attack_base  INT          NOT NULL COMMENT '基础攻击力',
    gain_exp     BIGINT       NOT NULL DEFAULT 0 COMMENT '击败后所得经验',
--     gain_money   BIGINT       NOT NULL DEFAULT 0 COMMENT '击败后所得金币(暂不开发)',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '魔物信息表';

-- 魔物行为关联表, 用于关联魔物的行为组
CREATE TABLE IF NOT EXISTS monster_action_info
(
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    monster_id          BIGINT UNSIGNED NOT NULL COMMENT '魔物ID',
    action_id           BIGINT UNSIGNED NOT NULL COMMENT '行为ID',
    current_cd          INT      NOT NULL DEFAULT 0 COMMENT '该行为当前冷却时间',
    rest_continue_round INT      NOT NULL DEFAULT 0 COMMENT '剩余持续回合数',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '魔物行为关联表';

-- 塔信息表, 存储各层战斗信息
CREATE TABLE IF NOT EXISTS tower_floor_info
(
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    floor               INT UNSIGNED UNIQUE NOT NULL COMMENT '层数',
    progress_needed     INT UNSIGNED NOT NULL COMMENT '下一层所需进度',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COMMENT = '魔塔-楼层配置表';

-- 层魔物关联表, 关联塔中每层的遇战信息
CREATE TABLE IF NOT EXISTS tower_floor_monster_info
(
    id                  BIGINT  UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    floor               INT     UNSIGNED NOT NULL COMMENT '层数',
    monster_id          BIGINT  UNSIGNED NOT NULL COMMENT '魔物ID',
    battle_order        INT     UNSIGNED NOT NULL COMMENT '遇敌顺序',
    reward_progress     INT      NOT NULL DEFAULT 0 COMMENT '击败后获得的层数进度(0-100)',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY          uk_floor_monster_order (floor, monster_id, battle_order) COMMENT '楼层+怪物+顺序唯一索引，防止重复配置'
) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COMMENT = '魔塔-楼层魔物配置表';

-- 以下部分暂不开发
-- DROP TABLE IF EXISTS item_info
-- -- 道具信息表, 用于定义道具
-- CREATE TABLE item_info
-- (
--     id                 BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
--     item_id            BIGINT UNSIGNED NOT NULL COMMENT '道具ID,用于分组隔离,10000~20000',
--     item_name          VARCHAR(50) NOT NULL DEFAULT "" COMMENT '道具名',
--     target_is_for_self TINYINT     NOT NULL DEFAULT 0 COMMENT '作用对象是否自身,0对方,1自身',
--     target_num         INT         NOT NULL DEFAULT 0 COMMENT '可作用对象数量',
--     for_hp             INT         NOT NULL DEFAULT 6 COMMENT '对HP的影响',
--     for_mp             INT         NOT NULL DEFAULT 4 COMMENT '对MP的影响',
--     for_exp            BIGINT      NOT NULL DEFAULT 0 COMMENT '对经验的影响',
--     price              BIGINT      NOT NULL DEFAULT 0 COMMENT '价值',
--     create_time        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--     update_time        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
-- ) ENGINE = InnoDB
--   DEFAULT CHARSET = utf8mb4
--   COMMENT = '道具信息表';
--
-- DROP TABLE IF EXISTS drop_info
-- -- 魔物掉落物关联表, 用于定义魔物可掉落物
-- CREATE TABLE drop_info
-- (
--     id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
--     item_id     BIGINT UNSIGNED NOT NULL COMMENT '对应的掉落物ID',
--     monster_id  BIGINT UNSIGNED NOT NULL COMMENT '掉落此物品的魔物ID',
--     drop_num    INT      NOT NULL DEFAULT 1 COMMENT '掉落数量',
--     rate        INT      NOT NULL DEFAULT 0 COMMENT '掉落概率,1000 = 100%',
--     create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--     update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
-- ) ENGINE = InnoDB
--   DEFAULT CHARSET = utf8mb4
--   COMMENT = '魔物掉落物关联表';

