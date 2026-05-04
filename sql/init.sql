-- ============================================
-- DMS 宿舍管理系统 - 数据库初始化脚本
-- 使用方法: 在 MySQL 中执行此文件即可
--   mysql -u root -p < sql/init.sql
-- ============================================

CREATE DATABASE IF NOT EXISTS `dormitory_db`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE `dormitory_db`;

-- ----------------------------
-- 楼宇表
-- ----------------------------
DROP TABLE IF EXISTS `bus_building`;
CREATE TABLE `bus_building` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '楼宇名称',
  `type` varchar(20) DEFAULT NULL COMMENT '类型(男生宿舍/女生宿舍)',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='楼宇表';

INSERT INTO `bus_building` VALUES
(1,'青教公寓1号楼','男生宿舍','靠近北门'),
(2,'雅苑2号楼','女生宿舍','靠近食堂'),
(3,'知行楼','男生宿舍','研究生宿舍楼');

-- ----------------------------
-- 宿舍房间表
-- ----------------------------
DROP TABLE IF EXISTS `bus_dormitory`;
CREATE TABLE `bus_dormitory` (
  `id` int NOT NULL AUTO_INCREMENT,
  `building_id` int NOT NULL COMMENT '所属楼宇ID',
  `room_number` varchar(20) NOT NULL COMMENT '房号',
  `capacity` int DEFAULT '4' COMMENT '满员人数',
  `current_number` int DEFAULT '0' COMMENT '已住人数',
  `price` decimal(10,2) DEFAULT NULL COMMENT '住宿费/学年',
  PRIMARY KEY (`id`),
  KEY `building_id` (`building_id`),
  CONSTRAINT `bus_dormitory_ibfk_1` FOREIGN KEY (`building_id`) REFERENCES `bus_building` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='宿舍房间表';

INSERT INTO `bus_dormitory` VALUES
(1,1,'101',4,1,'1200.00'),
(2,1,'102',4,1,'1200.00'),
(3,2,'201',6,1,'1000.00');

-- ----------------------------
-- 学生信息表
-- ----------------------------
DROP TABLE IF EXISTS `bus_student`;
CREATE TABLE `bus_student` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_no` varchar(20) NOT NULL COMMENT '学号',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `gender` char(1) DEFAULT NULL COMMENT '性别',
  `college` varchar(100) DEFAULT NULL COMMENT '学院',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `status` tinyint DEFAULT '0' COMMENT '0-未入住, 1-已入住',
  `password` varchar(100) DEFAULT '123456' COMMENT '登录密码',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_no` (`student_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生信息表';

INSERT INTO `bus_student` VALUES
(1,'2024001','张三','M','计算机学院','15512345678',1,'123456',NULL),
(2,'2024002','李四','M','自动化学院','15588889999',1,'123456',NULL),
(3,'2024003','王五','M','计算机学院','13344445555',0,'123456',NULL),
(4,'2024101','小红','F','经管学院','18800001111',1,'123456',NULL);

-- ----------------------------
-- 入住记录表
-- ----------------------------
DROP TABLE IF EXISTS `bus_check_in`;
CREATE TABLE `bus_check_in` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL COMMENT '学生ID',
  `dormitory_id` int NOT NULL COMMENT '宿舍ID',
  `check_in_date` date DEFAULT NULL COMMENT '入住日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  KEY `dormitory_id` (`dormitory_id`),
  CONSTRAINT `bus_check_in_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `bus_student` (`id`),
  CONSTRAINT `bus_check_in_ibfk_2` FOREIGN KEY (`dormitory_id`) REFERENCES `bus_dormitory` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='入住记录表';

INSERT INTO `bus_check_in` VALUES
(1,1,3,'2026-02-28','2026-04-18 21:07:49'),
(2,2,1,'2026-03-01','2026-04-18 21:07:49'),
(4,4,2,'2026-04-20','2026-04-20 14:27:20');

-- ----------------------------
-- 换宿申请表
-- ----------------------------
DROP TABLE IF EXISTS `bus_dorm_change`;
CREATE TABLE `bus_dorm_change` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL COMMENT '申请学生',
  `from_dormitory_id` int DEFAULT NULL COMMENT '当前宿舍',
  `to_dormitory_id` int NOT NULL COMMENT '目标宿舍',
  `reason` varchar(500) DEFAULT NULL COMMENT '申请原因',
  `status` tinyint DEFAULT '0' COMMENT '0-待审核 1-已同意 2-已拒绝',
  `handle_remark` varchar(500) DEFAULT NULL COMMENT '审批备注',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='换宿申请表';

INSERT INTO `bus_dorm_change` VALUES
(1,1,1,3,'111',1,'同意','2026-04-19 17:56:27','2026-04-19 17:57:08');

-- ----------------------------
-- 报修表
-- ----------------------------
DROP TABLE IF EXISTS `bus_repair`;
CREATE TABLE `bus_repair` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dormitory_id` int NOT NULL COMMENT '宿舍ID',
  `student_id` int NOT NULL COMMENT '申请学生ID',
  `content` text NOT NULL COMMENT '报修内容描述',
  `status` tinyint DEFAULT '0' COMMENT '0-待处理, 1-维修中, 2-已完成',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `dormitory_id` (`dormitory_id`),
  CONSTRAINT `bus_repair_ibfk_1` FOREIGN KEY (`dormitory_id`) REFERENCES `bus_dormitory` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报修表';

INSERT INTO `bus_repair` VALUES
(1,1,1,'101寝室空调不制冷，请求维修。',2,'2026-04-18 21:07:56'),
(2,3,4,'201寝室水龙头漏水。',1,'2026-04-18 21:07:56');

-- ----------------------------
-- 系统用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '姓名',
  `role` tinyint DEFAULT '1' COMMENT '0-超级管理员, 1-普通宿管',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';

INSERT INTO `sys_user` VALUES
(1,'admin','123456','系统管理员',0,'13800138000',NULL,'2026-04-18 21:07:23'),
(2,'sg_zhang','123456','张大爷',1,'13911112222',NULL,'2026-04-18 21:07:23');
