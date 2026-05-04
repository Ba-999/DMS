# DMS 宿舍管理系统

基于 Spring Boot + Vue 2 的前后端分离宿舍管理平台，支持楼宇管理、宿舍分配、学生入住/退宿、报修管理、数据统计。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 2.7 |
| ORM | MyBatis-Plus 3.5 |
| 数据库 | MySQL 8.0 |
| 前端框架 | Vue 2 + Element UI + Vite |
| 构建工具 | Maven |

## 功能模块

- 用户认证：管理员/学生双角色登录，Token 鉴权
- 楼宇管理：楼宇的增删改查
- 宿舍管理：房间的增删改查，已住人数实时更新
- 学生管理：学生信息维护，入住/未入住状态标记
- 入住管理：学生入住登记、退宿办理（事务保证三表一致性）
- 换宿申请：学生申请换宿，管理员审批
- 报修管理：学生报修 → 待处理 → 维修中 → 已完成，状态流转
- 数据概览：首页统计楼宇数、宿舍数、入住数、待处理报修数

## 快速开始

### 1. 环境要求

- JDK 8+
- MySQL 8.0+
- Maven 3.6+（或用项目自带的 mvnw）
- Node.js 16+（前端）

### 2. 创建数据库

在 MySQL 中执行初始化脚本：

```
mysql -u root -p < sql/init.sql
```

脚本会自动创建数据库 `dormitory_db`、所有表结构和测试数据。

### 3. 配置数据库连接

设置数据库连接信息（默认连接本地 MySQL）：

```bash
# Windows (cmd)
set DB_PASSWORD=你的密码

# Linux / Mac
export DB_PASSWORD=你的密码
```

完整可配置项及默认值：

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| DB_HOST | localhost | 数据库地址 |
| DB_PORT | 3306 | 数据库端口 |
| DB_NAME | dormitory_db | 数据库名 |
| DB_USERNAME | root | 数据库账号 |
| DB_PASSWORD | root | 数据库密码 |

### 4. 启动后端

```bash
# Windows
mvnw spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

后端启动后访问: http://localhost:8080/api

### 5. 启动前端（可选）

**桌面端访问：**

```bash
cd frontend
npm install
npm run dev
```

访问: http://localhost:5173

**移动端访问（手机和电脑在同一 WiFi 下）：**

```bash
cd frontend
npm install
npm run dev:host
```

启动后会显示局域网地址，例如 `http://192.168.1.100:5173`，手机浏览器输入该地址即可访问。

> 项目已做响应式适配，手机端侧边栏会自动转为抽屉菜单，表格可横向滑动。

### 6. 测试账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | 123456 |
| 宿管 | sg_zhang | 123456 |
| 学生 | 2024001 | 123456 |
| 学生 | 2024002 | 123456 |

## 项目结构

```
DMS
├── sql/
│   └── init.sql              # 数据库初始化脚本
├── src/main/java/.../dms/
│   ├── common/               # 统一响应、全局异常处理
│   ├── config/               # MyBatis-Plus配置、CORS、数据初始化
│   ├── controller/           # RESTful 接口层
│   ├── entity/               # 数据库实体
│   ├── interceptor/          # 登录拦截器
│   ├── mapper/               # MyBatis Mapper 接口
│   └── service/              # 业务逻辑层
├── src/main/resources/
│   └── application.yml       # 主配置文件
└── frontend/                 # Vue 2 前端工程
```
