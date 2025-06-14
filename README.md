# 自习室管理与小卖部系统

## 项目简介

本项目是一个基于 Spring Boot + Vue3 的自习室智能管理系统，集成了自习座位预约、用户管理、商品小卖部、订单管理等功能，适用于高校或公共自习室的数字化管理场景。系统采用前后端分离架构，支持多角色权限，界面简洁，交互友好。

---

## 功能模块

### 1. 用户管理

- 用户注册/登录：支持新用户注册、已有用户登录，采用 JWT 令牌认证。
- 用户信息管理：用户可查看和修改个人信息，管理员可管理所有用户。
- 权限控制：区分普通用户和管理员，部分功能仅管理员可用。

### 2. 自习座位管理

- 座位查询：用户可查看所有自习座位的状态（空闲、已预约、维护中等）。
- 座位预约：用户可预约指定时间段的座位，支持预约冲突检测。
- 座位状态管理：管理员可新增、修改、删除座位，调整座位状态。
- 预约记录：用户可查询、取消自己的预约，管理员可查看所有预约。

### 3. 商品小卖部

- 商品浏览：用户可浏览自习室内小卖部的所有商品及库存。
- 商品购买：用户可直接购买商品，系统自动生成订单并扣减库存。
- 商品管理：管理员可添加、编辑、删除商品，调整库存和价格。

### 4. 订单管理

- 订单查询：用户可查看自己的订单，管理员可查看所有订单。
- 订单发货：管理员可将订单状态从“待送”变为“已送达”。
- 订单删除：管理员可删除订单。

---

## 技术架构

### 前端

- 框架：Vue3 + Vite
- UI：Element Plus（或自定义样式）
- 路由：Vue Router
- HTTP请求：Axios

### 后端

- 框架：Spring Boot
- 安全认证：Spring Security + JWT
- 数据库：MySQL（可扩展为其他数据库）
- ORM：MyBatis 或 JPA
- 文件上传：支持商品图片等静态资源上传

### 其它

- 接口文档：详见 `接口文档.md`
- 示例 SQL 脚本：见 `sql/` 目录
- 静态资源：商品图片等见 `uploads/` 目录

---

## 主要数据结构

### 用户（User）

| 字段     | 类型   | 说明               |
| -------- | ------ | ------------------ |
| id       | Long   | 用户ID             |
| username | String | 用户名             |
| password | String | 密码（加密）       |
| email    | String | 邮箱               |
| role     | String | 角色（user/admin） |

### 座位（Seat）

| 字段     | 类型   | 说明                         |
| -------- | ------ | ---------------------------- |
| id       | Long   | 座位ID                       |
| status   | String | 状态（空闲、已预约、维护中） |
| location | String | 位置描述                     |

### 预约（Reservation）

| 字段      | 类型     | 说明                           |
| --------- | -------- | ------------------------------ |
| id        | Long     | 预约ID                         |
| userId    | Long     | 用户ID                         |
| seatId    | Long     | 座位ID                         |
| startTime | DateTime | 开始时间                       |
| endTime   | DateTime | 结束时间                       |
| status    | String   | 状态（已预约、已完成、已取消） |

### 商品（Product）

| 字段     | 类型    | 说明     |
| -------- | ------- | -------- |
| id       | Long    | 商品ID   |
| name     | String  | 商品名称 |
| price    | Double  | 商品价格 |
| stock    | Integer | 库存     |
| imageUrl | String  | 图片路径 |

### 订单（Order）

| 字段        | 类型     | 说明                   |
| ----------- | -------- | ---------------------- |
| id          | Long     | 订单ID                 |
| userId      | Long     | 用户ID                 |
| username    | String   | 用户名                 |
| productId   | Long     | 商品ID                 |
| productName | String   | 商品名称               |
| totalPrice  | Double   | 总价                   |
| createTime  | DateTime | 下单时间               |
| status      | Integer  | 状态（0待送，1已送达） |

---
