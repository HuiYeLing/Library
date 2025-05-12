# 图书馆座位管理系统

## 系统简介

本系统是一个基于Spring Boot开发的图书馆座位管理系统，旨在帮助图书馆实现座位的智能化管理，提高座位资源的利用率，为读者提供便捷的座位预约服务。

## 系统功能

### 用户管理
- 用户注册：新用户可以通过系统注册账号
- 用户登录：已注册用户可以通过用户名和密码登录系统
- 用户信息查询：查看用户的基本信息

### 座位管理
- 座位查询：用户可以查询所有座位、按状态查询座位、按位置查询座位
- 座位详情：查看特定座位的详细信息
- 座位状态管理：管理座位的可用状态（可用、已预约、维护中等）

### 预约管理
- 座位预约：用户可以预约指定时间段的座位
- 预约查询：用户可以查询自己的预约记录
- 预约取消：用户可以取消未使用的预约
- 预约完成：用户使用完座位后可以标记预约为已完成
- 座位可用性检查：检查特定时间段座位是否可预约

## 技术架构

### 后端技术栈
- 框架：Spring Boot
- 安全认证：Spring Security + JWT
- 数据存储：内存数据结构（实际项目中可替换为数据库）

### 安全机制
- JWT令牌认证：用户登录后获取JWT令牌，后续请求通过令牌进行身份验证
- 接口权限控制：部分接口（如登录、注册）允许匿名访问，其他接口需要认证
- 全局异常处理：统一处理系统异常，提供友好的错误信息

## API接口说明

### 用户接口
- POST `/api/users/login`：用户登录
- POST `/api/users/register`：用户注册
- GET `/api/users/{username}`：获取用户信息
- GET `/api/users`：获取所有用户（管理员功能）

### 座位接口
- GET `/api/seats`：获取所有座位
- GET `/api/seats/{seatId}`：获取特定座位信息
- GET `/api/seats/status/{status}`：按状态查询座位
- GET `/api/seats/location/{location}`：按位置查询座位
- POST `/api/seats`：添加新座位（管理员功能）
- PUT `/api/seats/{seatId}`：更新座位信息（管理员功能）
- DELETE `/api/seats/{seatId}`：删除座位（管理员功能）

### 预约接口
- GET `/api/reservations`：获取所有预约记录（管理员功能）
- GET `/api/reservations/{reservationId}`：获取特定预约详情
- GET `/api/reservations/user/{username}`：获取用户的预约记录
- POST `/api/reservations`：创建新预约
- DELETE `/api/reservations/{reservationId}`：取消预约
- PUT `/api/reservations/{reservationId}/complete`：完成预约
- GET `/api/reservations/check-availability`：检查座位在特定时间段是否可用

## 使用说明

### 系统启动
1. 确保已安装Java 8或更高版本
2. 使用Maven构建项目：`mvn clean package`
3. 运行生成的JAR文件：`java -jar target/library-0.0.1-SNAPSHOT.jar`
4. 系统默认在8080端口启动

### 接口调用示例

#### 用户登录
```
POST /api/users/login?username=student1&password=password
```
响应：
```json
{
  "message": "登录成功",
  "username": "student1",
  "name": "张三",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### 预约座位
```
POST /api/reservations?seatId=1&username=student1&startTime=2023-06-01T09:00:00&endTime=2023-06-01T12:00:00
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 注意事项

1. 本系统目前使用内存数据结构存储数据，系统重启后数据将丢失
2. 实际生产环境中，建议将存储机制替换为数据库
3. 密码目前采用明文存储，生产环境中应使用加密存储
4. JWT密钥应在生产环境中更换为强密钥并妥善保管

## 未来计划

1. 添加用户角色管理，区分普通用户和管理员权限
2. 集成数据库存储数据
3. 开发前端界面，提供完整的用户交互体验
4. 添加座位使用统计和分析功能
5. 实现邮件或短信通知功能