# 校园微墙后端服务

## 技术栈

- Java 21
- Spring Boot 3.2.x
- MyBatis Plus 3.5.x
- MySQL 8.0+
- JWT
- Knife4j

## 项目结构

```
backend/
├── src/main/java/com/example/schoolwall/
│   ├── controller/     # REST API控制层
│   ├── service/        # 业务逻辑层
│   │   └── impl/       # 实现类
│   ├── mapper/         # 数据访问层
│   ├── entity/         # 数据库实体
│   ├── dto/            # 数据传输对象
│   ├── config/         # 配置类
│   ├── security/       # 安全相关
│   ├── exception/      # 异常处理
│   ├── common/         # 通用工具
│   └── SchoolWallApplication.java
├── src/main/resources/
│   ├── application.yml # 应用配置
│   └── mapper/         # MyBatis XML
└── pom.xml
```

## 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.0+

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE school_wall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行 `DATABASE.sql` 初始化表结构

3. 修改 `application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/school_wall
    username: your_username
    password: your_password
```

### 3. 启动服务

```bash
cd backend
mvn spring-boot:run
```

### 4. 访问接口文档

启动后访问：`http://localhost:8080/doc.html`

## API接口

| 模块 | 路径 | 说明 |
| :--- | :--- | :--- |
| 用户 | `/api/user/*` | 用户登录、信息管理 |
| 帖子 | `/api/posts/*` | 帖子CRUD、点赞收藏 |
| 评论 | `/api/comments/*` | 评论管理 |
| 搜索 | `/api/search` | 搜索用户和帖子 |
| 消息 | `/api/messages/*` | 私信聊天 |

## 配置说明

### JWT配置

```yaml
jwt:
  secret: school-wall-secret-key-2024-may-be-here-and-long-enough
  expiration: 604800000  # 7天（毫秒）
```

### 日志配置

日志级别可在 `application.yml` 中调整：
```yaml
logging:
  level:
    root: INFO
    com.example.schoolwall: DEBUG
```
